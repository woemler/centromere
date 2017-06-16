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

import com.blueprint.centromere.core.commons.model.GeneCopyNumber;
import com.blueprint.centromere.core.commons.reader.GenericGeneCopyNumberMatrixReader;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.commons.validator.GeneCopyNumberValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.importer.MongoImportTempFileImporter;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.MongoImportTempFileWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes(value = { "gene_copy_number" }, description = "Gene-normalized copy number values, in a sample-gene matrix format.")
@Component
public class GenericGeneCopyNumberMatrixProcessor extends GenericRecordProcessor<GeneCopyNumber> {

  @Autowired
  public GenericGeneCopyNumberMatrixProcessor(
      SampleRepository sampleRepository,
      SubjectRepository subjectRepository,
      GeneRepository geneRepository,
      MongoTemplate mongoTemplate,
      Environment environment
  ) {
    this.setReader(new GenericGeneCopyNumberMatrixReader(subjectRepository, sampleRepository, geneRepository));
    this.setValidator(new GeneCopyNumberValidator());
    this.setWriter(new MongoImportTempFileWriter<>(GeneCopyNumber.class, mongoTemplate));
    this.setImporter(new MongoImportTempFileImporter<>(GeneCopyNumber.class, environment));
    this.setModel(GeneCopyNumber.class);
  }
  
}
