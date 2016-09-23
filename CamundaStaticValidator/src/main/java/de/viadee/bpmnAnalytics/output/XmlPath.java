package de.viadee.bpmnAnalytics.output;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "path")
public class XmlPath {

  private Collection<String> elementIds;

  public XmlPath() {
  }

  public XmlPath(final Collection<String> elementIds) {
    this.elementIds = elementIds;
  }

  @XmlElement(name = "elementId", required = false)
  public Collection<String> getElementIds() {
    return elementIds;
  }

  public void setElementIds(final Collection<String> elementIds) {
    this.elementIds = elementIds;
  }
}