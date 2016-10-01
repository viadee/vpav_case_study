package de.viadee.bpm.vPAV.output;

import java.util.Collection;

import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

public interface IssueOutputWriter {

  void write(final Collection<CheckerIssue> issues) throws OutputWriterException;

}
