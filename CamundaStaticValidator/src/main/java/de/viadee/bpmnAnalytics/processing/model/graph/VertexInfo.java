package de.viadee.bpmnAnalytics.processing.model.graph;

import java.util.HashMap;
import java.util.Map;

import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;

/**
 * University of Washington, Computer Science & Engineering, Course 373, Winter 2011, Jessica Miller
 * 
 * A utility class that attaches some "bookkeeping" information to a vertex. Used when searching the
 * graph for a path between two vertices.
 */
public class VertexInfo {

  /** The vertex itself. */
  public BpmnElement v;

  /** A mark for whether this vertex has been visited. Useful for path searching. */
  public boolean visited;

  private Map<String, Void> visitedVariables;

  /** Constructs information for the given vertex. */
  public VertexInfo(final BpmnElement v) {
    this.v = v;
    this.visitedVariables = new HashMap<String, Void>();
    this.clear();
  }

  public void visitVariable(final String varName) {
    visitedVariables.put(varName, null);
  }

  public boolean variableVisited(final String varName) {
    return visitedVariables.containsKey(varName);
  }

  /** Resets the visited field. */
  public void clear() {
    this.visited = false;
  }
}