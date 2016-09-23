package de.viadee.bpmnAnalytics.config.reader;

public class ConfigReaderException extends Exception {

  public ConfigReaderException(final Throwable e) {
    super(e);
  }

  public ConfigReaderException(final String message) {
    super(message);
  }
}
