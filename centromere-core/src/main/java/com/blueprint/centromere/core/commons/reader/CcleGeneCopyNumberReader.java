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
import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.commons.model.Subject.Attributes;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author woemler
 */
public class CcleGeneCopyNumberReader extends MultiRecordLineFileReader<GeneCopyNumber> {
  
  private static final Logger logger = LoggerFactory.getLogger(CcleGeneCopyNumberReader.class);
  
  private GeneRepository geneRepository;
  private SampleRepository sampleRepository;
  private SubjectRepository subjectRepository;
  private Map<Integer, String> samples = new HashMap<>();

  /**
   * Extracts multiple records from a single line of the text file.  If no valid records are found,
   * an empty list should be returned.
   */
  @Override
  protected List<GeneCopyNumber> getRecordsFromLine(String line) {
    
    String[] bits = line.trim().split(this.getDelimiter());
    List<GeneCopyNumber> records = new ArrayList<>();

    Gene gene = null;
    Optional<Gene> optional = geneRepository.findByPrimaryReferenceId(bits[0]);
    if (optional.isPresent()){
      gene = optional.get();
    } else {
      List<Gene> genes = geneRepository.guess(bits[1]);
      if (!genes.isEmpty()){
        gene = genes.get(0);
      }
    }
    if (this.getImportOptions().isInvalidGene(gene)){
      if (this.getImportOptions().skipInvalidGenes()){
        logger.warn("Skipping unknown gene: %s %s", bits[0], bits[1]);
        return records;
      } else {
        throw new DataImportException(String.format("Unknown gene: %s %s", bits[0], bits[1]));
      }
    }
    
    return records;
    
  }

  /**
   * Extracts the column names from the header line in the file.
   */
  @Override
  protected void parseHeader(String line) {
    String[] bits = line.trim().split(this.getDelimiter());
    for (int i = 2; i < bits.length; i++){
      Optional<Subject> optional = subjectRepository.findByName(bits[i]);
      if (optional.isPresent()){
        Subject subject = optional.get();
        Sample sample = new Sample();
        sample.setName(bits[i]);
        sample.setDataSetId(this.getDataSet().getId());
        sample.setSubjectId(subject.getId());
        //sample.setSampleType();
        if (subject.hasAttribute(Attributes.SAMPLE_TISSUE)) {
          sample.setTissue(subject.getAttribute(Attributes.SAMPLE_TISSUE));
        }
        if (subject.hasAttribute(Attributes.SAMPLE_HISTOLOGY)) {
          sample.setHistology(subject.getAttribute(Attributes.SAMPLE_HISTOLOGY));
        }
      } else {
        if (!this.getImportOptions().skipInvalidSamples()){
          throw new DataImportException(String.format("Unable to identify subject for sample: %s", bits[i]));
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

  @Autowired
  public void setGeneRepository(GeneRepository geneRepository) {
    this.geneRepository = geneRepository;
  }

  @Autowired
  public void setSampleRepository(SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }

  @Autowired
  public void setSubjectRepository(SubjectRepository subjectRepository) {
    this.subjectRepository = subjectRepository;
  }
}
