package de.viadee.bpm.camundaStaticValidator.config.reader;

public class ConfigReaderException extends Exception {

  private static final long serialVersionUID = 7310325755339963999L;

  public ConfigReaderException(final Throwable e) {
    super(e);
  }

  public ConfigReaderException(final String message) {
    super(message);
  }
}
