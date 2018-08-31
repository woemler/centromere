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

package com.blueprint.centromere.cli.dataimport.reader.impl;

import com.blueprint.centromere.cli.dataimport.DataImportException;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.model.impl.TranscriptExpression;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;

/**
 * Reads normalized transcript expression data from GCT files
 *   (http://software.broadinstitute.org/cancer/software/genepattern/file-formats-guide#GCT).
 * 
 * @author woemler
 * @since 0.5.0
 */
public class GctTranscriptExpressionSourceReader extends GctSourceReader<TranscriptExpression> {

  public GctTranscriptExpressionSourceReader(
      GeneRepository geneRepository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties) {
    super(TranscriptExpression.class, geneRepository, sampleRepository, dataImportProperties);
  }

  @Override
  protected TranscriptExpression getRecordValue(TranscriptExpression record, Sample sample,
      Gene gene, String line, int index) throws DataImportException {
    record = super.getRecordValue(record, sample, gene, line, index);
    String[] bits = line.split(this.getDelimiter());
    record.setTranscriptAccession(bits[0]);
    return record;
  }
}
