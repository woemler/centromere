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

package com.blueprint.centromere.core.dataimport;

import com.blueprint.centromere.core.config.Properties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * Helper class for handling common environment property checks in data import classes.
 *
 * @author woemler
 * @since 0.5.0
 */
@Deprecated
public interface EnvironmentPropertySupport extends EnvironmentAware {

  Environment getEnvironment();

  default boolean isSkipInvalidGenes() {
    return this.getEnvironment().getRequiredProperty(Properties.SKIP_INVALID_GENES, Boolean.class);
  }

  default boolean isInvalidGene(Object gene) throws DataImportException {
    if (gene == null){
      if (isSkipInvalidGenes()){
        return true;
      } else {
        throw new DataImportException("Gene object is null.");
      }
    } else {
      return false;
    }
  }

  default boolean isSkipInvalidRecords() {
    return this.getEnvironment().getRequiredProperty(Properties.SKIP_INVALID_RECORDS, Boolean.class);
  }

  default boolean isInvalidRecord(Object record) throws DataImportException {
    if (record == null){
      if (isSkipInvalidRecords()){
        return true;
      } else {
        throw new DataImportException("Record object is null.");
      }
    } else {
      return false;
    }
  }

  default boolean isSkipInvalidSamples() {
    return this.getEnvironment().getRequiredProperty(Properties.SKIP_INVALID_SAMPLES, Boolean.class);
  }

  default boolean isInvalidSample(Object sample) throws DataImportException {
    if (sample == null){
      if (isSkipInvalidSamples()){
        return true;
      } else {
        throw new DataImportException("Sample object is null.");
      }
    } else {
      return false;
    }
  }

}
