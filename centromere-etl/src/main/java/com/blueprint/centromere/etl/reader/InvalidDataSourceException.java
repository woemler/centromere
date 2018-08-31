package com.blueprint.centromere.etl.reader;

import com.blueprint.centromere.etl.DataImportException;

/**
 * @author woemler
 */
public class InvalidDataSourceException extends DataImportException {

  public InvalidDataSourceException() {
  }

  public InvalidDataSourceException(String message) {
    super(message);
  }

  public InvalidDataSourceException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidDataSourceException(Throwable cause) {
    super(cause);
  }

  public InvalidDataSourceException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
