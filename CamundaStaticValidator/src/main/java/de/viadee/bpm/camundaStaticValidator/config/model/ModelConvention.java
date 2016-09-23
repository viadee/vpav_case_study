package de.viadee.bpm.camundaStaticValidator.config.model;

public class ModelConvention {

  private String name;

  private String pattern;

  public ModelConvention(final String name, final String pattern) {
    super();
    this.name = name;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public String getPattern() {
    return pattern;
  }
}
