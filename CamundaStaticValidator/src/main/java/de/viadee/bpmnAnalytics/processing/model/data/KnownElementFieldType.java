package de.viadee.bpmnAnalytics.processing.model.data;

/**
 * known element field types
 */
public enum KnownElementFieldType {
  Class("Class"), FormField("Form Field"), Expression("Expression"), DelegateExpression(
      "Delegate Expression"), ResultVariable("Result Variable"), CalledElement(
          "Called Element"), CaseRef("Case Ref"), CollectionElement("Collection"), ElementVariable(
              "Element Variable"), LoopCardinality("Loop Cardinality"), CompletionCondition(
                  "Completion Condition"), InlineScript("Inline Script"), ExternalScript(
                      "External Script"), Assignee("Assignee"), CandidateUsers(
                          "Candidate Users"), CandidateGroups("Candidate Groups"), DueDate(
                              "Due Date"), FollowUpDate("Follow Up Date"), DMN(
                                  "DMN"), CamundaIn("Camunda:In"), CamundaOut("Camunda:Out");

  private String description;

  private KnownElementFieldType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
