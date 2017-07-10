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

package com.blueprint.centromere.core.dataimport.filter;

import com.blueprint.centromere.core.dataimport.DataImportComponent;
import com.blueprint.centromere.core.model.Model;

/**
 * @author woemler
 */
public interface Filter<T extends Model<?>> extends DataImportComponent {

  /**
   * Tests the model record to see if it should be removed from the import process. 
   * 
   * @param record record to be tested
   * @return true if the record should be excluded
   */
  boolean isFilterable(T record);
  
}
