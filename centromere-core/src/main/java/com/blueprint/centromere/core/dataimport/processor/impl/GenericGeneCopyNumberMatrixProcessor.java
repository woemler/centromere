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

package com.blueprint.centromere.core.dataimport.processor.impl;

import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.processor.impl.validator.GeneCopyNumberValidator;
import com.blueprint.centromere.core.dataimport.reader.impl.GenericGeneCopyNumberMatrixReader;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter.WriteMode;
import com.blueprint.centromere.core.model.impl.GeneCopyNumber;
import com.blueprint.centromere.core.repository.impl.GeneCopyNumberRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@Component
@DataTypes(value = { "gene_copy_number" }, description = "Gene-normalized copy number values, in a sample-gene matrix format.")
public class GenericGeneCopyNumberMatrixProcessor extends GenericRecordProcessor<GeneCopyNumber> {

  public GenericGeneCopyNumberMatrixProcessor(
      GeneRepository geneRepository,
      GeneCopyNumberRepository repository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties
  ) {
    this.setReader(new GenericGeneCopyNumberMatrixReader(geneRepository, sampleRepository, dataImportProperties));
    this.setValidator(new GeneCopyNumberValidator());
    this.setWriter(new RepositoryRecordWriter<>(repository, WriteMode.INSERT, 200));
    this.setModel(GeneCopyNumber.class);
  }
  
}
