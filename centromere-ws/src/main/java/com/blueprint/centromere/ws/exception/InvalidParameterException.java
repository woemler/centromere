package com.blueprint.centromere.ws.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown by invalid query string options for GET request queries.
 *
 * @author woemler
 */
public class InvalidParameterException extends RestException {
  public InvalidParameterException(String message) {
    super(HttpStatus.BAD_REQUEST, 400, message);
  }
}
