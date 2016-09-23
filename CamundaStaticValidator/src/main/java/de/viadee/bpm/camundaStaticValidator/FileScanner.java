package de.viadee.bpm.camundaStaticValidator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelException;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import de.viadee.bpm.camundaStaticValidator.config.model.Rule;
import de.viadee.bpm.camundaStaticValidator.config.model.Setting;
import de.viadee.bpm.camundaStaticValidator.processing.checker.VersioningChecker;

/**
 * scans maven project for files, which are necessary for the later analysis
 *
 */
public class FileScanner {

  private final ClassLoader classLoader;

  private final Set<String> processdefinitions;

  private final Set<String> javaResources;

  private final Map<String, String> decisionRefToPathMap;

  private Collection<String> resourcesNewestVersions = new ArrayList<String>();

  private Map<String, String> processIdToPathMap;

  public FileScanner(final MavenProject project, final Map<String, Rule> rules)
      throws MalformedURLException, DependencyResolutionRequiredException {

    // determine class loader
    classLoader = getClassLoader(project);

    // initialize scanner for searching files in maven project
    final Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setScanners(new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader(classLoader)));

    // get file paths of process definitions
    processdefinitions = reflections
        .getResources(Pattern.compile(ConstantsConfig.BPMN_FILE_PATTERN));

    // get mapping from process id to file path
    processIdToPathMap = createProcessIdToPathMap(processdefinitions);

    // get file paths of java files
    javaResources = reflections.getResources(Pattern.compile(ConstantsConfig.JAVA_FILE_PATTERN));

    // get mapping from decision reference to file path
    decisionRefToPathMap = createDmnKeyToPathMap(
        reflections.getResources(Pattern.compile(ConstantsConfig.DMN_FILE_PATTERN)));

    // determine version name schema for resources
    final String versioningSchema = loadVersioningSchemaClass(rules);
    if (versioningSchema != null) {
      // get current versions for resources, that match the name schema
      resourcesNewestVersions = createResourcesToNewestVersions(
          reflections.getResources(Pattern.compile(versioningSchema)), versioningSchema);
    }
  }

  /**
   * get class loader
   * 
   * @return
   */
  public ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * get file paths for process definitions
   * 
   * @return
   */
  public Set<String> getProcessdefinitions() {
    return processdefinitions;
  }

  /**
   * get file paths of java resources
   * 
   * @return
   */
  public Set<String> getJavaResources() {
    return javaResources;
  }

  /**
   * get mapping from process id to file path of bpmn models
   * 
   * @return
   */
  public Map<String, String> getProcessIdToPathMap() {
    return processIdToPathMap;
  }

  /**
   * get mapping from decisionRef to file path of dmn models
   * 
   * @return
   */
  public Map<String, String> getDecisionRefToPathMap() {
    return decisionRefToPathMap;
  }

  /**
   * get a list of versioned resources (only with current versions)
   * 
   * @return
   */
  public Collection<String> getResourcesNewestVersions() {
    return resourcesNewestVersions;
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
   * Map for getting bpmn reference by process id
   * 
   * @param paths
   * @return
   */
  private static Map<String, String> createProcessIdToPathMap(final Set<String> paths) {

    final Map<String, String> keyToPathMap = new HashMap<String, String>();

    for (final String path : paths) {
      // read bpmn file
      BpmnModelInstance modelInstance = null;
      try {
        modelInstance = Bpmn.readModelFromFile(new File("src/main/resources/" + path));
      } catch (final BpmnModelException ex) {
        throw new RuntimeException("bpmn model couldn't be read", ex);
      }
      // if bpmn file could read
      if (modelInstance != null) {
        // find process
        final Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        if (processes != null) {
          for (final Process process : processes) {
            // save path for each process
            keyToPathMap.put(process.getId(), path);
          }
        }
      }
    }
    return keyToPathMap;
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
        throw new RuntimeException("dmn model couldn't be read", ex);
      }
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
