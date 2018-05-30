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
import com.blueprint.centromere.core.dataimport.processor.impl.validator.TranscriptExpressionValidator;
import com.blueprint.centromere.core.dataimport.reader.impl.GctTranscriptExpressionSourceReader;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter.WriteMode;
import com.blueprint.centromere.core.model.impl.TranscriptExpression;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.core.repository.impl.TranscriptExpressionRepository;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@Component
@DataTypes(value = { "gct_transcript_expression" }, description = "Transcript expression data from GCT files")
public class GctTranscriptExpressionProcessor extends GenericRecordProcessor<TranscriptExpression> {

  public GctTranscriptExpressionProcessor(
      GeneRepository geneRepository, 
      TranscriptExpressionRepository repository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties
  ) {
    this.setModel(TranscriptExpression.class);
    this.setReader(new GctTranscriptExpressionSourceReader(geneRepository, sampleRepository, dataImportProperties));
    this.setValidator(new TranscriptExpressionValidator());
    this.setWriter(new RepositoryRecordWriter<>(repository, WriteMode.INSERT, 200));
  }
}
