package com.blueprint.centromere.ws.exception;

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
