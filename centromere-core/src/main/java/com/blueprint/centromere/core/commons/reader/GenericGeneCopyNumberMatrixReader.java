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

package com.blueprint.centromere.core.commons.reader;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.GeneCopyNumber;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.commons.support.DataSetSupport;
import com.blueprint.centromere.core.commons.support.GenericDataSetSupport;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidGeneException;
import com.blueprint.centromere.core.dataimport.exception.InvalidRecordException;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public class GenericGeneCopyNumberMatrixReader extends MultiRecordLineFileReader<GeneCopyNumber> 
    implements SampleAware {
  
  private static final Logger logger = LoggerFactory.getLogger(GenericGeneCopyNumberMatrixReader.class);
  
  private final GeneRepository geneRepository;
  private final DataSetSupport support;
  private Map<Integer, Sample> samples = new HashMap<>();

  public GenericGeneCopyNumberMatrixReader(SubjectRepository subjectRepository,
      SampleRepository sampleRepository, GeneRepository geneRepository) {
    this.geneRepository = geneRepository;
    this.support = new GenericDataSetSupport(subjectRepository, sampleRepository);
  }

  public GenericGeneCopyNumberMatrixReader(DataSetSupport dataSetSupport, 
      GeneRepository geneRepository) {
    this.geneRepository = geneRepository;
    this.support = dataSetSupport;
  }

  /**
   * Extracts multiple records from a single line of the text file.  If no valid records are found,
   * an empty list should be returned.
   */
  @Override
  protected List<GeneCopyNumber> getRecordsFromLine(String line) throws DataImportException {
    
    String[] bits = line.trim().split(this.getDelimiter());
    List<GeneCopyNumber> records = new ArrayList<>();

    Gene gene = null;
    Optional<Gene> optional = geneRepository.bestGuess(bits[0]);
    if (optional.isPresent()){
      gene = optional.get();
    } 
    if (this.getImportOptions().isInvalidGene(gene)){
      if (this.getImportOptions().skipInvalidGenes()){
        logger.warn("Skipping unknown gene: %s %s", bits[0], bits[1]);
        return records;
      } else {
        throw new InvalidGeneException(String.format("Unknown gene: %s %s", bits[0], bits[1]));
      }
    }
    
    for (int i = 2; i < bits.length; i++){
      GeneCopyNumber record = new GeneCopyNumber();
      if (samples.containsKey(i)){
        record.setSampleId(samples.get(i).getId());
        record.setSubjectId(samples.get(i).getSubjectId());
      } else {
        continue;
      }
      record.setDataFileId(this.getDataFile().getId());
      record.setGeneId(gene.getId());
      record.setDataSetId(this.getDataSet().getId());
      try {
        Double val = Double.parseDouble(bits[i]);
        record.setValue(val);
      } catch (NumberFormatException e){
        if (this.getImportOptions().skipInvalidRecords()){
          continue;
        } else {
          throw new InvalidRecordException(String.format("Cannot parse floating point expression value " 
              + "from column: %s", bits[i]));
        }
      }
      records.add(record);
    }
    
    return records;
    
  }

  /**
   * Extracts the column names from the header line in the file.
   */
  @Override
  protected void parseHeader(String line) throws DataImportException {
    String[] bits = line.trim().split(this.getDelimiter());
    for (int i = 2; i < bits.length; i++){
      Optional<Sample> optional = support.findOrCreateSample(bits[i], this.getDataSet());
      if (optional.isPresent()){
        samples.put(i, optional.get());
      } else {
        if (!this.getImportOptions().skipInvalidSamples()){
          throw new InvalidSampleException(String.format("Unable to identify subject for sample: %s", bits[i]));
        }
      }
    }
  }

  /**
   * Tests whether a given line should be skipped.
   */
  @Override
  protected boolean isSkippableLine(String line) {
    return line.trim().split(this.getDelimiter()).length < 3;
  }

  @Override
  public List<Sample> getSamples() {
    return new ArrayList<>(samples.values());
  }
}
