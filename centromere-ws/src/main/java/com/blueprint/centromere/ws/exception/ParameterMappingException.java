package com.blueprint.centromere.ws.exception;

import org.springframework.http.HttpStatus;

/**
 * @author woemler
 */
public class ParameterMappingException extends RestException {
  public ParameterMappingException(String message) {
    super(HttpStatus.BAD_REQUEST, 400, message);
  }
}
