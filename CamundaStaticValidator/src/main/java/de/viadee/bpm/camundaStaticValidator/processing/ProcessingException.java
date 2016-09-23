package de.viadee.bpm.camundaStaticValidator.processing;

public class ProcessingException extends RuntimeException {

  public ProcessingException(final String message, Throwable e) {
    super(message, e);
  }

  public ProcessingException(final String message) {
    super(message);
  }
}
