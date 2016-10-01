package de.viadee.bpm.vPAV.config.model;

public class Setting {

  private String name;

  private String value;

  public Setting(final String name, final String value) {
    super();
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
