package de.viadee.bpmnAnalytics.processing.model.data;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import de.viadee.bpmnAnalytics.processing.model.graph.Path;

/**
 * Class for holding issues (errors, warnings, infos) from the checkers
 * 
 */
public class CheckerIssue {

  private String ruleName;

  private CriticalityEnum classification;

  private String bpmnFile;

  private String resourceFile;

  private String elementId;

  private String variable;

  private List<Path> invalidPaths;

  private String message;

  public CheckerIssue() {
  }

  public CheckerIssue(final String ruleName, final CriticalityEnum classification,
      final String bpmnFile, final String resourceFile, final String elementId,
      final String variable, final List<Path> invalidPaths, final String message) {
    super();
    this.ruleName = ruleName;
    this.variable = variable;
    this.invalidPaths = invalidPaths;
    this.classification = classification;
    this.bpmnFile = bpmnFile;
    this.resourceFile = resourceFile;
    this.elementId = elementId;
    this.message = message;
  }

  public String getId() {
    return getMD5(
        ruleName + "_" + bpmnFile + "_" + resourceFile + "_" + elementId + "_" + variable);
  }

  public String getRuleName() {
    return ruleName;
  }

  public String getVariable() {
    return variable;
  }

  public List<Path> getInvalidPaths() {
    return invalidPaths;
  }

  public CriticalityEnum getClassification() {
    return classification;
  }

  public String getBpmnFile() {
    return bpmnFile;
  }

  public String getResourceFile() {
    return resourceFile;
  }

  public String getElementId() {
    return elementId;
  }

  public String getMessage() {
    return message;
  }

  public void setClassification(final CriticalityEnum classification) {
    this.classification = classification;
  }

  public void setBpmnFile(final String bpmnFile) {
    this.bpmnFile = bpmnFile;
  }

  public void setElementId(final String elementId) {
    this.elementId = elementId;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public static String getMD5(String input) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] messageDigestByteArray = messageDigest.digest(input.getBytes());
      BigInteger number = new BigInteger(1, messageDigestByteArray);
      String hashtext = number.toString(16);
      // Now we need to zero pad it if you actually want the full 32 chars.
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
