package com.blueprint.centromere.core.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * Base exception class for web services.  Generates a {@link RestError} object, that will be returned
 *   in the HTTP response when the exception is thrown.
 *
 * @author woemler
 */
public class RestException extends RuntimeException {

  private HttpStatus status;
  private Integer code;
  private String message;

  public RestException(HttpStatus status, Integer code, String message){
    Assert.notNull(status,"HttpStatus argument cannot be null.");
    this.status = status;
    this.code = code;
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public RestError getRestError(){
    return new RestError(status, code, message);
  }

}
