package de.viadee.bpmnAnalytics.processing.model.graph;

import java.util.List;

import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;

public class Path {

  private List<BpmnElement> elements;

  public Path(final List<BpmnElement> elements) {
    this.elements = elements;
  }

  public List<BpmnElement> getElements() {
    return elements;
  }

  public String toString() {
    return elements.toString();
  }
}
