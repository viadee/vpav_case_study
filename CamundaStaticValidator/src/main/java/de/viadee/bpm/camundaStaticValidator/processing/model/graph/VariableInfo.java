package de.viadee.bpm.camundaStaticValidator.processing.model.graph;

import de.viadee.bpm.camundaStaticValidator.processing.model.data.ProcessVariable;

/**
 * Info for the searched process variable
 */
public class VariableInfo {

  public ProcessVariable var;

  boolean visited;

  public VariableInfo(final ProcessVariable var) {
    this.var = var;
    this.clear();
  }

  /** Resets the visited field. */
  public void clear() {
    this.visited = false;
  }
}
