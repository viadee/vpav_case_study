package de.viadee.bpmnAnalytics;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.checker.ElementChecker;
import de.viadee.bpmnAnalytics.processing.checker.EmbeddedGroovyScriptChecker;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;

public class EmbeddedGroovyScriptCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final Rule rule = new Rule("EmbeddedGroovyScriptChecker", true, null, null, null);
    checker = new EmbeddedGroovyScriptChecker(rule);
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: there is an empty script reference
   */
  @Test
  public void testEmptyScriptReference() {
    final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScriptReference.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ScriptTask> baseElements = modelInstance
        .getModelElementsByType(ScriptTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() == 0) {
      Assert.fail("there should be generated an issue");
    }
    Assert.assertEquals("there is an empty script reference",
        issues.iterator().next().getMessage());
  }

  /**
   * Case: there is no script format for given script
   */
  @Test
  public void testEmptyScriptFormat() {
    final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScriptFormat.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() == 0) {
      Assert.fail("there should be generated an issue");
    }
    Assert.assertEquals("there is no script format for given script",
        issues.iterator().next().getMessage());
  }

  /**
   * Case: there is no script content for given script format
   */
  @Test
  public void testEmptyScript() {
    final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScript.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() == 0) {
      Assert.fail("there should be generated an issue");
    }
    Assert.assertEquals("there is no script content for given script format",
        issues.iterator().next().getMessage());
  }

  /**
   * Case: invalid script for groovy script format
   */
  @Test
  public void testInvalidGroovyScript() {
    final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_InvalidGroovyScript.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() == 0) {
      Assert.fail("there should be generated an issue");
    }
    final String message = issues.iterator().next().getMessage();
    Assert.assertTrue(message.startsWith("startup failed:"));
  }
}
