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

package com.blueprint.centromere.cli.dataimport.processor.impl;

import com.blueprint.centromere.cli.dataimport.DataTypes;
import com.blueprint.centromere.cli.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.cli.dataimport.processor.impl.validator.MutationValidator;
import com.blueprint.centromere.cli.dataimport.reader.impl.MafSourceReader;
import com.blueprint.centromere.cli.dataimport.writer.RepositoryRecordWriter;
import com.blueprint.centromere.cli.dataimport.writer.RepositoryRecordWriter.WriteMode;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.model.impl.Mutation;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.MutationRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@Component
@DataTypes(value = { "maf_mutation" }, description = "Mutation calls extracted from generic MAF files.")
public class MafMutationProcessor extends GenericRecordProcessor<Mutation> {

  public MafMutationProcessor(
      GeneRepository geneRepository, 
      MutationRepository repository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties
  ) {
    this.setModel(Mutation.class);
    this.setReader(new MafSourceReader(geneRepository, sampleRepository, dataImportProperties));
    this.setValidator(new MutationValidator());
    this.setWriter(new RepositoryRecordWriter<>(repository, WriteMode.INSERT, 200));
  }
}
