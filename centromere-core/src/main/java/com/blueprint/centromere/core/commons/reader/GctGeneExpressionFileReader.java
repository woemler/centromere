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

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetSupport;
import com.blueprint.centromere.core.commons.support.GenericDataSetSupport;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import com.blueprint.centromere.core.model.ModelSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Reads normalized gene expression data from GCT files
 *   (http://software.broadinstitute.org/cancer/software/genepattern/file-formats-guide#GCT).
 * 
 * @author woemler
 * @since 0.4.3
 */
public class GctGeneExpressionFileReader
		extends MultiRecordLineFileReader<GeneExpression>
		implements ModelSupport<GeneExpression>, DataFileAware, SampleAware {

	private final GeneRepository geneRepository;
	private final DataSetSupport dataSetSupport;
	
	private DataFile dataFile;
	private Map<String, Sample> sampleMap;
	private Class<GeneExpression> model;
	
	private static final Logger logger = LoggerFactory.getLogger(GctGeneExpressionFileReader.class);

  public GctGeneExpressionFileReader(
      GeneRepository geneRepository,
      SampleRepository sampleRepository,
      SubjectRepository subjectRepository) {
    this.geneRepository = geneRepository;
    this.dataSetSupport = new GenericDataSetSupport(subjectRepository, sampleRepository);
  }

  public GctGeneExpressionFileReader(
      GeneRepository geneRepository,
      DataSetSupport dataSetSupport) {
    this.geneRepository = geneRepository;
    this.dataSetSupport = dataSetSupport;
  }

  @Override 
	public void doBefore(Object... args) {
    Assert.notNull(dataFile, "DataFile cannot be null.");
    Assert.notNull(dataFile.getId(), "DataFile ID cannot be null.");
    sampleMap = new HashMap<>();
	}

	@Override 
	protected List<GeneExpression> getRecordsFromLine(String line) {
	  
		List<GeneExpression> records = new ArrayList<>();
		String[] bits = line.trim().split(this.getDelimiter());
		
		if (bits.length > 1){
			
		  Gene gene = getGene(line);
			if (gene == null){
				if (this.getImportOptions().skipInvalidGenes()){
					logger.warn(String.format("Skipping line due to invalid gene: %s", line));
					return new ArrayList<>();
				} else {
					throw new DataImportException(String.format("Invalid gene in line: %s", line));
				}
			}
			
			for (int i = 2; i < bits.length; i++){
				
			  GeneExpression record = new GeneExpression();
			  
				Sample sample = null;
				if (sampleMap.containsKey(this.getHeaders().get(i))){
					sample = sampleMap.get(this.getHeaders().get(i));
				} else {
					Optional<Sample> optional = dataSetSupport.findSample(this.getHeaders().get(i), this.getDataSet());
					if (optional.isPresent()){
						sample = optional.get();
						sampleMap.put(this.getHeaders().get(i), sample);
					}
				}
				if (sample == null){
					if (this.getImportOptions().skipInvalidSamples()){
						logger.warn(String.format("Skipping record due to invalid sample: %s", 
								this.getHeaders().get(i)));
						continue;
					} else {
						throw new DataImportException(String.format("Invalid sample: %s", this.getHeaders().get(i)));
					}
				}
				
				try {
					record.setValue(Double.parseDouble(bits[i]));
				} catch (NumberFormatException e){
					if (this.getImportOptions().skipInvalidRecords()){
						logger.warn(String.format("Invalid record, cannot parse value: %s", bits[i]));
						continue;
					} else {
						throw new DataImportException(String.format("Cannot parse value: %s", bits[i]));
					}
				}
				
				record.setDataFileId(dataFile.getId());
				record.setDataSetId(getDataSet().getId());
				record.setGeneId(gene.getId());
				record.setSampleId(sample.getId());
				record.setSubjectId(sample.getSubjectId());
				records.add(record);
				
			}
			
		}
		return records;
	}

	private Gene getGene(String line){
		Gene gene = null;
		String[] b = line.split(getDelimiter());
		if (b.length > 1){
			Optional<Gene> optional;
			if (!b[0].equals("")){
				optional = geneRepository.bestGuess(b[0]);
				if (optional.isPresent()) gene = optional.get();
			}
			if (gene == null){
				optional = geneRepository.bestGuess(b[1]);
				if (optional.isPresent()) gene = optional.get();
			}
		}
		return gene;
	}

	@Override 
	protected boolean isSkippableLine(String line) {
		return false;
	}

	public DataFile getDataFile() {
		return dataFile;
	}

	public void setDataFile(DataFile dataFile) {
		this.dataFile = dataFile;
	}

	@Override 
	public Class<GeneExpression> getModel() {
		return model;
	}

	@Override 
	public void setModel(Class<GeneExpression> model) {
		this.model = model;
	}

	@Override 
	public List<Sample> getSamples() {
		return new ArrayList<>(sampleMap.values());
	}

}
