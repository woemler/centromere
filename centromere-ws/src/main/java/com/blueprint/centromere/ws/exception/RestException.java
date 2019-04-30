package com.blueprint.centromere.ws.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * Base exception class for web services.  Generates a {@link RestError} object, that will be
 * returned in the HTTP response when the exception is thrown.
 *
 * @author woemler
 */
public class RestException extends RuntimeException {

    private final HttpStatus status;
    private final Integer code;
    private final String message;

    public RestException(HttpStatus status, Integer code, String message) {
        Assert.notNull(status, "HttpStatus argument cannot be null.");
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public RestError getRestError() {
        return new RestError(status, code, message);
    }

}
