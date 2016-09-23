package de.viadee.bpm.camundaStaticValidator.processing.model.graph;

import java.util.Collection;
/**
 * University of Washington, Computer Science & Engineering, Course 373, Winter 2011, Jessica Miller
 * 
 * A basic graph interface.
 */
import java.util.List;
import java.util.Map;

import de.viadee.bpm.camundaStaticValidator.processing.model.data.AnomalyContainer;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.BpmnElement;

public interface IGraph {

  public String getProcessId();

  public void addVertex(BpmnElement v);

  public Collection<BpmnElement> getVertices();

  public void addEdge(BpmnElement v1, BpmnElement v2, int weight);

  public Collection<List<Edge>> getEdges();

  public void removeEdge(BpmnElement v1, BpmnElement v2);

  public boolean hasEdge(BpmnElement v1, BpmnElement v2);

  public Edge getEdge(BpmnElement v1, BpmnElement v2);

  public List<Path> getAllInvalidPaths(BpmnElement v, AnomalyContainer anomaly);

  public void setAnomalyInformation(BpmnElement v);

  public Map<BpmnElement, List<AnomalyContainer>> getNodesWithAnomalies();

  public void addStartNode(final BpmnElement node);

  public Collection<BpmnElement> getStartNodes();

  public void addEndNode(final BpmnElement node);

  public Collection<BpmnElement> getEndNodes();

  public String toString();
}
