package de.zpid.datawiz.exceptions;

import de.zpid.datawiz.enumeration.DataWizErrorCodes;

public class DataWizSystemException extends Exception {

  private static final long serialVersionUID = 8028749143659397286L;
  private DataWizErrorCodes error;

  public DataWizSystemException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public DataWizSystemException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

  public DataWizSystemException(String message, DataWizErrorCodes error, Throwable cause) {
    super(message, cause);
    this.error = error;
    // TODO Auto-generated constructor stub
  }

  public DataWizSystemException(String message, DataWizErrorCodes error) {
    super(message);
    this.error = error;
    // TODO Auto-generated constructor stub
  }

  public DataWizSystemException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  public DataWizErrorCodes getErrorCode() {
    return error;
  }

}
