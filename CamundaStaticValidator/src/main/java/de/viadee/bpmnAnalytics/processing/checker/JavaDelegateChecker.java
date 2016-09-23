package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;

/**
 * Class JavaDelegateChecker
 * 
 * Checks a bpmn model, if code references (java delegates) for service tasks have been set
 * correctly.
 *
 */
public class JavaDelegateChecker implements ElementChecker {

  private final Rule rule;

  public JavaDelegateChecker(final Rule rule) {
    this.rule = rule;
  }

  public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader classLoader) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    final BaseElement bpmnElement = element.getBaseElement();

    // read attributes from service task
    final String classAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
        "class");
    final String delegateExprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
        "delegateExpression");
    final String exprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
        "expression");
    final String typeAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "type");

    if (classAttr != null) {
      if (classAttr.trim().length() == 0 && delegateExprAttr == null && exprAttr == null
          && typeAttr == null) {
        // Error, because no class has been configured
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"), null, null,
            "service task '" + bpmnElement.getAttributeValue("name") + "' with no class name"));
      }
      if (classAttr.trim().length() > 0) {
        // If a class path has been found, check the correctness
        try {
          Class<?> clazz = classLoader.loadClass(classAttr);

          // Checks, whether the correct interface was implemented
          Class<?>[] interfaces = clazz.getInterfaces();
          boolean javaDelegateImplemented = false;
          for (final Class<?> _interface : interfaces) {
            if (_interface.getName().contains("JavaDelegate")) {
              javaDelegateImplemented = true;
            }
          }
          if (javaDelegateImplemented == false) {
            // Klasse implementiert nicht das Interface "JavaDelegate"
            issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"), null,
                null, "class for service task '" + bpmnElement.getAttributeValue("name")
                    + "' not implement interface JavaDelegate"));
          }

        } catch (final ClassNotFoundException e) {
          // Throws an error, if the class was not found
          issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
              element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"), null, null,
              "class for service task '" + bpmnElement.getAttributeValue("name") + "' not found"));
        }
      }
    }
    if (classAttr == null && delegateExprAttr == null && exprAttr == null && typeAttr == null) {
      // No technical attributes have been added
      issues.add(
          new CheckerIssue(rule.getName(), CriticalityEnum.WARNING, element.getProcessdefinition(),
              null, bpmnElement.getAttributeValue("id"), null, null, "service task '"
                  + bpmnElement.getAttributeValue("name") + "' with no code reference yet"));
    }
    return issues;
  }
}
