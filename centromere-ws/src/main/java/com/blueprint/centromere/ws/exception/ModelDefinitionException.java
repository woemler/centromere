package com.blueprint.centromere.ws.exception;

import org.springframework.http.HttpStatus;

/**
 * @author woemler
 */
public class ModelDefinitionException extends RestException {

    public ModelDefinitionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, 500, message);
    }
}
