package de.viadee.bpm.vPAV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.vPAV.config.model.ElementConvention;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.checker.ElementChecker;
import de.viadee.bpm.vPAV.processing.checker.TaskNamingConventionChecker;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

public class TaskNamingConventionCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    checker = new TaskNamingConventionChecker(createRule());
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java/");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case 1: Recognise a task name that fits the naming convention
   */
  @Test
  public void testCorrectTaskNamingConvention() {
    final String PATH = BASE_PATH
        + "TaskNamingConventionCheckerTest_CorrectTaskNamingConvention.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<BaseElement> baseElements = modelInstance
        .getModelElementsByType(BaseElement.class);

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    for (final BaseElement baseElement : baseElements) {
      final BpmnElement element = new BpmnElement(PATH, baseElement);
      issues.addAll(checker.check(element, cl));
    }

    if (issues.size() > 0) {
      fail("There are issues, altough the convention is correct.");
    }
  }

  /**
   * Case 2: Recognise a violation against the naming convention
   */
  @Test
  public void testWrongTaskNamingConvention() {
    final String PATH = BASE_PATH
        + "TaskNamingConventionCheckerTest_WrongTaskNamingConvention.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<BaseElement> baseElements = modelInstance
        .getModelElementsByType(BaseElement.class);

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    for (final BaseElement baseElement : baseElements) {
      final BpmnElement element = new BpmnElement(PATH, baseElement);
      issues.addAll(checker.check(element, cl));
    }

    assertEquals("The issue wasn't recognised", 1, issues.size());
  }

  /**
   * Creates rule configuration
   * 
   * @return rule
   */
  private static Rule createRule() {

    final Collection<ElementConvention> elementConventions = new ArrayList<ElementConvention>();

    final ElementConvention elementConvention = new ElementConvention("convention", null,
        "[A-ZÄÖÜ][a-zäöü\\-\\s]+");
    elementConventions.add(elementConvention);

    final Rule rule = new Rule("TaskNamingConventionChecker", true, null, elementConventions, null);

    return rule;
  }
}
