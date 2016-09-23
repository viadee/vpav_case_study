package de.viadee.bpmnAnalytics.processing.model.graph;

/**
 * University of Washington, Computer Science & Engineering, Course 373, Winter 2011, Jessica Miller
 * 
 * A basic graph interface.
 */
import java.util.List;

import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;

public interface IGraph {

  public void addVertex(BpmnElement v);

  public void addEdge(BpmnElement v1, BpmnElement v2, int weight);

  public boolean hasEdge(BpmnElement v1, BpmnElement v2);

  public Edge getEdge(BpmnElement v1, BpmnElement v2);

  public boolean hasPath(BpmnElement v, String varName);

  public List<BpmnElement> getDFSPath(BpmnElement v, String varName);

  public List<Path> getAllInvalidPaths(BpmnElement v, String varName, int maxSize);

  public String toString();
}
