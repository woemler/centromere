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

package com.blueprint.centromere.core.dataimport.impl.importer;

import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.blueprint.centromere.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * @author woemler
 * @since 0.5.0
 */
public abstract class AbstractFileImporter<T extends Model<?>> implements RecordImporter {

  private final static Logger logger = LoggerFactory.getLogger(AbstractFileImporter.class);
  
  private final Class<T> model;
  private ImportOptions options = new ImportOptionsImpl();

  public AbstractFileImporter(Class<T> model) {
    this.model = model;
  }

  /**
   * Empty default implementation.  The purpose of extending {@link org.springframework.beans.factory.InitializingBean} 
   * is to trigger bean post-processing by a {@link BeanPostProcessor}.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(model, "Model must not be null");
  }

  @Override
  public ImportOptions getImportOptions() {
    return options;
  }

  @Override
  public void setImportOptions(ImportOptions options) {
    this.options = options;
  }

  public Class<T> getModel(){
    return model;
  }

}
