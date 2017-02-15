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
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.config.ApplicationProperties;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.MultiRecordLineFileReader;
import com.blueprint.centromere.core.model.ModelSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * Reads normalized RNA-Seq gene expression data from the TCGA files.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class TcgaRnaSeqGeneExpressionFileReader
		extends MultiRecordLineFileReader<GeneExpression>
		implements ModelSupport<GeneExpression>, DataFileAware {

	private static final Logger logger = LoggerFactory.getLogger(TcgaRnaSeqGeneExpressionFileReader.class);

	private final SampleRepository sampleRepository;
	private final GeneRepository geneRepository;
	private final SubjectRepository subjectRepository;
	private DataFile dataFile;
	private final Environment environment;
	private Map<String, Sample> sampleMap;
	private Map<String, Gene> geneMap;
	private Class<GeneExpression> model;
	private final Pattern pattern = Pattern.compile("(tcga-[a-zA-Z0-9]+-[a-zA-Z0-9]+)-.+", Pattern.CASE_INSENSITIVE);

	public TcgaRnaSeqGeneExpressionFileReader(
      SampleRepository sampleRepository,
      SubjectRepository subjectRepository,
      GeneRepository geneRepository,
      Environment environment
	){
    this.sampleRepository = sampleRepository;
    this.subjectRepository = subjectRepository;
    this.geneRepository = geneRepository;
    this.environment = environment;
	}

	@Override
	public void doBefore(Object... args) throws DataImportException {
		super.doBefore(args);
        Assert.notNull(dataFile.getDataSet(), "DataSet must be set for DataFile object.");
		sampleMap = new HashMap<>();
		geneMap = new HashMap<>();
		for (Gene gene: geneRepository.findAll()){
		  geneMap.put(gene.getPrimaryReferenceId(), gene);
    }
	}

	@Override 
	protected List<GeneExpression> getRecordsFromLine(String line) throws DataImportException {
		List<GeneExpression> records = new ArrayList<>();
		String[] bits = line.trim().split(this.getDelimiter());
		if (bits.length > 1){
			Gene gene = getGene(bits[0]);
			if (gene == null){
				if (environment.getRequiredProperty(ApplicationProperties.SKIP_INVALID_GENES, Boolean.class)){
					logger.debug(String.format("Skipping line due to invalid gene: %s", line));
					return new ArrayList<>();
				} else {
					throw new DataImportException(String.format("Invalid gene in line: %s", line));
				}
			}
			for (int i = 1; i < bits.length; i++){
				GeneExpression record  = new GeneExpression();
				Sample sample = getSample(i);
				if (sample == null){
					if (environment.getRequiredProperty(ApplicationProperties.SKIP_INVALID_SAMPLES, Boolean.class)){
						logger.debug(String.format("Skipping record due to invalid sample: %s", 
								this.getHeaders().get(i)));
						continue;
					} else {
						throw new DataImportException(String.format("Invalid sample: %s", this.getHeaders().get(i)));
					}
				}
				try {
					record.setValue(Double.parseDouble(bits[i]));
				} catch (NumberFormatException e){
					if (environment.getRequiredProperty(ApplicationProperties.SKIP_INVALID_RECORDS, Boolean.class)){
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

	private Sample getSample(int index) {
		String sampleName = this.getHeaders().get(index);
		Sample sample = null;
		if (sampleMap.containsKey(sampleName)){
			sample = sampleMap.get(sampleName);
		} else {
			List<Sample> samples = sampleRepository.findByName(sampleName);
			if (samples != null && !samples.isEmpty()){
					sample = samples.get(0);
			} else {
				Matcher matcher = pattern.matcher(sampleName);
				if (matcher.matches()){
					String subjectName = matcher.group(1);
					List<Subject> subjects = subjectRepository.findByName(subjectName);
					if (subjects != null && !subjects.isEmpty()) {
						Subject subject = subjects.get(0);
						sample = new Sample();
						sample.setName(sampleName);
						sample.setDataSet(dataFile.getDataSet());
						sample.setSubject(subject);
						sample.setTissue(subject.getAttribute("tumor_tissue_site"));
						sample.setHistology(subject.getAttribute("histological_type"));
						sampleRepository.save(sample);
						sampleMap.put(sampleName, sample);
					} else {
						logger.warn("Sample has no associated Subject record: " + sampleName);
						return null;
					}
				} else {
					logger.warn("Unable to extract subject name from header: " + sampleName);
				}
			}
		}
		return sample;
	}
	
	private Gene getGene(String field){
		Gene gene = null;
		String[] b = field.trim().split("|");
		if (b.length > 1){
			if (geneMap.containsKey(b[1])){
			  gene = geneMap.get(b[1]);
      } else if (!b[0].equals("?")){
				List<Gene> genes = geneRepository.guess(b[0]);
        if (genes.size() > 0){
          gene = genes.get(0);
        }
			} 
		}
		return gene;
	}

	@Override 
	protected boolean isSkippableLine(String line) {
		return line.toLowerCase().startsWith("gene_id");
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
	public DataFile getDataFile() {
			return dataFile;
	}

	@Override
	public void setDataFile(DataFile dataFile) {
			this.dataFile = dataFile;
	}
}
