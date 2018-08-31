/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.core.exceptions;

/**
 * Generic exception to throw when a model fails to persist due to constraint errors.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class ModelPersistenceException extends RuntimeException {

  public ModelPersistenceException() {
  }

  public ModelPersistenceException(String message) {
    super(message);
  }

  public ModelPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModelPersistenceException(Throwable cause) {
    super(cause);
  }

  public ModelPersistenceException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
