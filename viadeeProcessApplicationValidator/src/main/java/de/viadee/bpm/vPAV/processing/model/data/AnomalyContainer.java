package de.viadee.bpm.vPAV.processing.model.data;

import java.util.Objects;

public class AnomalyContainer {

  private String name;

  private Anomaly anomaly;

  private String elementId;

  private ProcessVariable variable;

  public AnomalyContainer(final String name, final Anomaly anomaly, final String elementId,
      final ProcessVariable variable) {
    this.name = name;
    this.anomaly = anomaly;
    this.elementId = elementId;
    this.variable = variable;
  }

  public String getName() {
    return name;
  }

  public Anomaly getAnomaly() {
    return anomaly;
  }

  public String getElementId() {
    return elementId;
  }

  public ProcessVariable getVariable() {
    return variable;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AnomalyContainer) {
      final AnomalyContainer anomalyContainer = (AnomalyContainer) obj;
      if (this.name.equals(anomalyContainer.getName())
          && this.anomaly == anomalyContainer.getAnomaly()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.hashCode(), anomaly.toString().hashCode(), elementId.hashCode());
  }

  @Override
  public String toString() {
    return name + "(" + elementId + ", " + anomaly + ")";
  }
}
