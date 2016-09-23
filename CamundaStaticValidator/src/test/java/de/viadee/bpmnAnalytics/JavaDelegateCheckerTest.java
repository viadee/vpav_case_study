package de.viadee.bpmnAnalytics;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.checker.ElementChecker;
import de.viadee.bpmnAnalytics.processing.checker.JavaDelegateChecker;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;

/**
 * unit tests for class JavaDelegateChecker
 *
 */
public class JavaDelegateCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final Rule rule = new Rule("JavaDelegateChecker", true, null, null, null);
    checker = new JavaDelegateChecker(rule, null);
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: JavaDelegate has been correct set
   */
  @Test
  public void testCorrectJavaDelegateReference() {
    final String PATH = BASE_PATH + "testCorrectJavaDelegateReference.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() > 0) {
      Assert.fail("correct java delegate generates an issue");
    }
  }

  /**
   * Case: There are no technical attributes
   */
  @Test
  public void testNoTechnicalAttributes() {
    final String PATH = BASE_PATH + "testNoTechnicalAttributes.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("task 'Service Task 1' with no code reference yet",
          issues.iterator().next().getMessage());
    }
  }

  /**
   * Case: java delegate has not been set
   */
  @Test
  public void testNoJavaDelegateEntered() {
    final String PATH = BASE_PATH + "testNoJavaDelegateEntered.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("task 'Service Task 1' with no class name",
          issues.iterator().next().getMessage());
    }
  }

  /**
   * Case: The path of the java delegate isn't correct
   */
  @Test
  public void testWrongJavaDelegatePath() {
    final String PATH = BASE_PATH + "testWrongJavaDelegatePath.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("class for task 'Service Task 1' not found",
          issues.iterator().next().getMessage());
    }
  }

  /**
   * Case: The java delegates implements no or a wrong interface
   */
  @Test
  public void testWrongJavaDelegateInterface() {
    final String PATH = BASE_PATH + "testWrongJavaDelegateInterface.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("class for task 'Service Task 1' not implement interface JavaDelegate",
          issues.iterator().next().getMessage());
    }
  }
}
