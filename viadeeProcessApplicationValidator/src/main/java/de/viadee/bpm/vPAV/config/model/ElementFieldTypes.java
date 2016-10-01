package de.viadee.bpm.vPAV.config.model;

import java.util.Collection;

public class ElementFieldTypes {

  private Collection<String> elementFieldTypes;

  private boolean excluded;

  public ElementFieldTypes(final Collection<String> elementFieldTypes, final boolean excluded) {
    super();
    this.elementFieldTypes = elementFieldTypes;
    this.excluded = excluded;
  }

  public Collection<String> getElementFieldTypes() {
    return elementFieldTypes;
  }

  public boolean isExcluded() {
    return excluded;
  }
}
