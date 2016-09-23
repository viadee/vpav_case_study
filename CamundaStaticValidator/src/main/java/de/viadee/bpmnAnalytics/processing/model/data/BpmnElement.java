package de.viadee.bpmnAnalytics.processing.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.model.bpmn.instance.BaseElement;

/**
 * Represents an bpmn element
 *
 */
public class BpmnElement {

  private String processdefinition;

  private BaseElement baseElement;

  private Map<String, InOutState> used = new HashMap<String, InOutState>();

  private Map<String, InOutState> defined = new HashMap<String, InOutState>();

  private Map<String, InOutState> in = new HashMap<String, InOutState>();

  private Map<String, InOutState> out = new HashMap<String, InOutState>();

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

  public void setProcessVariable(final String variableName, final ProcessVariable variableObject) {
    processVariables.put(variableName, variableObject);
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

  public Map<String, InOutState> getIn() {
    return in;
  }

  public Map<String, InOutState> getOut() {
    return out;
  }

  public void setIn(final Map<String, InOutState> outPredecessor) {
    this.in = outPredecessor;
  }

  public void setOut() {
    out.putAll(defined());
    changeStatusToRead(in);
    out.putAll(killed());
  }

  private Map<String, InOutState> used() {
    if (this.used.isEmpty()) {
      for (final ProcessVariable var : processVariables.values()) {
        if (var.getOperation() == VariableOperation.READ) {
          used.put(var.getName(), InOutState.READ);
        }
      }
    }
    return used;
  }

  public Map<String, InOutState> defined() {
    if (this.defined.isEmpty()) {
      for (final ProcessVariable var : processVariables.values()) {
        if (var.getOperation() == VariableOperation.WRITE) {
          defined.put(var.getName(), InOutState.DEFINED);
        }
      }
    }
    return defined;
  }

  private Map<String, InOutState> killed() {
    final Map<String, InOutState> killedVariables = new HashMap<String, InOutState>();
    for (final ProcessVariable var : processVariables.values()) {
      if (var.getOperation() == VariableOperation.DELETE) {
        killedVariables.put(var.getName(), InOutState.DELETED);
      }
    }
    return killedVariables;
  }

  public boolean ur(final String varName) {
    if ((in.containsKey(varName) == false
        || (in.containsKey(varName) == true && in.get(varName) == InOutState.DELETED))
        && used().containsKey(varName)) {
      return true;
    }
    return false;
  }

  public boolean du(final String varName) {
    if (in.containsKey(varName) && in.get(varName) == InOutState.DEFINED && out.containsKey(varName)
        && out.get(varName) == InOutState.DELETED) {
      return true;
    }
    return false;
  }

  public boolean dd(final String varName) {
    if (in.containsKey(varName) && in.get(varName) == InOutState.DEFINED
        && defined().containsKey(varName)) {
      return true;
    }
    return false;
  }

  public Map<BpmnElement, List<AnomalyContainer>> getAnomalies() {
    final Map<BpmnElement, List<AnomalyContainer>> anomalyMap = new HashMap<BpmnElement, List<AnomalyContainer>>();
    final Set<String> variableNames = new HashSet<String>();
    variableNames.addAll(used().keySet());
    for (final String variableName : in.keySet()) {
      if (in.get(variableName) == InOutState.DEFINED) {
        variableNames.add(variableName);
      }
    }
    final List<AnomalyContainer> anomalies = new ArrayList<AnomalyContainer>();
    for (final String variableName : variableNames) {
      if (ur(variableName)) {
        anomalies.add(new AnomalyContainer(variableName, Anomaly.UR, baseElement.getId(),
            processVariables.get(variableName)));
      }
      if (du(variableName)) {
        anomalies.add(new AnomalyContainer(variableName, Anomaly.DU, baseElement.getId(),
            processVariables.get(variableName)));
      }
      if (dd(variableName)) {
        anomalies.add(new AnomalyContainer(variableName, Anomaly.DD, baseElement.getId(),
            processVariables.get(variableName)));
      }
    }
    anomalyMap.put(this, anomalies);

    return anomalyMap;
  }

  private void changeStatusToRead(final Map<String, InOutState> inVariables) {
    for (final String varName : inVariables.keySet()) {
      if (used().containsKey(varName)) {
        out.put(varName, InOutState.READ);
      } else {
        out.put(varName, inVariables.get(varName));
      }
    }
  }
}
