package de.viadee.bpm.vPAV.config.reader;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "setting")
@XmlType(propOrder = { "name", "value" })
public class XmlSetting {

  private String name;

  private String value;

  public XmlSetting() {
  }

  public XmlSetting(final String name, final String value) {
    super();
    this.name = name;
    this.value = value;
  }

  @XmlAttribute(name = "name", required = true)
  public String getName() {
    return name;
  }

  @XmlValue
  public String getValue() {
    return value;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
