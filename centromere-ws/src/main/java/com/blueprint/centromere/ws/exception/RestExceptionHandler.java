package com.blueprint.centromere.ws.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception handler for {@link RestException} errors. Should be
 *   implemented as {@link org.springframework.web.bind.annotation.ControllerAdvice}.
 *
 * @author woemler
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Catches a {@link RestException} thrown bt a web service
     *   controller and returns an informative message.
     *
     * @param ex {@link RestException}
     * @param request {@link WebRequest}
     * @return {@link RestError}
     */
    @ExceptionHandler(value = { RestException.class })
    public ResponseEntity<RestError> handleRestException(RestException ex, WebRequest request) {
        RestError restError = ex.getRestError();
        return new ResponseEntity<>(restError, restError.getStatus());
    }

}
