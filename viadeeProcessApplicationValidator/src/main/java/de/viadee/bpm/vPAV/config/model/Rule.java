package de.viadee.bpm.vPAV.config.model;

import java.util.Collection;
import java.util.Map;

public class Rule {

  private String name;

  private boolean isActive;

  private Map<String, Setting> settings;

  private Collection<ElementConvention> elementConventions;

  private Collection<ModelConvention> modelConventions;

  public Rule(final String name, final boolean isActive, final Map<String, Setting> settings,
      final Collection<ElementConvention> elementConventions,
      final Collection<ModelConvention> modelConventions) {
    super();
    this.name = name;
    this.isActive = isActive;
    this.settings = settings;
    this.elementConventions = elementConventions;
    this.modelConventions = modelConventions;
  }

  public String getName() {
    return name;
  }

  public boolean isActive() {
    return isActive;
  }

  public Map<String, Setting> getSettings() {
    return settings;
  }

  public Collection<ElementConvention> getElementConventions() {
    return elementConventions;
  }

  public Collection<ModelConvention> getModelConventions() {
    return modelConventions;
  }
}
