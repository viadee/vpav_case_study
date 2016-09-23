package de.viadee.bpm.camundaStaticValidator.processing.checker;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.viadee.bpm.camundaStaticValidator.processing.model.data.CheckerIssue;

/**
 * Checks, which concern the whole model
 * 
 */
public interface ModelChecker {

  Collection<CheckerIssue> check(final BpmnModelInstance processdefinition, final ClassLoader cl);
}
