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

package com.blueprint.centromere.core.etl;

import com.blueprint.centromere.core.exceptions.DataProcessingException;
import java.io.File;
import java.util.Map;

/**
 * Defines the basic methods that all data processing pipeline components must implement.  In short,
 *   every component should have methods that run before and after data processing occurs. These 
 *   methods should be aware of the file being processed (if any) and any arguments modulating the
 *   data processing.
 * 
 * @author woemler
 */
public interface PipelineComponent {

  /**
   * Runs before the primary data processing method executes.
   * 
   * @param file file to be processed
   * @param args arguments to apply to data processing
   * @throws DataProcessingException
   */
  void doBefore(File file, Map<String, String> args) throws DataProcessingException;

  /**
   * Runs after the primary data processing method completes successfully.
   *
   * @param file file to be processed
   * @param args arguments to apply to data processing
   * @throws DataProcessingException
   */
  void doOnSuccess(File file, Map<String, String> args) throws DataProcessingException;

  /**
   * Runs if the data processing step fails to properly execute.
   *
   * @param file file to be processed
   * @param args arguments to apply to data processing
   * @throws DataProcessingException
   */
  void doOnFailure(File file, Map<String, String> args) throws DataProcessingException;

}
