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

import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.model.Model;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
public abstract class BasicFilter<T extends Model<?>> implements Filter<T> {

  private ImportOptions importOptions;

  /**
   * To be executed before the main component method is first called.  Can be configured to handle
   * a variety of tasks using flexible input parameters.
   */
  @Override
  public void doBefore() throws DataImportException {
    try {
      Assert.notNull(importOptions, "ImportOptions not set.");
    } catch (Exception e){
      throw new DataImportException(e);
    }
  }

  @Override
  public ImportOptions getImportOptions() {
    return importOptions;
  }

  @Override
  public void setImportOptions(ImportOptions importOptions) {
    this.importOptions = importOptions;
  }
}
