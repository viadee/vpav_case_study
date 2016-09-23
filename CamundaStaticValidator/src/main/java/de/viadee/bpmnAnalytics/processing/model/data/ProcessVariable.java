package de.viadee.bpmnAnalytics.processing.model.data;

/**
 * Represents a process variable with some meaningful information.
 *
 */
public class ProcessVariable {

  private String name;

  private VariableOperation operation;

  private String scopeId;

  /** Detailed Information about the location of the match **/
  private BpmnElement element;

  private String resourceFilePath;

  private ElementChapter chapter;

  private KnownElementFieldType fieldType;

  public ProcessVariable(final String name, final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String resourceFilePath,
      final VariableOperation operation, final String scopeId) {
    super();
    this.name = name;
    this.element = element;
    this.resourceFilePath = resourceFilePath;
    this.chapter = chapter;
    this.fieldType = fieldType;
    this.operation = operation;
    this.scopeId = scopeId;
  }

  public String getName() {
    return name;
  }

  public String getResourceFilePath() {
    return resourceFilePath;
  }

  public BpmnElement getElement() {
    return element;
  }

  public ElementChapter getChapter() {
    return chapter;
  }

  public KnownElementFieldType getFieldType() {
    return fieldType;
  }

  public VariableOperation getOperation() {
    return operation;
  }

  public String getScopeId() {
    return scopeId;
  }

  public String toString() {
    return name + " [" + element.getProcessdefinition() + ", " + element.getBaseElement().getId()
        + ", Scope: " + scopeId + ", " + chapter.name() + ", " + fieldType.getDescription() + ", "
        + resourceFilePath + "]";
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof ProcessVariable) {
      final ProcessVariable p = (ProcessVariable) o;
      if (name.equals(p.getName())) {
        return true;
      }
    }
    return false;
  }
}
