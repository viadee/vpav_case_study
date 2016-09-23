package de.viadee.bpmnAnalytics.processing.model.data;

public enum Anomaly {
  DD("defined-defined"), DU("defined-undefined"), UR("undefined-read");

  private final String description;

  private Anomaly(String value) {
    description = value;
  }

  public String getDescription() {
    return description;
  }
}
