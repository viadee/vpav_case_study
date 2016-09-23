package de.viadee.bpm.camundaStaticValidator.config.reader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "elementConvention")
@XmlType(propOrder = { "name", "elementFieldTypes", "pattern" })
public class XmlElementConvention {

  private String name;

  private XmlElementFieldTypes elementFieldTypes;

  private String pattern;

  public XmlElementConvention() {
  }

  public XmlElementConvention(final String name, final XmlElementFieldTypes elementFieldTypes,
      final String pattern) {
    super();
    this.name = name;
    this.elementFieldTypes = elementFieldTypes;
    this.pattern = pattern;
  }

  @XmlElement(name = "name", required = true)
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @XmlElement(name = "elementFieldTypes", required = false)
  public XmlElementFieldTypes getElementFieldTypes() {
    return elementFieldTypes;
  }

  public void setElementFieldTypes(final XmlElementFieldTypes elementFieldTypes) {
    this.elementFieldTypes = elementFieldTypes;
  }

  @XmlElement(name = "pattern", required = true)
  public String getPattern() {
    return pattern;
  }

  public void setPattern(final String pattern) {
    this.pattern = pattern;
  }
}
