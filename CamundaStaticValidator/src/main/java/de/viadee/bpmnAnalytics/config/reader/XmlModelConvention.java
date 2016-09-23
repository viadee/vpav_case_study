package de.viadee.bpmnAnalytics.config.reader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "modelConvention")
@XmlType(propOrder = { "name", "pattern" })
public class XmlModelConvention {

  private String name;

  private String pattern;

  public XmlModelConvention() {
  }

  public XmlModelConvention(final String name, final String pattern) {
    super();
    this.name = name;
    this.pattern = pattern;
  }

  @XmlElement(name = "name", required = true)
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @XmlElement(name = "pattern", required = true)
  public String getPattern() {
    return pattern;
  }

  public void setPattern(final String pattern) {
    this.pattern = pattern;
  }
}
