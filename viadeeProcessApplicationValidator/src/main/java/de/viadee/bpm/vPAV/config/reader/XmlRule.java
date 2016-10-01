package de.viadee.bpm.vPAV.config.reader;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * dient dazu...
 * 
 * Annahmen und Voraussetzungen sind...
 *
 */
@XmlRootElement(name = "rule")
@XmlType(propOrder = { "name", "state", "settings", "elementConventions", "modelConventions" })
public class XmlRule {

  private String name;

  private boolean state;

  private Collection<XmlSetting> settings;

  private Collection<XmlElementConvention> elementConventions;

  private Collection<XmlModelConvention> modelConventions;

  public XmlRule() {
  }

  public XmlRule(String name, boolean state, final Collection<XmlSetting> settings,
      final Collection<XmlElementConvention> elementConventions,
      final Collection<XmlModelConvention> modelConventions) {
    super();
    this.name = name;
    this.state = state;
    this.settings = settings;
    this.elementConventions = elementConventions;
    this.modelConventions = modelConventions;
  }

  @XmlElement(name = "name", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlElement(name = "state", required = true)
  public boolean isState() {
    return state;
  }

  public void setState(boolean state) {
    this.state = state;
  }

  @XmlElementWrapper(name = "settings")
  @XmlElement(name = "setting", required = false)
  public Collection<XmlSetting> getSettings() {
    return settings;
  }

  public void setSettings(Collection<XmlSetting> settings) {
    this.settings = settings;
  }

  @XmlElementWrapper(name = "elementConventions")
  @XmlElement(name = "elementConvention", required = false)
  public Collection<XmlElementConvention> getElementConventions() {
    return elementConventions;
  }

  public void setElementConventions(Collection<XmlElementConvention> elementConventions) {
    this.elementConventions = elementConventions;
  }

  @XmlElementWrapper(name = "modelConventions")
  @XmlElement(name = "modelConvention", required = false)
  public Collection<XmlModelConvention> getModelConventions() {
    return modelConventions;
  }

  public void setModelConventions(Collection<XmlModelConvention> modelConventions) {
    this.modelConventions = modelConventions;
  }
}
