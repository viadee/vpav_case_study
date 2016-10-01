package de.viadee.bpm.vPAV.processing.model.graph;

import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;

/**
 * University of Washington, Computer Science & Engineering, Course 373, Winter 2011, Jessica Miller
 * 
 * Representation of a directed graph edge.
 */
public class Edge {

  public BpmnElement from, to;

  public int weight;

  public Edge(BpmnElement from, BpmnElement to, int weight) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("null");
    }
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  public String toString() {
    return from + " -> " + to;
  }
}