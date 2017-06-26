/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.bpm.vPAV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.viadee.bpm.vPAV.beans.BeanMappingXmlParser;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.config.reader.ConfigReaderException;
import de.viadee.bpm.vPAV.config.reader.XmlConfigReader;
import de.viadee.bpm.vPAV.output.IssueOutputWriter;
import de.viadee.bpm.vPAV.output.JsOutputWriter;
import de.viadee.bpm.vPAV.output.JsonOutputWriter;
import de.viadee.bpm.vPAV.output.OutputWriterException;
import de.viadee.bpm.vPAV.output.XmlOutputWriter;
import de.viadee.bpm.vPAV.processing.BpmnModelDispatcher;
import de.viadee.bpm.vPAV.processing.ConfigItemNotFoundException;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

/**
 * Goal which checks bpmn files
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class BpmnCheckerMojo extends AbstractMojo {

    private static Logger logger = Logger.getLogger(BpmnCheckerMojo.class.getName());

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    private static FileScanner fileScanner;

    public static Set<String> getModelPath() {
        return fileScanner.getProcessdefinitions();
    }

    public void execute() throws MojoExecutionException {

        // 1a) read config file
        final Map<String, Rule> rules = readConfigFile();

        // 1b) read bean mappings, if available
        final Map<String, String> beanMapping = BeanMappingXmlParser
                .parse(new File(ConstantsConfig.BEAN_MAPPING));

        // 2) Scan class path for bpmn models, dmn models, java files and versioned resources

        try {
            fileScanner = new FileScanner(project, rules);
        } catch (final MalformedURLException e) {
            throw new MojoExecutionException("wrong URL format");
        } catch (final DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("classpath couldn't be resolved");
        }

        // 3) get process variables from process start
        final OuterProcessVariablesScanner variableScanner = new OuterProcessVariablesScanner(
                fileScanner.getJavaResources(), fileScanner.getClassLoader());
        readOuterProcessVariables(variableScanner);

        // 4) Check each model
        final Collection<CheckerIssue> issues = checkModels(rules, beanMapping, fileScanner,
                variableScanner);

        // 5) remove ignored issues
        final Collection<CheckerIssue> filteredIssues = filterIssues(issues);

        // 6) write check results to xml and json and js file
        writeOutput(filteredIssues);

        // 7) copy html-files to target
        try {
            copyFilesToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (issuesWithErrors(issues))
            // throw error for maven build process
            // --> Build Fail
            throw new MojoExecutionException(
                    "BPMN validation with errors, see " + ConstantsConfig.VALIDATION_XML_OUTPUT);

        logger.info("BPMN validation successful completed");
    }

    /**
     * write output files (xml / json/ js)
     * 
     * @param filteredIssues
     * @throws MojoExecutionException
     */
    private void writeOutput(final Collection<CheckerIssue> filteredIssues)
            throws MojoExecutionException {
        final IssueOutputWriter xmlOutputWriter = new XmlOutputWriter();
        final IssueOutputWriter jsonOutputWriter = new JsonOutputWriter();
        final IssueOutputWriter jsOutputWriter = new JsOutputWriter();
        try {
            xmlOutputWriter.write(filteredIssues);
            jsonOutputWriter.write(filteredIssues);
            jsOutputWriter.write(filteredIssues);

        } catch (final OutputWriterException ex) {
            throw new MojoExecutionException(ex.getMessage());
        }
    }

    private void copyFilesToTarget() throws IOException {
        copyFileToTarget("bpmn-viewer.js");
        copyFileToTarget("bpmn.io.viewer.app.js");
        copyFileToTarget("bpmn.io.viewer.html");
        copyFileToTarget("jquery-3.2.1.js");
        copyFileToTarget("logo.png");
    }

    private void copyFileToTarget(String File) throws IOException {
        InputStream source = this.getClass().getClassLoader().getResourceAsStream(File);
        Path destination = Paths.get("target/" + File);
        if (destination.toFile().exists()) // if file exist, delete
            destination.toFile().delete();
        Files.copy(source, destination);
    }

    /**
     * filter issues based on black list
     * 
     * @param issues
     * @return
     * @throws MojoExecutionException
     */
    private Collection<CheckerIssue> filterIssues(final Collection<CheckerIssue> issues)
            throws MojoExecutionException {
        Collection<CheckerIssue> filteredIssues;
        try {
            filteredIssues = getFilteredIssues(issues);
        } catch (final IOException e) {
            throw new MojoExecutionException("ignored issues couldn't read successfully", e);
        }
        return filteredIssues;
    }

    /**
     * check consistency of all models
     * 
     * @param rules
     * @param beanMapping
     * @param fileScanner
     * @param variableScanner
     * @return
     * @throws MojoExecutionException
     */
    private Collection<CheckerIssue> checkModels(final Map<String, Rule> rules,
            final Map<String, String> beanMapping, final FileScanner fileScanner,
            final OuterProcessVariablesScanner variableScanner) throws MojoExecutionException {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

        for (final String pathToModel : fileScanner.getProcessdefinitions()) {
            issues.addAll(checkModel(rules, beanMapping, pathToModel, fileScanner, variableScanner));
        }
        return issues;
    }

    /**
     * scan process variables in external classes, which are not referenced from model
     * 
     * @param scanner
     * @throws MojoExecutionException
     */
    private void readOuterProcessVariables(final OuterProcessVariablesScanner scanner)
            throws MojoExecutionException {
        try {
            scanner.scanProcessVariables();
        } catch (final IOException e) {
            throw new MojoExecutionException(
                    "outer process variables couldn't be read: " + e.getMessage());
        }
    }

    /**
     * read configuration from xml file
     * 
     * @return rules
     * @throws MojoExecutionException
     */
    private Map<String, Rule> readConfigFile() throws MojoExecutionException {

        final Map<String, Rule> rules;
        try {
            rules = new XmlConfigReader().read(new File(ConstantsConfig.RULESET));
        } catch (final ConfigReaderException e) {
            throw new MojoExecutionException("config file couldn't be read", e);
        }
        return rules;
    }

    /**
     * check consistency of a model
     * 
     * @param rules
     * @param beanMapping
     * @param processdef
     * @param fileScanner
     * @param variableScanner
     * @return
     * @throws MojoExecutionException
     */
    private Collection<CheckerIssue> checkModel(final Map<String, Rule> rules,
            final Map<String, String> beanMapping, final String processdef, final FileScanner fileScanner,
            final OuterProcessVariablesScanner variableScanner) throws MojoExecutionException {
        Collection<CheckerIssue> modelIssues;
        try {
            modelIssues = BpmnModelDispatcher.dispatch(new File(ConstantsConfig.BASEPATH + processdef),
                    fileScanner.getDecisionRefToPathMap(), fileScanner.getProcessIdToPathMap(), beanMapping,
                    variableScanner.getMessageIdToVariableMap(), variableScanner.getProcessIdToVariableMap(),
                    fileScanner.getResourcesNewestVersions(), rules, fileScanner.getClassLoader());

        } catch (final ConfigItemNotFoundException e) {
            throw new MojoExecutionException("config item couldn't be read", e);
        }
        return modelIssues;
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
            logger.info(".ignoredIssues file doesn't exist");
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
}
