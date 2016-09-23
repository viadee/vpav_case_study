package de.viadee.bpmnAnalytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.DependencyResolutionRequiredException;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelException;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import de.viadee.bpmnAnalytics.beans.BeanMappingXmlParser;
import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.config.model.Setting;
import de.viadee.bpmnAnalytics.config.reader.ConfigReaderException;
import de.viadee.bpmnAnalytics.config.reader.XmlConfigReader;
import de.viadee.bpmnAnalytics.output.IssueOutputWriter;
import de.viadee.bpmnAnalytics.output.JsonOutputWriter;
import de.viadee.bpmnAnalytics.output.OutputWriterException;
import de.viadee.bpmnAnalytics.output.XmlOutputWriter;
import de.viadee.bpmnAnalytics.processing.BpmnModelDispatcher;
import de.viadee.bpmnAnalytics.processing.ConfigItemNotFoundException;
import de.viadee.bpmnAnalytics.processing.checker.VersioningChecker;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;

/**
 * Goal which checks bpmn files
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class BpmnCheckerMojo extends AbstractMojo {

  private Logger logger = Logger.getLogger(BpmnCheckerMojo.class);

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  public void execute() throws MojoExecutionException {

    // 1a) read config file
    final Map<String, Rule> rules;
    try {
      rules = new XmlConfigReader().read(new File(ConstantsConfig.RULESET));
    } catch (final ConfigReaderException e) {
      throw new MojoExecutionException("config file couldn't be read", e);
    }

    // 1b) read bean mappings, if available
    final Map<String, String> beanMapping = BeanMappingXmlParser
        .parse(new File(ConstantsConfig.BEAN_MAPPING));

    // 2) Scan class path for bpmn models, dmn models, java files and versioned resources
    final ClassLoader classLoader;
    final Set<String> processdefinitions;
    final Set<String> javaResources;
    final Map<String, String> decisionRefToPathMap;
    Collection<String> resourcesNewestVersions = new ArrayList<String>();
    try {
      classLoader = getClassLoader(project);
      final Reflections reflections = new Reflections(
          new ConfigurationBuilder().setScanners(new ResourcesScanner())
              .setUrls(ClasspathHelper.forClassLoader(classLoader)));

      processdefinitions = reflections
          .getResources(Pattern.compile(ConstantsConfig.BPMN_FILE_PATTERN));
      javaResources = reflections.getResources(Pattern.compile(ConstantsConfig.JAVA_FILE_PATTERN));
      decisionRefToPathMap = createDmnKeyToPathMap(
          reflections.getResources(Pattern.compile(ConstantsConfig.DMN_FILE_PATTERN)));
      final String versioningSchema = loadVersioningSchemaClass(rules);
      if (versioningSchema != null) {
        resourcesNewestVersions = createResourcesToNewestVersions(
            reflections.getResources(Pattern.compile(versioningSchema)), versioningSchema);
      }

    } catch (final MalformedURLException e) {
      throw new MojoExecutionException("wrong URL format");
    } catch (final DependencyResolutionRequiredException e) {
      throw new MojoExecutionException("classpath couldn't be resolved");
    }

    // 3) get process variables from process start
    final OuterProcessVariablesScanner scanner = new OuterProcessVariablesScanner(javaResources,
        classLoader);
    try {
      scanner.scanProcessVariables();
    } catch (final IOException e) {
      throw new MojoExecutionException(
          "outer process variables couldn't be read: " + e.getMessage());
    }

    // 4) Check each model
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    for (final String pathToModel : processdefinitions) {
      issues.addAll(checkModel(classLoader, decisionRefToPathMap, rules, beanMapping,
          scanner.getMessageIdToVariableMap(), scanner.getProcessIdToVariableMap(),
          resourcesNewestVersions, pathToModel));
    }

    // 5) remove ignored issues
    Collection<CheckerIssue> filteredIssues;
    try {
      filteredIssues = getFilteredIssues(issues);
    } catch (final IOException e) {
      throw new MojoExecutionException("ignored issues couldn't read successfully", e);
    }

    // 6) write check results to xml and json file
    final IssueOutputWriter xmlOutputWriter = new XmlOutputWriter();
    final IssueOutputWriter jsonOutputWriter = new JsonOutputWriter();
    try {
      xmlOutputWriter.write(filteredIssues);
      jsonOutputWriter.write(filteredIssues);
    } catch (final OutputWriterException ex) {
      throw new MojoExecutionException(ex.getMessage());
    }

    if (issuesWithErrors(issues))
      throw new MojoExecutionException(
          "BPMN validation with errors, see " + ConstantsConfig.VALIDATION_XML_OUTPUT);

    logger.info("BPMN validation successful completed");
  }

  private Collection<CheckerIssue> checkModel(final ClassLoader classLoader,
      final Map<String, String> decisionRefToPathMap, final Map<String, Rule> rules,
      final Map<String, String> beanMapping,
      final Map<String, Collection<String>> messageIdToVariables,
      final Map<String, Collection<String>> processIdToVariables,
      final Collection<String> resourcesNewestVersions, final String processdef)
      throws MojoExecutionException {
    Collection<CheckerIssue> modelIssues;
    try {
      modelIssues = BpmnModelDispatcher.dispatch(new File("src/main/resources/" + processdef),
          decisionRefToPathMap, beanMapping, messageIdToVariables, processIdToVariables,
          resourcesNewestVersions, rules, classLoader);
    } catch (final ConfigItemNotFoundException e) {
      throw new MojoExecutionException("config item couldn't be read", e);
    }
    return modelIssues;
  }

  /**
   * Get class loader for the maven project, which uses this plugin
   * 
   * @param project
   * @return
   * @throws DependencyResolutionRequiredException
   * @throws MalformedURLException
   */
  private static ClassLoader getClassLoader(final MavenProject project)
      throws DependencyResolutionRequiredException, MalformedURLException {
    final List<String> classPathElements = project.getRuntimeClasspathElements();
    final List<URL> classpathElementUrls = new ArrayList<URL>(classPathElements.size());
    for (final String classPathElement : classPathElements) {
      classpathElementUrls.add(new File(classPathElement).toURI().toURL());
    }
    classpathElementUrls.add(new File("src/main/java").toURI().toURL());
    return new URLClassLoader(classpathElementUrls.toArray(new URL[classpathElementUrls.size()]),
        Thread.currentThread().getContextClassLoader());
  }

  /**
   * Check, if the result contains errors
   * 
   * @return boolean (true/false)
   */
  private static boolean issuesWithErrors(final Collection<CheckerIssue> issues) {
    boolean error = false;
    final Iterator<CheckerIssue> iterator = issues.iterator();
    while (iterator.hasNext() && !error) {
      if (iterator.next().getClassification() == CriticalityEnum.ERROR) {
        error = true;
      }
    }
    return error;
  }

  /**
   * Map for getting dmn reference by key
   * 
   * @param paths
   * @return
   */
  private static Map<String, String> createDmnKeyToPathMap(final Set<String> paths) {

    final Map<String, String> keyToPathMap = new HashMap<String, String>();

    for (final String path : paths) {
      // read dmn file
      DmnModelInstance modelInstance = null;
      try {
        modelInstance = Dmn.readModelFromFile(new File("src/main/resources/" + path));
      } catch (final DmnModelException ex) {
        /* ignore dmn exception and except null value */ }
      // if dmn could read
      if (modelInstance != null) {
        // find decisions
        final Collection<Decision> decisions = modelInstance.getModelElementsByType(Decision.class);
        if (decisions != null) {
          for (final Decision decision : decisions) {
            // save path for each decision
            keyToPathMap.put(decision.getId(), path);
          }
        }
      }
    }

    return keyToPathMap;
  }

  /**
   * remove false positives from issue collection
   * 
   * @param issues
   * @return filteredIssues
   * @throws IOException
   */
  private Collection<CheckerIssue> getFilteredIssues(Collection<CheckerIssue> issues)
      throws IOException {
    final Collection<CheckerIssue> filteredIssues = new ArrayList<CheckerIssue>();
    filteredIssues.addAll(issues);

    final Collection<String> ignoredIssues = collectIgnoredIssues(ConstantsConfig.IGNORE_FILE);
    for (final CheckerIssue issue : issues) {
      if (ignoredIssues.contains(issue.getId())) {
        filteredIssues.remove(issue);
      }
    }
    return filteredIssues;
  }

  /**
   * Read issue ids, that should be ignored
   * 
   * Assumption: Each row is an issue id
   * 
   * @param filePath
   * @return issue ids
   * @throws IOException
   */
  private static Collection<String> collectIgnoredIssues(final String filePath) throws IOException {

    final Collection<String> ignoredIssues = new ArrayList<String>();

    FileReader fileReader = null;
    try {
      fileReader = new FileReader(filePath);
    } catch (final FileNotFoundException ex) {
      /* if file not found, fileReader will be null */
      /* file must not exist, because there are no ignored issues */
    }
    if (fileReader != null) {
      final BufferedReader bufferedReader = new BufferedReader(fileReader);
      String zeile = bufferedReader.readLine();
      addIgnoredIssue(ignoredIssues, zeile);
      while (zeile != null) {
        zeile = bufferedReader.readLine();
        addIgnoredIssue(ignoredIssues, zeile);
      }
      bufferedReader.close();
    }

    return ignoredIssues;
  }

  /**
   * Add ignored issue
   * 
   * @param issues
   * @param zeile
   */
  private static void addIgnoredIssue(final Collection<String> issues, final String zeile) {
    if (zeile != null && !zeile.isEmpty() && !zeile.trim().startsWith("#"))
      issues.add(zeile);
  }

  /**
   * reads versioned classes and scripts and generates a map with newest versions
   * 
   * @return Map
   */
  private static Collection<String> createResourcesToNewestVersions(
      final Set<String> versionedFiles, final String versioningSchema) {
    final Map<String, String> newestVersionsMap = new HashMap<String, String>();

    if (versionedFiles != null) {
      for (final String versionedFile : versionedFiles) {
        final Pattern pattern = Pattern.compile(versioningSchema);
        final Matcher matcher = pattern.matcher(versionedFile);
        while (matcher.find()) {
          final String resource = matcher.group(1);
          final String oldVersion = newestVersionsMap.get(resource);
          if (oldVersion != null) {
            // If smaller than 0 this version is newer
            if (oldVersion.compareTo(versionedFile) < 0) {
              newestVersionsMap.put(resource, versionedFile);
            }
          } else {
            newestVersionsMap.put(resource, versionedFile);
          }
        }
      }
    }
    return newestVersionsMap.values();
  }

  /**
   * determine versioning schema for an active versioning checker
   * 
   * @param rules
   * @return schema (regex), if null the checker is inactive
   */
  private static String loadVersioningSchemaClass(final Map<String, Rule> rules) {
    final String SETTING_NAME = "versioningSchemaClass";
    String schema = null;
    final Rule rule = rules.get(VersioningChecker.class.getSimpleName());
    if (rule != null && rule.isActive()) {
      final Map<String, Setting> settings = rule.getSettings();
      final Setting setting = settings.get(SETTING_NAME);
      if (setting == null) {
        schema = ConstantsConfig.DEFAULT_VERSIONED_FILE_PATTERN;
        final Setting newSetting = new Setting(SETTING_NAME,
            ConstantsConfig.DEFAULT_VERSIONED_FILE_PATTERN);
        settings.put(SETTING_NAME, newSetting);
      } else {
        schema = setting.getValue();
      }
    }
    return schema;
  }
}
