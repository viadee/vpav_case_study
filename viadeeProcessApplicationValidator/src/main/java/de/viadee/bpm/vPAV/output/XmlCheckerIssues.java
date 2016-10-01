package de.viadee.bpm.vPAV.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "issues")
public class XmlCheckerIssues {

  private List<XmlCheckerIssue> issues = new ArrayList<XmlCheckerIssue>();

  public XmlCheckerIssues() {
  }

  public XmlCheckerIssues(final List<XmlCheckerIssue> issues) {
    this.issues = issues;
  }

  @XmlElement(name = "issue", type = XmlCheckerIssue.class)
  public Collection<XmlCheckerIssue> getIssues() {
    return issues;
  }

  public void setIssues(final List<XmlCheckerIssue> issues) {
    this.issues = issues;
  }

  public void addIssue(final XmlCheckerIssue issue) {
    this.issues.add(issue);
  }

  public void addIssues(final Collection<XmlCheckerIssue> issues) {
    this.issues.addAll(issues);
  }
}
