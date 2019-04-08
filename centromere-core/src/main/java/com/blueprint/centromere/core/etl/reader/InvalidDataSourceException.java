/*
 * Copyright 2019 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.etl.reader;

import com.blueprint.centromere.core.exceptions.DataProcessingException;

/**
 * Exception thrown when the supplied file or resource is inaccessible or of the wrong type.
 *
 * @author woemler
 */
public class InvalidDataSourceException extends DataProcessingException {

    public InvalidDataSourceException() {
    }

    public InvalidDataSourceException(String message) {
        super(message);
    }

    public InvalidDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataSourceException(Throwable cause) {
        super(cause);
    }

    public InvalidDataSourceException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
