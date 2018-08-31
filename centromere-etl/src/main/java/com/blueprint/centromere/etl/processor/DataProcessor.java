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

package com.blueprint.centromere.etl.processor;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import com.blueprint.centromere.etl.DataImportException;
import com.blueprint.centromere.etl.PipelineComponent;
import java.io.File;
import java.util.Map;

/**
 * Core data import component interface.  Defines the API for handling the processing of a single file. 
 *   This API makes no assumptions about what the final state of the input data should be, but rather
 *   provides a framework for steps in processing data.
 * 
 * @author woemler
 * @since 0.6.0
 */
public interface DataProcessor<T extends Model<?>> extends PipelineComponent, ModelSupport<T> {
  
  void processFile(File file, Map<String, String> args) throws DataImportException;
  
}
