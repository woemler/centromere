package com.blueprint.centromere.ws.exception;

import org.springframework.http.HttpStatus;

/**
 * 404 error when a single resource request returns null
 *
 * @author woemler
 */
public class ResourceNotFoundException extends RestException {
    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, 404, "The requested resource could not be found.");
    }
}
