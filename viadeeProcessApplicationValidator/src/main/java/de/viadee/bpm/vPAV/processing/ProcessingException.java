package de.viadee.bpm.vPAV.processing;

public class ProcessingException extends RuntimeException {

  private static final long serialVersionUID = -7507728646377465787L;

  public ProcessingException(final String message, Throwable e) {
    super(message, e);
  }

  public ProcessingException(final String message) {
    super(message);
  }
}
