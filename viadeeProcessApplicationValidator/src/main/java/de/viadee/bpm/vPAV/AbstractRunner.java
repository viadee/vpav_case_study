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
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.maven.artifact.DependencyResolutionRequiredException;

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

public abstract class AbstractRunner {

    private static Logger logger = Logger.getLogger(AbstractRunner.class.getName());

    private static FileScanner fileScanner;

    private static Map<String, String> beanMapping;

    private static OuterProcessVariablesScanner variableScanner;

    private static Collection<CheckerIssue> issues;

    private static Collection<CheckerIssue> filteredIssues;

    protected static ClassLoader classLoader;

    public static void run_vPAV() {

        /*
         * 1. Read Config 1b. Read Bean Mapping (Mojo) 1c. Create Bean Mapping (JUnit) 2. Retrieve Class Path for
         * JUnit/Maven 2b. Scan Class Path for Models 3. Get Process Variables 4. Check Each Model 5. Remove Ignored
         * Issues 6. Write Check Results 7. Copy Files
         */

        // 1
        final Map<String, Rule> rules = readConfig();

        // 2
        scanClassPath(rules);

        // 3
        getProcessVariables();

        // 4
        createIssues(rules);

        // 5
        filteredIssues = filterIssues(issues);

        // 6
        writeOutput(filteredIssues);

        // 7
        copyFiles();

        logger.info("BPMN validation successful completed");

    }

    // 1a - Read config file

    public static Map<String, Rule> readConfig() throws RuntimeException {
        final Map<String, Rule> rules;
        try {
            rules = new XmlConfigReader().read(new File(ConstantsConfig.RULESET));
        } catch (final ConfigReaderException e) {
            throw new RuntimeException("Config file couldn`t be read");
        }
        return rules;
    }

    // 2b - Scan classpath for models
    public static void scanClassPath(Map<String, Rule> rules)
            throws RuntimeException {
        try {
            fileScanner = new FileScanner(rules, classLoader);
        } catch (final MalformedURLException e) {
            throw new RuntimeException("Cannot resolve URL format");
        } catch (final DependencyResolutionRequiredException e) {
            throw new RuntimeException("Classpath couldn't be resolved");
        }
    }

    // 3 - Get process variables
    public static void getProcessVariables() throws RuntimeException {
        variableScanner = new OuterProcessVariablesScanner(
                fileScanner.getJavaResources(), fileScanner.getClassLoader());
        readOuterProcessVariables(variableScanner);
    }

    // 4 - Check each model
    public static void createIssues(Map<String, Rule> rules) throws RuntimeException {
        issues = checkModels(rules, beanMapping, fileScanner, variableScanner);
    }

    // 5 remove ignored issues
    public static void removeIgnoredIssues() throws RuntimeException {
        filteredIssues = filterIssues(issues);
    }

    /**
     * write output files (xml / json/ js)
     * 
     * @param filteredIssue
     * @throws OutputWriterException
     */
    // 6 write check results to xml and json and js file
    public static void writeOutput(final Collection<CheckerIssue> filteredIssues) throws RuntimeException {
        final IssueOutputWriter xmlOutputWriter = new XmlOutputWriter();
        final IssueOutputWriter jsonOutputWriter = new JsonOutputWriter();
        final IssueOutputWriter jsOutputWriter = new JsOutputWriter();
        try {
            xmlOutputWriter.write(filteredIssues);
            jsonOutputWriter.write(filteredIssues);
            jsOutputWriter.write(filteredIssues);

        } catch (final OutputWriterException e) {
            throw new RuntimeException("Output couldn't be written");
        }
    }

    // 7 copy html-files to target
    public static void copyFiles() throws RuntimeException {
        if (filteredIssues.size() > 0) {
            copyFileToTarget("bpmn-viewer.js");
            copyFileToTarget("bpmn.io.viewer.app.js");
            copyFileToTarget("bpmn.io.viewer.html");
            copyFileToTarget("logo.png");
        } else {
            copyFileToTarget("noIssues.html");
            copyFileToTarget("logo.png");
        }
    }

    /**
     * filter issues based on black list
     * 
     * @param issues
     * @return
     * @throws IOException
     */
    private static Collection<CheckerIssue> filterIssues(final Collection<CheckerIssue> issues)
            throws RuntimeException {
        Collection<CheckerIssue> filteredIssues;
        try {
            filteredIssues = getFilteredIssues(issues);
        } catch (final IOException e) {
            throw new RuntimeException("Ignored issues couldn't be read successfully", e);
        }
        return filteredIssues;
    }

    /**
     * remove false positives from issue collection
     * 
     * @param issues
     * @return filteredIssues
     * @throws IOException
     */
    private static Collection<CheckerIssue> getFilteredIssues(Collection<CheckerIssue> issues)
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

    private static void copyFileToTarget(String File) throws RuntimeException {
        InputStream source = AbstractRunner.class.getClassLoader().getResourceAsStream(File);
        Path destination = Paths.get("target/" + File);
        if (destination.toFile().exists()) // if file exist, delete
            destination.toFile().delete();
        try {
            Files.copy(source, destination);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * check consistency of all models
     * 
     * @param rules
     * @param beanMapping
     * @param fileScanner
     * @param variableScanner
     * @return
     * @throws ConfigItemNotFoundException
     */
    private static Collection<CheckerIssue> checkModels(final Map<String, Rule> rules,
            final Map<String, String> beanMapping, final FileScanner fileScanner,
            final OuterProcessVariablesScanner variableScanner) throws RuntimeException {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

        for (final String pathToModel : fileScanner.getProcessdefinitions()) {
            issues.addAll(checkModel(rules, beanMapping, pathToModel, fileScanner, variableScanner));
        }
        return issues;
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
     * @throws ConfigItemNotFoundException
     */
    private static Collection<CheckerIssue> checkModel(final Map<String, Rule> rules,
            final Map<String, String> beanMapping, final String processdef, final FileScanner fileScanner,
            final OuterProcessVariablesScanner variableScanner) throws RuntimeException {
        Collection<CheckerIssue> modelIssues;
        try {
            modelIssues = BpmnModelDispatcher.dispatch(new File(ConstantsConfig.BASEPATH + processdef),
                    fileScanner.getDecisionRefToPathMap(), fileScanner.getProcessIdToPathMap(), beanMapping,
                    variableScanner.getMessageIdToVariableMap(), variableScanner.getProcessIdToVariableMap(),
                    fileScanner.getResourcesNewestVersions(), rules, fileScanner.getClassLoader());

        } catch (final ConfigItemNotFoundException e) {
            throw new RuntimeException("Config item couldn't be read");
        }
        return modelIssues;
    }

    /**
     * scan process variables in external classes, which are not referenced from model
     *
     * @param scanner
     * @throws IOException
     */
    private static void readOuterProcessVariables(final OuterProcessVariablesScanner scanner) throws RuntimeException {
        try {
            scanner.scanProcessVariables();
        } catch (final IOException e) {
            throw new RuntimeException("Outer process variables couldn't be read: " + e.getMessage());
        }
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

    public static Set<String> getModelPath() {
        return fileScanner.getProcessdefinitions();
    }

    public static Collection<CheckerIssue> getfilteredIssues() {
        return filteredIssues;
    }

}
