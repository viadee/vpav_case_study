package de.viadee.bpm.camundaStaticValidator.output;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "issue")
@XmlType(propOrder = { "id", "ruleName", "bpmnFile", "resourceFile", "classification", "elementId",
    "elementName", "variable", "anomaly", "paths", "message" })
public class XmlCheckerIssue {

  private String id;

  private String ruleName;

  private String bpmnFile;

  private String resourceFile;

  private String variable;

  private String anomaly;

  private List<XmlPath> paths;

  private String classification;

  private String elementId;

  private String elementName;

  private String message;

  public XmlCheckerIssue() {
  }

  public XmlCheckerIssue(final String id, final String ruleName, final String classification,
      final String bpmnFile, final String resourceFile, final String elementId,
      final String elementName, final String message, final String variable, final String anomaly,
      final List<XmlPath> invalidPaths) {
    super();
    this.id = id;
    this.ruleName = ruleName;
    this.classification = classification;
    this.bpmnFile = bpmnFile;
    this.resourceFile = resourceFile;
    this.elementId = elementId;
    this.elementName = elementName;
    this.message = message;
    this.variable = variable;
    this.anomaly = anomaly;
    this.paths = invalidPaths;
  }

  @XmlElement(name = "id", required = true)
  public String getId() {
    return id;
  }

  @XmlElement(name = "ruleName", required = true)
  public String getRuleName() {
    return ruleName;
  }

  @XmlElement(name = "resourceFile", required = false)
  public String getResourceFile() {
    return resourceFile;
  }

  @XmlElement(name = "variable", required = false)
  public String getVariable() {
    return variable;
  }

  @XmlElement(name = "anomaly", required = false)
  public String getAnomaly() {
    return anomaly;
  }

  @XmlElementWrapper(name = "paths")
  @XmlElement(name = "path", required = false)
  public List<XmlPath> getPaths() {
    return paths;
  }

  @XmlElement(name = "classification", required = true)
  public String getClassification() {
    return classification;
  }

  @XmlElement(name = "bpmnFile", required = true)
  public String getBpmnFile() {
    return bpmnFile;
  }

  @XmlElement(name = "elementId", required = true)
  public String getElementId() {
    return elementId;
  }

  @XmlElement(name = "elementName", required = false)
  public String getElementName() {
    return elementName;
  }

  @XmlElement(name = "message", required = true)
  public String getMessage() {
    return message;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }

  public void setBpmnFile(String bpmnFile) {
    this.bpmnFile = bpmnFile;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  public void setResourceFile(String resourceFile) {
    this.resourceFile = resourceFile;
  }

  public void setVariable(String variable) {
    this.variable = variable;
  }

  public void setAnomaly(String anomaly) {
    this.anomaly = anomaly;
  }

  public void setPaths(List<XmlPath> paths) {
    this.paths = paths;
  }
}
