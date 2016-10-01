package de.viadee.bpm.vPAV;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.checker.DmnTaskChecker;
import de.viadee.bpm.vPAV.processing.checker.ElementChecker;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

/**
 * Unit Tests for DmnTaskChecker
 *
 */
public class DmnTaskCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final Rule rule = new Rule("DmnTaskChecker", true, null, null, null);
    checker = new DmnTaskChecker(rule);
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: DMN task without a reference should produce an error
   */
  @Test
  public void testWrongDmnTask() {
    final String PATH = BASE_PATH + "DmnTaskCheckerTest_WrongDmnTask.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<BusinessRuleTask> baseElements = modelInstance
        .getModelElementsByType(BusinessRuleTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);
    Assert.assertEquals(1, issues.size());
  }
}
