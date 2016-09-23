package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.Task;

import de.viadee.bpmnAnalytics.config.model.ElementConvention;
import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.ProcessingException;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;

public class TaskNamingConventionChecker implements ElementChecker {

  private Rule rule;

  public TaskNamingConventionChecker(final Rule rule) {
    this.rule = rule;
  }

  @Override
  public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    final BaseElement baseElement = element.getBaseElement();
    if (baseElement instanceof Task) {
      final Collection<ElementConvention> elementConventions = rule.getElementConventions();
      if (elementConventions == null || elementConventions.size() < 1
          || elementConventions.size() > 1) {
        throw new ProcessingException(
            "task naming convention checker must have one element convention!");
      }
      final String patternString = elementConventions.iterator().next().getPattern();
      final String taskName = baseElement.getAttributeValue("name");
      if (taskName != null && taskName.trim().length() > 0) {
        final Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(taskName);
        if (!matcher.matches()) {
          issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
              element.getProcessdefinition(), null, baseElement.getId(), null, null,
              "task name '" + taskName + "' is against the naming convention"));
        }
      } else {
        issues.add(
            new CheckerIssue(rule.getName(), CriticalityEnum.ERROR, element.getProcessdefinition(),
                null, baseElement.getId(), null, null, "task name must be specified"));
      }
    }
    return issues;
  }
}
