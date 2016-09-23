package de.viadee.bpmnAnalytics.processing.model.graph;

/**
 * University of Washington, Computer Science & Engineering, Course 373, Winter 2011, Jessica Miller
 * 
 * A basic graph interface.
 */
import java.util.List;
import java.util.Map;

import de.viadee.bpmnAnalytics.processing.model.data.AnomalyContainer;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;

public interface IGraph {

  public void addVertex(BpmnElement v);

  public void addEdge(BpmnElement v1, BpmnElement v2, int weight);

  public void removeEdge(BpmnElement v1, BpmnElement v2);

  public boolean hasEdge(BpmnElement v1, BpmnElement v2);

  public Edge getEdge(BpmnElement v1, BpmnElement v2);

  public List<Path> getAllInvalidPaths(BpmnElement v, AnomalyContainer anomaly);

  public void setAnomalyInformation(BpmnElement v);

  public Map<BpmnElement, List<AnomalyContainer>> getNodesWithAnomalies();

  public String toString();
}
