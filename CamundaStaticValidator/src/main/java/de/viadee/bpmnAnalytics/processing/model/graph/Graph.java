package de.viadee.bpmnAnalytics.processing.model.graph;

/**
 * University of Washington, Computer Science & Engineering, Course 373, Winter 2011, Jessica Miller
 * 
 * A class for a directed graph. Implemented by an adjacency list representation of a graph.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;

public class Graph implements IGraph {

  private Map<BpmnElement, List<Edge>> adjacencyList; // [vertices] -> [edge]

  private Map<BpmnElement, VertexInfo> vertexInfo; // [vertex] -> [info]

  public Graph() {
    this.adjacencyList = new HashMap<BpmnElement, List<Edge>>();
    this.vertexInfo = new HashMap<BpmnElement, VertexInfo>();
  }

  public void addVertex(BpmnElement v) {
    if (v == null) {
      throw new IllegalArgumentException("null");
    }

    adjacencyList.put(v, new ArrayList<Edge>());
    vertexInfo.put(v, new VertexInfo(v));
  }

  public void addEdge(BpmnElement from, BpmnElement to, int weight) {
    List<Edge> edgeList = adjacencyList.get(from);
    if (edgeList == null) {
      throw new IllegalArgumentException("source vertex not in graph");
    }

    Edge newEdge = new Edge(from, to, weight);
    edgeList.add(newEdge);
  }

  public boolean hasEdge(BpmnElement from, BpmnElement to) {
    return getEdge(from, to) != null;
  }

  public Edge getEdge(BpmnElement from, BpmnElement to) {
    List<Edge> edgeList = adjacencyList.get(from);
    if (edgeList == null) {
      throw new IllegalArgumentException("source vertex not in graph");
    }

    for (Edge e : edgeList) {
      if (e.to.equals(to)) {
        return e;
      }
    }

    return null;
  }

  public boolean hasPath(BpmnElement v, final String varName) {
    return getDFSPath(v, varName) != null;
  }

  public List<BpmnElement> getDFSPath(BpmnElement v, final String varName) {
    clearVertexInfo();

    List<BpmnElement> path = new ArrayList<BpmnElement>();
    getDFSPath(v, varName, path);
    if (path.isEmpty()) {
      return null;
    } else {
      return path;
    }
  }

  /**
   * search path with written process variable
   * 
   * @param start
   *          element
   * @param variable
   * @return
   */
  private List<BpmnElement> getDFSPath(final BpmnElement v, final String varName,
      final List<BpmnElement> path) {

    path.add(v);
    final VertexInfo vInfo = vertexInfo.get(v);
    vInfo.visitVariable(varName);

    final Map<String, ProcessVariable> variablesMap = v.getProcessVariables();
    final ProcessVariable currentVar = variablesMap.get(varName);
    // Abbruchbedingung
    if (currentVar != null && currentVar.isWriteOperation()) {
      return path;
    }

    final List<Edge> edges = this.adjacencyList.get(v);
    for (Edge e : edges) {
      final VertexInfo vInfo2 = vertexInfo.get(e.to);
      if (!vInfo2.variableVisited(varName)) {
        getDFSPath(e.to, varName, path);
        if (!path.isEmpty()) {
          final Map<String, ProcessVariable> variableMap = path.get(path.size() - 1)
              .getProcessVariables();
          final ProcessVariable nextElementVar = variableMap.get(varName);
          // Abbruchbedingung
          if (nextElementVar != null && nextElementVar.isWriteOperation()) {
            return path;
          }
        }
      }
    }
    path.remove(v);
    return path;
  }

  /**
   * search all paths with variables, which has not been set
   * 
   * source:
   * http://codereview.stackexchange.com/questions/45678/find-all-paths-from-source-to-destination
   */
  public List<Path> getAllInvalidPaths(final BpmnElement source, final String varName,
      final int maxSize) {
    final List<Path> paths = new ArrayList<Path>();
    // int lastPathLength = 0;
    recursive(source, varName, paths, new LinkedHashSet<BpmnElement>()/* , lastPathLength */,
        maxSize);
    return paths;
  }

  /**
   * search all paths with variables, which has not been set
   * 
   * source:
   * http://codereview.stackexchange.com/questions/45678/find-all-paths-from-source-to-destination
   * 
   * @param startNode
   * @param varName
   * @param paths
   * @param path
   * @param maxSize
   */
  private void recursive(final BpmnElement startNode, final String varName, final List<Path> paths,
      final LinkedHashSet<BpmnElement> path, /* int lastPathLength, */ final int maxSize) {

    path.add(startNode);

    final Map<String, ProcessVariable> variableMap = startNode.getProcessVariables();
    final ProcessVariable variable = variableMap.get(varName);

    if (variable != null && variable.isWriteOperation()) {
      // cancel search, if a variable is set
      path.clear();
      return;
    }

    final List<Edge> edges = this.adjacencyList.get(startNode);

    for (final Edge t : edges) {
      if (!path.contains(t)) {
        recursive(t.to, varName, paths, path, /* lastPathLength, */ maxSize);
      }
    }

    if (startNode.getBaseElement() != null) {
      // save the path, if the the search has reached the begin of the process
      if (startNode.getBaseElement().getElementType().getTypeName().equals("startEvent")
          && startNode.getBaseElement().getParentElement().getElementType().getTypeName()
              .equals("process")
          && paths.size() < maxSize) {
        final List<BpmnElement> newPath = new ArrayList<BpmnElement>(path);
        paths.add(new Path(newPath));
        // lastPathLength = newPath.size();
      }
    }

    path.remove(startNode);
  }

  public String toString() {
    Set<BpmnElement> keys = adjacencyList.keySet();
    String str = "digraph G {\n";

    for (BpmnElement v : keys) {
      str += " ";

      List<Edge> edgeList = adjacencyList.get(v);

      for (Edge edge : edgeList) {
        str += edge;
        str += "\n";
      }
    }
    str += "}";
    return str;
  }

  protected final void clearVertexInfo() {
    for (VertexInfo info : this.vertexInfo.values()) {
      info.clear();
    }
  }
}
