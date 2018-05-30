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

package com.blueprint.centromere.core.dataimport.reader.impl;

import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidGeneException;
import com.blueprint.centromere.core.dataimport.exception.InvalidRecordException;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordFileReader;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.model.impl.SampleAware;
import com.blueprint.centromere.core.model.impl.SimpleData;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic base class for reading data from GCT files
 *   (http://software.broadinstitute.org/cancer/software/genepattern/file-formats-guide#GCT).
 * 
 * @author woemler
 * @since 0.5.0
 */
public abstract class GctSourceReader<T extends SimpleData<?>> 
    extends MultiRecordFileReader<T>
		implements SampleAware {

  private static final Logger logger = LoggerFactory.getLogger(GctSourceReader.class);
  
  private final Class<T> model;
	private final GeneRepository geneRepository;
	private final SampleRepository sampleRepository;
	private final DataImportProperties dataImportProperties;
	
	private Map<String, Sample> sampleMap;
	private String gctFormatVersion;
	private Long probeCount;
	private Long sampleCount;

  public GctSourceReader(
      Class<T> model,
      GeneRepository geneRepository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties) {
    this.model = model;
    this.geneRepository = geneRepository;
    this.sampleRepository = sampleRepository;
    this.dataImportProperties = dataImportProperties;
  }

  @Override 
	public void doBefore() throws DataImportException {
    super.doBefore();
    sampleMap = new HashMap<>();
	}

	@Override 
	protected List<T> getRecordsFromLine(String line) throws DataImportException {
	  
		List<T> records = new ArrayList<>();
		String[] bits = line.trim().split(this.getDelimiter());
		
		if (bits.length == 1 && gctFormatVersion == null){
		  
		  gctFormatVersion = bits[0].replace("#", "");
		  
    } else if (bits.length == 2 && probeCount == null && sampleCount == null) {
      
		  try {
        probeCount = Long.parseLong(bits[0]);
      } catch (NumberFormatException e){
        e.printStackTrace();
        logger.warn(String.format("Unable to parse probe count: %s", bits[0]));
      }

      try {
        sampleCount = Long.parseLong(bits[1]);
      } catch (NumberFormatException e){
        e.printStackTrace();
        logger.warn(String.format("Unable to parse sample count: %s", bits[1]));
      }
      
    } else if (bits.length > 2){
			
		  Gene gene = getGeneFromLine(line);
		  
			if (gene == null){
				if (dataImportProperties.isSkipInvalidGenes()){
					logger.warn(String.format("Skipping line due to invalid gene: %s", line));
					return new ArrayList<>();
				} else {
					throw new InvalidGeneException(String.format("Invalid gene in line: %s", line));
				}
			}
			
			for (int i = 2; i < bits.length; i++){
				
			  T record;
			  try {
			    record = model.newInstance();
        } catch (Exception e){
			    throw new DataImportException(e);
        }
			  
				Sample sample = getSampleFromColumn(i);
				if (sample == null){
					if (dataImportProperties.isSkipInvalidSamples()){
						logger.warn(String.format("Skipping record due to invalid sample: %s", 
								this.getHeaders().get(i)));
						continue;
					} else {
						throw new InvalidSampleException(String.format("Invalid sample: %s", this.getHeaders().get(i)));
					}
				}
				
				record.setDataSourceId(this.getDataSource().getDataSourceId());
				record.setDataSetId(getDataSet().getDataSetId());
				record.setGeneId(gene.getGeneId());
				record.setSampleId(sample.getSampleId());
				
				record = getRecordValue(record, sample, gene, line, i);
				
				if (record != null) records.add(record);
				
			}
			
		}
		
		return records;
	}

  /**
   * Extracts the appropriate {@link SimpleData#value} value from the line of text.
   * 
   * @param record
   * @param sample
   * @param gene
   * @param line
   * @param index
   * @return
   * @throws DataImportException
   */
	protected T getRecordValue(T record, Sample sample, Gene gene, String line, int index) 
      throws DataImportException {
    String[] bits = line.split(this.getDelimiter());
    try {
      record.setValue(Double.parseDouble(bits[index]));
    } catch (NumberFormatException e){
      if (dataImportProperties.isSkipInvalidRecords()){
        logger.warn(String.format("Invalid record, cannot parse value: %s", bits[index]));
        return null;
      } else {
        throw new InvalidRecordException(String.format("Cannot parse value: %s", bits[index]));
      }
    }
    return record;
  }

  /**
   * Gets the {@link Sample} record corresponding to the record's column position.
   * 
   * @param i
   * @return
   */
	protected Sample getSampleFromColumn(int i){
    Sample sample = null;
    if (sampleMap.containsKey(this.getHeaders().get(i))){
      sample = sampleMap.get(this.getHeaders().get(i));
    } else {
      Optional<Sample> optional = sampleRepository.bestGuess(this.getHeaders().get(i));
      if (optional.isPresent()){
        sample = optional.get();
        sampleMap.put(this.getHeaders().get(i), sample);
      }
    }
    return sample;
  } 

  /**
   * Inspects the line to determine the corresponding {@link Gene} record to associate with the records.
   * 
   * @param line
   * @return
   */
	protected Gene getGeneFromLine(String line){
		Gene gene = null;
		String[] b = line.split(getDelimiter());
		if (b.length > 1){
			Optional<Gene> optional;
			if (!b[0].trim().equals("")){
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
  protected boolean isHeaderLine(String line) {
    return this.getHeaders().isEmpty() && line.split(this.getDelimiter()).length > 2;
  }

  public Class<T> getModel() {
    return model;
  }

  public String getGctFormatVersion() {
    return gctFormatVersion;
  }

  public Long getProbeCount() {
    return probeCount;
  }

  public Long getSampleCount() {
    return sampleCount;
  }

  @Override 
	protected boolean isSkippableLine(String line) {
		return false;
	}

	@Override 
	public List<Sample> getSamples() {
		return new ArrayList<>(sampleMap.values());
	}

}
