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
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.support.DataSetSupport;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.commons.support.TcgaSupport;
import com.blueprint.centromere.core.dataimport.DataSetSupportAware;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidRecordException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import com.blueprint.centromere.core.model.ModelSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads normalized RNA-Seq gene expression data from the TCGA files.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class TcgaRnaSeqGeneExpressionFileReader
		extends MultiRecordLineFileReader<GeneExpression>
		implements ModelSupport<GeneExpression>, SampleAware, DataSetSupportAware {

	private static final Logger logger = LoggerFactory.getLogger(TcgaRnaSeqGeneExpressionFileReader.class);

	private final GeneRepository geneRepository;
	private final TcgaSupport tcgaSupport;
	private Map<String, Sample> sampleMap;
	private Map<String, Gene> geneMap;
	private Class<GeneExpression> model = GeneExpression.class;

	public TcgaRnaSeqGeneExpressionFileReader(
      GeneRepository geneRepository,
      TcgaSupport tcgaSupport
	){
    this.geneRepository = geneRepository;
    this.tcgaSupport = tcgaSupport;
	}

	@Override
	public void doBefore() throws DataImportException {
		super.doBefore();
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
			if (this.getImportOptions().isInvalidGene(gene)){
				logger.debug(String.format("Skipping line due to invalid gene: %s", line));
        return new ArrayList<>();
			}
			for (int i = 1; i < bits.length; i++){
				GeneExpression record  = new GeneExpression();
				Sample sample = getSample(i);
				if (this.getImportOptions().isInvalidSample(sample)){
          logger.debug(String.format("Skipping record due to invalid sample: %s",
              this.getHeaders().get(i)));
          continue;
				}
				try {
					record.setValue(Double.parseDouble(bits[i]));
				} catch (NumberFormatException e){
					if (this.getImportOptions().skipInvalidRecords()){
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
				records.add(record);
			}
			
		}
		return records;
	}

  @Override
  protected void parseHeader(String line) throws DataImportException {
    super.parseHeader(line);
  }

	private Sample getSample(int index) {
		String sampleName = this.getHeaders().get(index).toLowerCase();
		Sample sample;
		if (sampleMap.containsKey(sampleName)){
			sample = sampleMap.get(sampleName);
		} else {
			Optional<Sample> optional = tcgaSupport.findSample(sampleName, this.getDataSet());
			sample = optional.isPresent() ? optional.get() : tcgaSupport.createSample(sampleName, this.getDataSet());
			sampleMap.put(this.getHeaders().get(index), sample);
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
				Optional<Gene> optional = geneRepository.bestGuess(b[0]);
        if (optional.isPresent()){
          gene = optional.get();
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
  public List<Sample> getSamples() {
    return new ArrayList<>(sampleMap.values());
  }

  @Override
  public DataSetSupport getDataSetSupport() {
    return tcgaSupport;
  }
}
