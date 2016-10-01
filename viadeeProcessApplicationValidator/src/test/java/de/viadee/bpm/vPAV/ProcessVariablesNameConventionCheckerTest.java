package de.viadee.bpm.vPAV;

import static org.junit.Assert.assertEquals;

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
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.vPAV.config.model.ElementConvention;
import de.viadee.bpm.vPAV.config.model.ElementFieldTypes;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.ProcessVariableReader;
import de.viadee.bpm.vPAV.processing.checker.ElementChecker;
import de.viadee.bpm.vPAV.processing.checker.ProcessVariablesNameConventionChecker;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.ProcessVariable;

/**
 * unit tests for ProcessVariablesNameConventionChecker
 *
 */
public class ProcessVariablesNameConventionCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static Map<String, String> beanMapping;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    beanMapping = new HashMap<String, String>();
    beanMapping.put("myBean", "de.viadee.bpm.vPAV.delegates.TestDelegate");
    checker = new ProcessVariablesNameConventionChecker(createRule());
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java/");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * case: internal and external process variables follows the conventions
   */
  @Test
  public void testCorrectProcessVariableNames() {
    final String PATH = BASE_PATH
        + "ProcessVariablesNameConventionCheckerTest_CorrectProcessVariablesNamingConvention.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<BaseElement> baseElements = modelInstance
        .getModelElementsByType(BaseElement.class);

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    for (final BaseElement baseElement : baseElements) {
      final BpmnElement element = new BpmnElement(PATH, baseElement);
      Map<String, ProcessVariable> variables = new ProcessVariableReader(null, beanMapping)
          .getVariablesFromElement(element, cl);
      element.setProcessVariables(variables);

      issues.addAll(checker.check(element, cl));
    }

    assertEquals(0, issues.size());
  }

  /**
   * case: recognise variables which are against the naming conventions (internal/external)
   */
  @Test
  public void testWrongProcessVariableNames() {
    final String PATH = BASE_PATH
        + "ProcessVariablesNameConventionCheckerTest_WrongProcessVariablesNamingConvention.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<BaseElement> baseElements = modelInstance
        .getModelElementsByType(BaseElement.class);

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    for (final BaseElement baseElement : baseElements) {
      final BpmnElement element = new BpmnElement(PATH, baseElement);
      Map<String, ProcessVariable> variables = new ProcessVariableReader(null, beanMapping)
          .getVariablesFromElement(element, cl);
      element.setProcessVariables(variables);

      issues.addAll(checker.check(element, cl));
    }
    int externalConventions = 0;
    int internalConventions = 0;
    for (CheckerIssue issue : issues) {
      if (issue.getMessage().contains("external")) {
        externalConventions++;
      }
      if (issue.getMessage().contains("internal")) {
        internalConventions++;
      }
    }

    assertEquals(4, issues.size());
    assertEquals(1, internalConventions);
    assertEquals(3, externalConventions);
  }

  /**
   * Creates the configuration rule
   * 
   * @return rule
   */
  private static Rule createRule() {

    final Collection<ElementConvention> elementConventions = new ArrayList<ElementConvention>();
    final Collection<String> fieldTypeNames = new ArrayList<String>();
    fieldTypeNames.add("Class");
    fieldTypeNames.add("ExternalScript");
    fieldTypeNames.add("DelegateExpression");

    final ElementFieldTypes internalTypes = new ElementFieldTypes(fieldTypeNames, true);

    final ElementConvention internalElementConvention = new ElementConvention("internal",
        internalTypes, "int_[a-zA-Z]+");

    final ElementFieldTypes externalTypes = new ElementFieldTypes(fieldTypeNames, false);

    final ElementConvention externalElementConvention = new ElementConvention("external",
        externalTypes, "ext_[a-zA-Z]+");
    elementConventions.add(internalElementConvention);
    elementConventions.add(externalElementConvention);

    final Rule rule = new Rule("ProcessVariablesNameConventionChecker", true, null,
        elementConventions, null);

    return rule;
  }
}
