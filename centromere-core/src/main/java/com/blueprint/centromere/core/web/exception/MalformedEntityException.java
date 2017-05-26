package com.blueprint.centromere.core.web.exception;

import org.springframework.http.HttpStatus;

/**
 * 406 error
 *
 * @author woemler
 */
public class MalformedEntityException extends RestException {
  public MalformedEntityException(String message) {
    super(HttpStatus.NOT_ACCEPTABLE, 406, message);
  }
}
