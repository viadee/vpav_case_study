package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.model.data.Anomaly;
import de.viadee.bpmnAnalytics.processing.model.data.AnomalyContainer;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;

public class ProcessVariablesModelChecker implements ModelChecker {

  private final Rule rule;

  private final Map<AnomalyContainer, List<Path>> invalidPathsMap;

  public ProcessVariablesModelChecker(final Rule rule,
      final Map<AnomalyContainer, List<Path>> invalidPathsMap) {
    this.rule = rule;
    this.invalidPathsMap = invalidPathsMap;
  }

  @Override
  public Collection<CheckerIssue> check(final BpmnModelInstance processdefinition,
      final ClassLoader cl) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    for (final AnomalyContainer anomaly : invalidPathsMap.keySet()) {
      final List<Path> paths = invalidPathsMap.get(anomaly);
      final ProcessVariable var = anomaly.getVariable();
      if (paths != null) {
        issues.add(new CheckerIssue(rule.getName(), determineCriticality(anomaly.getAnomaly()),
            var.getElement().getProcessdefinition(), var.getResourceFilePath(),
            var.getElement().getBaseElement().getId(),
            var.getElement().getBaseElement().getAttributeValue("name"), var.getName(),
            anomaly.getAnomaly(), paths, "process variable creates an anomaly " + "(compare "
                + var.getChapter() + ", " + var.getFieldType().getDescription() + ")"));
      }
    }

    return issues;
  }

  private CriticalityEnum determineCriticality(final Anomaly anomaly) {

    if (anomaly == Anomaly.DD || anomaly == Anomaly.DU) {
      return CriticalityEnum.WARNING;
    } else {
      return CriticalityEnum.ERROR;
    }
  }
}
