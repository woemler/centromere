package com.blueprint.centromere.core.web.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic 400 error
 *
 * @author woemler
 */
public class RequestFailureException extends RestException {
  public RequestFailureException(String message) {
    super(HttpStatus.BAD_REQUEST, 400, message);
  }
}
