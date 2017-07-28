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

import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.commons.reader.GenericSubjectReader;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.commons.validator.SubjectValidator;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes(value = "generic_subjects", description = "Generic subject metadata")
@Component
public class GenericSubjectProcessor extends GenericRecordProcessor<Subject> {

    @Autowired
    public GenericSubjectProcessor(
        SubjectRepository repository, 
        DataImportProperties dataImportProperties
    ) {
      this.setModel(Subject.class);
      this.setReader(new GenericSubjectReader(dataImportProperties));
      this.setValidator(new SubjectValidator());
      this.setWriter(new RepositoryRecordWriter<>(repository));
        
    }

}
