/*
 * Copyright 2017 the original author or authors
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

package com.blueprint.centromere.core.dataimport.exception;

/**
 * @author woemler
 * @since 0.5.0
 */
public class InvalidDataFileException extends DataImportException {

  public InvalidDataFileException() {
  }

  public InvalidDataFileException(String message) {
    super(message);
  }

  public InvalidDataFileException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidDataFileException(Throwable cause) {
    super(cause);
  }

  public InvalidDataFileException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}