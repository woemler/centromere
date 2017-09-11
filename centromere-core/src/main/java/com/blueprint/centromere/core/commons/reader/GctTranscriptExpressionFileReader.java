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
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.model.TranscriptExpression;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.support.DataSetSupport;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.config.DataImportProperties;
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
 * Reads normalized transcript expression data from GCT files
 *   (http://software.broadinstitute.org/cancer/software/genepattern/file-formats-guide#GCT).
 * 
 * @author woemler
 * @since 0.5.0
 */
public class GctTranscriptExpressionFileReader extends MultiRecordLineFileReader<TranscriptExpression>
		implements SampleAware {

  private static final Logger logger = LoggerFactory.getLogger(GctTranscriptExpressionFileReader.class);
  
	private final GeneRepository geneRepository;
	private final DataSetSupport dataSetSupport;
	private final DataImportProperties dataImportProperties;
	
	private Map<String, Sample> sampleMap;

  public GctTranscriptExpressionFileReader(
      GeneRepository geneRepository,
      DataSetSupport dataSetSupport,
      DataImportProperties dataImportProperties) {
    this.geneRepository = geneRepository;
    this.dataSetSupport = dataSetSupport;
    this.dataImportProperties = dataImportProperties;
  }

  @Override 
	public void doBefore() throws DataImportException {
    super.doBefore();
    sampleMap = new HashMap<>();
	}

	@Override 
	protected List<TranscriptExpression> getRecordsFromLine(String line) throws DataImportException {
	  
		List<TranscriptExpression> records = new ArrayList<>();
		String[] bits = line.trim().split(this.getDelimiter());
		
		if (bits.length > 1){
			
		  Optional<Gene> geneOptional = getGene(line);
			if (!geneOptional.isPresent()){
				if (dataImportProperties.isSkipInvalidGenes()){
					logger.warn(String.format("Skipping line due to invalid gene: %s", line));
					return new ArrayList<>();
				} else {
					throw new InvalidGeneException(String.format("Invalid gene in line: %s", line));
				}
			}
			Gene gene = geneOptional.get();
			
			for (int i = 2; i < bits.length; i++){
				
			  TranscriptExpression record = new TranscriptExpression();
			  
				Sample sample = null;
				if (sampleMap.containsKey(this.getHeaders().get(i))){
					sample = sampleMap.get(this.getHeaders().get(i));
				} else {
					Optional<Sample> optional = dataSetSupport.findOrCreateSample(this.getHeaders().get(i), this.getDataSet());
					if (optional.isPresent()){
						sample = optional.get();
						sampleMap.put(this.getHeaders().get(i), sample);
					} 
				}
				if (sample == null){
					if (dataImportProperties.isSkipInvalidSamples()){
						logger.warn(String.format("Skipping record due to invalid sample: %s", 
								this.getHeaders().get(i)));
						continue;
					} else {
						throw new InvalidSampleException(String.format("Invalid sample: %s", this.getHeaders().get(i)));
					}
				}
				
				try {
					record.setValue(Double.parseDouble(bits[i]));
				} catch (NumberFormatException e){
					if (dataImportProperties.isSkipInvalidRecords()){
						logger.warn(String.format("Invalid record, cannot parse value: %s", bits[i]));
						continue;
					} else {
						throw new InvalidRecordException(String.format("Cannot parse value: %s", bits[i]));
					}
				}
				
				record.setDataFileId(this.getDataFile().getId());
				record.setDataSetId(getDataSet().getId());
				record.setGeneId(gene.getId());
				record.setSampleId(sample.getId());
				record.setSubjectId(sample.getSubjectId());
				record.setTranscriptAccession(bits[0]);
				records.add(record);
				
			}
			
		}
		return records;
	}

	private Optional<Gene> getGene(String line){
		String[] b = line.split(getDelimiter());
		return geneRepository.bestGuess(b[1]);
	}

	@Override 
	protected boolean isSkippableLine(String line) {
		return line.startsWith("#") || line.trim().split(this.getDelimiter()).length < 3;
	}

	@Override 
	public List<Sample> getSamples() {
		return new ArrayList<>(sampleMap.values());
	}

}
