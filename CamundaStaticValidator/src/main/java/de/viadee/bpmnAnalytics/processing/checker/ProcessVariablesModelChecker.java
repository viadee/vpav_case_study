package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;

public class ProcessVariablesModelChecker implements ModelChecker {

  private final Rule rule;

  private final Map<ProcessVariable, List<Path>> invalidPathsMap;

  public ProcessVariablesModelChecker(final Rule rule,
      final Map<ProcessVariable, List<Path>> invalidPathsMap) {
    this.rule = rule;
    this.invalidPathsMap = invalidPathsMap;
  }

  @Override
  public Collection<CheckerIssue> check(final BpmnModelInstance processdefinition,
      final ClassLoader cl) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    for (final ProcessVariable var : invalidPathsMap.keySet()) {
      final List<Path> paths = invalidPathsMap.get(var);
      if (paths != null) {
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
            var.getElement().getProcessdefinition(), var.getResourceFilePath(),
            var.getElement().getBaseElement().getId(), var.getName(), paths,
            "process variable is not initialised (compare " + var.getChapter() + ", "
                + var.getFieldType().getDescription() + ")"));
      }
    }

    return issues;
  }
}
