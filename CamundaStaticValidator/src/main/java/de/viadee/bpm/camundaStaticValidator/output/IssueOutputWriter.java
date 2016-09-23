package de.viadee.bpm.camundaStaticValidator.output;

import java.util.Collection;

import de.viadee.bpm.camundaStaticValidator.processing.model.data.CheckerIssue;

public interface IssueOutputWriter {

  void write(final Collection<CheckerIssue> issues) throws OutputWriterException;

}
