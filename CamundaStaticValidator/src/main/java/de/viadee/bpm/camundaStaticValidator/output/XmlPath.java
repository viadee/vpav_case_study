package de.viadee.bpm.camundaStaticValidator.output;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "path")
public class XmlPath {

  private Collection<XmlPathElement> elements;

  public XmlPath() {
  }

  public XmlPath(final Collection<XmlPathElement> elements) {
    this.elements = elements;
  }

  @XmlElement(name = "element", required = false)
  public Collection<XmlPathElement> getElements() {
    return elements;
  }

  public void setElements(final Collection<XmlPathElement> elements) {
    this.elements = elements;
  }
}