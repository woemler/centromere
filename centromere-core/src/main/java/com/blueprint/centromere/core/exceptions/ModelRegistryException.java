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

import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;

/**
 * Exception thrown when {@link ModelRepositoryRegistry} or other registry throws an exception.
 *
 * @author woemler
 */
public class ModelRegistryException extends ConfigurationException {

  public ModelRegistryException() {
  }

  public ModelRegistryException(String message) {
    super(message);
  }

  public ModelRegistryException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModelRegistryException(Throwable cause) {
    super(cause);
  }

  public ModelRegistryException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
