package de.viadee.bpmnAnalytics.processing.checker;

import java.util.Collection;

import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;

/**
 * Checks bpmn models for defined characteristics
 */
public interface ElementChecker {

  Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl);
}
