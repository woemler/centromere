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
import com.blueprint.centromere.core.commons.reader.TcgaSampleReader;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.validator.SampleValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes(value = "tcga_samples", description = "TCGA sample metadata")
@Component
public class TcgaSampleProcessor<T extends Sample<ID>, ID extends Serializable> 
    extends GenericRecordProcessor<T> {

    @Autowired
    public TcgaSampleProcessor(Class<T> model, SampleRepository<T, ID> repository) {
      this.setModel(model);
      this.setReader(new TcgaSampleReader<>(model));
      this.setValidator(new SampleValidator());
      this.setWriter(new RepositoryRecordWriter<>(repository));
        
    }

}
