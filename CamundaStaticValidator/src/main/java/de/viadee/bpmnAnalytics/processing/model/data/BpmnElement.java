package de.viadee.bpmnAnalytics.processing.model.data;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.BaseElement;

/**
 * Represents an bpmn element
 *
 */
public class BpmnElement {

  private String processdefinition;

  private BaseElement baseElement;

  private Map<String, ProcessVariable> processVariables;

  public BpmnElement(final String processdefinition, final BaseElement element) {
    this.processdefinition = processdefinition;
    this.baseElement = element;
    this.processVariables = new HashMap<String, ProcessVariable>();
  }

  public String getProcessdefinition() {
    return processdefinition;
  }

  public BaseElement getBaseElement() {
    return baseElement;
  }

  public Map<String, ProcessVariable> getProcessVariables() {
    return processVariables;
  }

  public void setProcessVariables(final Map<String, ProcessVariable> variables) {
    this.processVariables = variables;
  }

  @Override
  public int hashCode() {
    return baseElement.getId().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BpmnElement && this.hashCode() == o.hashCode()) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return baseElement.getId();
  }
}
