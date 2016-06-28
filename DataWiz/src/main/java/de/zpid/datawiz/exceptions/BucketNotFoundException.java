package de.zpid.datawiz.exceptions;

public class BucketNotFoundException extends Exception {

  private static final long serialVersionUID = 6805413357763795084L;

  public BucketNotFoundException() {
    super();
  }

  public BucketNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public BucketNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public BucketNotFoundException(String message) {
    super(message);
  }

  public BucketNotFoundException(Throwable cause) {
    super(cause);
  }

}
