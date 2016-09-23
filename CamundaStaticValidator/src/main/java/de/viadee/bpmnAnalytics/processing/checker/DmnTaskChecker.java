package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;

/**
 * Checks, whether a business rule task with dmn implementation is valid
 * 
 */
public class DmnTaskChecker implements ElementChecker {

  private Rule rule;

  public DmnTaskChecker(final Rule rule) {
    this.rule = rule;
  }

  @Override
  public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    final BaseElement baseElement = element.getBaseElement();

    if (baseElement instanceof BusinessRuleTask) {
      final BusinessRuleTask task = (BusinessRuleTask) baseElement;
      // no references to business rules
      if (task.getCamundaExpression() == null && task.getCamundaDelegateExpression() == null
          && task.getCamundaClass() == null && task.getCamundaDecisionRef() == null) {
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
            element.getProcessdefinition(), null, task.getId(), null, null,
            "business rule task with dmn implementation without a decision ref"));
      }
    }

    return issues;
  }
}
