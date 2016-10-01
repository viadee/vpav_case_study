package de.viadee.bpm.vPAV.config.reader;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ruleSet")
public class XmlRuleSet {

  private Collection<XmlRule> rules;

  public XmlRuleSet() {
  }

  public XmlRuleSet(Collection<XmlRule> rules) {
    super();
    this.rules = rules;
  }

  @XmlElement(name = "rule", type = XmlRule.class)
  public Collection<XmlRule> getRules() {
    return rules;
  }

  public void setRules(Collection<XmlRule> rules) {
    this.rules = rules;
  }
}
