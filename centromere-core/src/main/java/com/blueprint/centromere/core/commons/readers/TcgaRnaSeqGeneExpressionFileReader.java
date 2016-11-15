/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package com.blueprint.centromere.core.commons.readers;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.dataimport.*;
import com.blueprint.centromere.core.model.ModelSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads normalized RNA-Seq gene expression data from the TCGA files.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class TcgaRnaSeqGeneExpressionFileReader
		extends MultiRecordLineFileReader<GeneExpression>
		implements InitializingBean, ImportOptionsAware, ModelSupport<GeneExpression>,
			DataFileAware, SampleAware {

	private SampleRepository sampleRepository;
	private GeneRepository geneRepository;
	private BasicImportOptions options;
	private DataFile dataFile;
	private Map<String, Sample> sampleMap;
	private Class<GeneExpression> model;
	
	private static final Logger logger = LoggerFactory.getLogger(TcgaRnaSeqGeneExpressionFileReader.class);
	
	@PostConstruct
	public void afterPropertiesSet(){
		Assert.notNull(sampleRepository, "SampleRepository must not be null.");
		Assert.notNull(geneRepository, "GeneRepository must not be null.");
		Assert.notNull(dataFile, "DataFile cannot be null.");
		Assert.notNull(dataFile.getId(), "DataFile ID cannot be null.");
	}

	@Override 
	public void doBefore(Object... args) throws DataImportException {
		super.doBefore(args);
		sampleMap = new HashMap<>();
		afterPropertiesSet();
	}

	@Override 
	protected List<GeneExpression> getRecordsFromLine(String line) throws DataImportException {
		List<GeneExpression> records = new ArrayList<>();
		String[] bits = line.trim().split(this.getDelimiter());
		if (bits.length > 1){
			Gene gene = getGene(bits[0]);
			if (gene == null){
				if (options.isSkipInvalidGenes()){
					logger.warn(String.format("Skipping line due to invalid gene: %s", line));
					return new ArrayList<>();
				} else {
					throw new DataImportException(String.format("Invalid gene in line: %s", line));
				}
			}
			for (int i = 1; i < bits.length; i++){
				GeneExpression record;
				try {
					record = new GeneExpression();
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
					if (options.isSkipInvalidSamples()){
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
					if (options.isSkipInvalidRecords()){
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
	
	private Gene getGene(String field){
		Gene gene = null;
		String[] b = field.trim().split("|");
		if (b.length > 1){
			List<Gene> genes = null;
			if (!b[0].equals("?")){
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

	public BasicImportOptions getImportOptions() {
		return options;
	}

	public void setImportOptions(ImportOptions options) {
		this.options = (BasicImportOptions) options;
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
