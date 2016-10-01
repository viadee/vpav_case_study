package de.viadee.bpm.vPAV.config.reader;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "elementFieldTypes")
@XmlType(propOrder = { "elementFieldTypes", "excluded" })
public class XmlElementFieldTypes {

  private Collection<String> elementFieldTypes;

  private boolean excluded;

  public XmlElementFieldTypes() {
  }

  public XmlElementFieldTypes(final Collection<String> elementFieldTypes, final boolean excluded) {
    super();
    this.elementFieldTypes = elementFieldTypes;
    this.excluded = excluded;
  }

  @XmlElement(name = "elementFieldType", required = false)
  public Collection<String> getElementFieldTypes() {
    return elementFieldTypes;
  }

  @XmlAttribute(name = "excluded", required = false)
  public boolean isExcluded() {
    return excluded;
  }

  public void setElementFieldTypes(Collection<String> elementFieldTypes) {
    this.elementFieldTypes = elementFieldTypes;
  }

  public void setExcluded(boolean excluded) {
    this.excluded = excluded;
  }
}
