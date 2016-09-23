package de.viadee.bpmnAnalytics;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.config.model.Setting;
import de.viadee.bpmnAnalytics.processing.checker.ElementChecker;
import de.viadee.bpmnAnalytics.processing.checker.VersioningChecker;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import junit.framework.Assert;

public class VersioningCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: test versioning for java class
   */
  @Test
  public void testJavaClassVersioning() {
    final String PATH = BASE_PATH + "VersioningCheckerTest_JavaClassVersioning.bpmn";

    final Rule rule = new Rule("VersioningChecker", true, null, null, null);

    // Versions
    final Collection<String> resourcesNewestVersions = new ArrayList<String>();
    resourcesNewestVersions.add("de/test/TestDelegate_1_2.java");

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final ElementChecker checker = new VersioningChecker(rule, null, resourcesNewestVersions);
    final Collection<CheckerIssue> issues = checker.check(element, cl);
    Assert.assertEquals(1, issues.size());
  }

  /**
   * Case: test versioning for script
   */
  @Test
  public void testScriptVersioning() {
    final String PATH = BASE_PATH + "VersioningCheckerTest_ScriptVersioning.bpmn";

    final Rule rule = new Rule("VersioningChecker", true, null, null, null);

    // Versions
    final Collection<String> resourcesNewestVersions = new ArrayList<String>();
    resourcesNewestVersions.add("de/test/testScript_1_2.groovy");

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final ElementChecker checker = new VersioningChecker(rule, null, resourcesNewestVersions);
    final Collection<CheckerIssue> issues = checker.check(element, cl);
    Assert.assertEquals(1, issues.size());
  }

  /**
   * Case: test versioning for spring bean, with an outdated class reference
   */
  @Test
  public void testBeanVersioningWithOutdatedClass() {
    final String PATH = BASE_PATH + "VersioningCheckerTest_BeanVersioningOutdatedClass.bpmn";

    final Map<String, Setting> settings = new HashMap<String, Setting>();
    settings.put("versioningSchemaClass",
        new Setting("versioningSchemaClass", "([^_]*)_{1}([0-9][_][0-9]{1})\\.(java|groovy)"));

    final Rule rule = new Rule("VersioningChecker", true, settings, null, null);

    // Versions
    final Collection<String> resourcesNewestVersions = new ArrayList<String>();
    resourcesNewestVersions.add("de/test/TestDelegate_1_2.java");

    // Bean-Mapping
    final Map<String, String> beanMapping = new HashMap<String, String>();
    beanMapping.put("myBean_1_1", "de.test.TestDelegate_1_1");

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final ElementChecker checker = new VersioningChecker(rule, beanMapping,
        resourcesNewestVersions);
    final Collection<CheckerIssue> issues = checker.check(element, cl);
    Assert.assertEquals(1, issues.size());
  }
}
