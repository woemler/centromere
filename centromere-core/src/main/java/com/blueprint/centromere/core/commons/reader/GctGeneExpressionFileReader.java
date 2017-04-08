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
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.config.Properties;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import com.blueprint.centromere.core.model.ModelSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
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
		implements ModelSupport<GeneExpression>, DataFileAware, SampleAware, EnvironmentAware {

	private SampleRepository sampleRepository;
	private GeneRepository geneRepository;
	private DataFile dataFile;
	private Map<String, Sample> sampleMap;
	private Class<GeneExpression> model;
	private Environment environment;
	
	private static final Logger logger = LoggerFactory.getLogger(GctGeneExpressionFileReader.class);

	@Override 
	public void doBefore(Object... args) throws DataImportException {
        Assert.notNull(sampleRepository, "SampleRepository must not be null.");
        Assert.notNull(geneRepository, "GeneRepository must not be null.");
        Assert.notNull(dataFile, "DataFile cannot be null.");
        Assert.notNull(dataFile.getId(), "DataFile ID cannot be null.");
        sampleMap = new HashMap<>();
	}

	@Override 
	protected List<GeneExpression> getRecordsFromLine(String line) throws DataImportException {
		List<GeneExpression> records = new ArrayList<>();
		String[] bits = line.trim().split(this.getDelimiter());
		if (bits.length > 1){
			Gene gene = getGene(line);
			if (gene == null){
				if (environment.getRequiredProperty(Properties.SKIP_INVALID_GENES, Boolean.class)){
					logger.warn(String.format("Skipping line due to invalid gene: %s", line));
					return new ArrayList<>();
				} else {
					throw new DataImportException(String.format("Invalid gene in line: %s", line));
				}
			}
			for (int i = 2; i < bits.length; i++){
				GeneExpression record;
				try {
					record = this.getModel().newInstance();
				} catch (Exception e){
					throw new DataImportException(String.format("Unable to create instance of model object: %s"
							, model.getName()));
				}
				Sample sample = null;
				if (sampleMap.containsKey(this.getHeaders().get(i))){
					sample = sampleMap.get(this.getHeaders().get(i));
				} else {
					List<Sample> samples = sampleRepository.guess(this.getHeaders().get(i));
					if (samples != null && !samples.isEmpty()){
						sample = samples.get(0);
						sampleMap.put(this.getHeaders().get(i), sample);
					}
				}
				if (sample == null){
					if (environment.getRequiredProperty(Properties.SKIP_INVALID_SAMPLES, Boolean.class)){
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
					if (environment.getRequiredProperty(Properties.SKIP_INVALID_RECORDS, Boolean.class)){
						logger.warn(String.format("Invalid record, cannot parse value: %s", bits[i]));
						continue;
					} else {
						throw new DataImportException(String.format("Cannot parse value: %s", bits[i]));
					}
				}
				record.setDataFile(dataFile);
				record.setGene(gene);
				record.setSample(sample);
				records.add(record);
			}
			
		}
		return records;
	}

	private Gene getGene(String line){
		Gene gene = null;
		String[] b = line.split(getDelimiter());
		if (b.length > 1){
			List<Gene> genes = null;
			if (!b[0].equals("")){
				genes = geneRepository.guess(b[0]);
			}
			if (genes == null || genes.isEmpty()){
				genes = geneRepository.guess(b[1]);
			}
			if (genes.size() > 0){
				gene = genes.get(0);
			}
		}
		return gene;
	}

	@Override 
	protected boolean isSkippableLine(String line) {
		return false;
	}

	@Autowired
	public void setSampleRepository(SampleRepository sampleRepository) {
		this.sampleRepository = sampleRepository;
	}

	@Autowired
	public void setGeneRepository(GeneRepository geneRepository) {
		this.geneRepository = geneRepository;
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

	@Override
	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
