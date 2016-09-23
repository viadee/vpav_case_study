package de.viadee.bpmnAnalytics.output;

import java.util.Collection;

import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;

public interface IssueOutputWriter {

  void write(final Collection<CheckerIssue> issues) throws OutputWriterException;

}
