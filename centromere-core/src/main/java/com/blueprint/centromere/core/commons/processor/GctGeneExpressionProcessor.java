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

import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.reader.GctGeneExpressionFileReader;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.validator.GeneExpressionValidator;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter.WriteMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes(value = { "gct_gene_expression" }, description = "Gene-normalized expression data from GCT files")
@Component
public class GctGeneExpressionProcessor extends GenericRecordProcessor<GeneExpression> {

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public GctGeneExpressionProcessor(
      GeneRepository geneRepository, 
      GeneExpressionRepository repository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties
  ) {
    this.setModel(GeneExpression.class);
    this.setReader(new GctGeneExpressionFileReader(geneRepository, sampleRepository, dataImportProperties));
    this.setValidator(new GeneExpressionValidator());
    this.setWriter(new RepositoryRecordWriter<>(repository, WriteMode.INSERT, 200));
//    this.setWriter(new MongoImportTempFileWriter<>(dataImportProperties, mongoTemplate));
//    this.setImporter(new MongoImportTempFileImporter<>(GeneExpression.class, databaseProperties));
  }
}
