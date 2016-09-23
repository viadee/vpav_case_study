package de.viadee.bpm.camundaStaticValidator.config.model;

public class ElementConvention {

  private String name;

  private ElementFieldTypes elementFieldTypes;

  private String pattern;

  public ElementConvention(final String name, final ElementFieldTypes elementFieldTypes,
      final String pattern) {
    super();
    this.name = name;
    this.elementFieldTypes = elementFieldTypes;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public ElementFieldTypes getElementFieldTypes() {
    return elementFieldTypes;
  }

  public String getPattern() {
    return pattern;
  }
}
