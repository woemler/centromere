package com.blueprint.centromere.ws.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * Simple representation of a web services exception.  Returns to the user an HTTP status code,
 *   API-specific error code, user message, developer message, and URL for more information.
 *
 * @author woemler
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestError {

    private final HttpStatus status;
    private final Integer code;
    private final String message;

    public RestError(HttpStatus status, Integer code, String message) {
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

}
