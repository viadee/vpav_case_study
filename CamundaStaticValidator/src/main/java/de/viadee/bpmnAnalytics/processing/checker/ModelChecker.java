package de.viadee.bpmnAnalytics.processing.checker;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;

/**
 * Checks, which concern the whole model
 * 
 */
public interface ModelChecker {

  Collection<CheckerIssue> check(final BpmnModelInstance processdefinition, final ClassLoader cl);
}
