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

package com.blueprint.centromere.core.config;

import java.util.Optional;

/**
 * @author woemler
 */
@Deprecated
public interface ApplicationOptions {

  /**
   * Fetches an application parameter, returning it is an {@link Optional}, in case it is not defined.
   * 
   * @param name parameter name
   * @return parameter value
   */
  Optional<String> getParameter(String name);

  /**
   * Fetches an application parameter, returning it is an {@link Optional}, in case it is not defined.
   *   Tries to convert it from the default string representation to the requested type. 
   *
   * @param name parameter name
   * @param type parameter type
   * @return parameter value
   */
  <S> Optional<S> getParameter(String name, Class<S> type);

  /**
   * Fetches a boolean application property, with a default value of false if it is not found.
   * 
   * @param name parameter name
   * @return boolean
   */
  boolean getBooleanParameter(String name);

  /**
   * Fetches a boolean application property, with a provided default value, in case it is not defined.
   *
   * @param name parameter name
   * @return boolean
   */
  boolean getBooleanParameter(String name, boolean defaultValue);

}
