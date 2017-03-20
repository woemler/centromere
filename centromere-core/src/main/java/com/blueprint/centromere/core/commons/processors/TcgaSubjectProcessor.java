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

package com.blueprint.centromere.core.commons.processors;

import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.readers.TcgaSubjectReader;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.core.commons.validators.SubjectValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.RepositoryRecordWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes({ "tcga_subjects" })
@Component
public class TcgaSubjectProcessor extends CommonsDataProcessor<Subject> {

    @Autowired
    public TcgaSubjectProcessor(SubjectRepository repository) {
        this.setModel(Subject.class);
        this.setReader(new TcgaSubjectReader());
        this.setValidator(new SubjectValidator());
        this.setWriter(new RepositoryRecordWriter<>(repository));
    }

}
