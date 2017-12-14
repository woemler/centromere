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

package com.blueprint.centromere.core.commons.processor;

import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.reader.GenericSampleReader;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.validator.SampleValidator;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;
import java.io.Serializable;

/**
 * @author woemler
 */
@DataTypes(value = "generic_samples", description = "Generic sample metadata")
public class GenericSampleProcessor<T extends Sample<ID>, ID extends Serializable> 
    extends GenericRecordProcessor<T> {

    public GenericSampleProcessor(
        Class<T> model,
        SampleRepository<T, ID> sampleRepository,
        DataImportProperties dataImportProperties
    ) {
      this.setModel(model);
      this.setReader(new GenericSampleReader<>(model, dataImportProperties));
      this.setValidator(new SampleValidator());
      this.setWriter(new RepositoryRecordWriter<>(sampleRepository));
    }

}
