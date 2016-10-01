package de.viadee.bpm.vPAV.processing.checker;

import java.util.Collection;

import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

/**
 * Checks bpmn models for defined characteristics
 */
public interface ElementChecker {

  Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl);
}
