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
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.dataimport.reader.StandardRecordFileReader;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Mutation;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.model.impl.SampleAware;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic file reader for Mutation Annotation Format (https://wiki.nci.nih.gov/display/TCGA/Mutation+Annotation+Format+(MAF)+Specification) 
 *   files.  Parses the bare-minimum required columns from the file.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class MafSourceReader extends StandardRecordFileReader<Mutation> implements SampleAware {
  
  private static final Logger logger = LoggerFactory.getLogger(MafSourceReader.class);
  
  private final GeneRepository geneRepository;
  private final SampleRepository sampleRepository;
  private final DataImportProperties dataImportProperties;
  
  private Map<String, Sample> sampleMap = new HashMap<>();

  public MafSourceReader(
      GeneRepository geneRepository,
      SampleRepository sampleRepository,
      DataImportProperties dataImportProperties) {
    this.geneRepository = geneRepository;
    this.sampleRepository = sampleRepository;
    this.dataImportProperties = dataImportProperties;
  }

  @Override
  protected Mutation getRecordFromLine(String line) throws DataImportException {
    
    String[] bits = line.split(this.getDelimiter());
    Mutation mutation = new Mutation();
    
    // Get gene
    Optional<Gene> geneOptional = getRecordGene(line);
    if (!geneOptional.isPresent()){
      if (dataImportProperties.isSkipInvalidGenes()){
        logger.info(String.format("Skipping invalid gene in line: %s", line));
        return null;
      } else {
        throw new InvalidGeneException(line);
      }
    }
    Gene gene = geneOptional.get();
    
    // Get sample
    Optional<Sample> sampleOptional = getRecordSample(line);
    if (!sampleOptional.isPresent()){
      if (dataImportProperties.isSkipInvalidSamples()){
        logger.info(String.format("Skipping invalid sample in line: %s ", line));
        return null;
      } else {
        throw new InvalidSampleException(line);
      }
    }
    Sample sample = sampleOptional.get();
    
    mutation.setSampleId(sample.getSampleId());
    mutation.setGeneId(gene.getGeneId());
    mutation.setDataSourceId(this.getDataSource().getDataSourceId());
    mutation.setDataSetId(this.getDataSet().getDataSetId());
    
    for (int i = 0; i < bits.length; i++){
      
      String header = this.getColumnHeader(i);
      if (header.toLowerCase().equals("chromosome")) mutation.setChromosome(bits[i]);
      else if (header.toLowerCase().equals("start_position")) mutation.setDnaStartPosition(getColumnValue(line, header, Integer.class));
      else if (header.toLowerCase().equals("end_position")) mutation.setDnaStopPosition(getColumnValue(line, header, Integer.class));
      else if (header.toLowerCase().equals("strand")) mutation.setStrand(bits[i]);
      else if (header.toLowerCase().equals("variant_classification")) mutation.setVariantClassification(bits[i]);
      else if (header.toLowerCase().equals("variant_type")) mutation.setVariantType(bits[i]);
      else if (header.toLowerCase().equals("reference_allele")) mutation.setReferenceAllele(bits[i]);
      else if (header.toLowerCase().equals("dbsnp_rs")){
        Set<String> refs = new HashSet<>();
        Collections.addAll(refs, bits[i].split("[,;]"));
        mutation.setExternalReferences(refs);
      }
      else mutation.addAttribute(header, bits[i]);
    }
    
    if (this.hasColumn("tumor_seq_allele1") && this.hasColumn("tumor_seq_allele2")){
      mutation.setAlternateAllele(this.getColumnValue(line, "tumor_seq_allele1") + "/" 
          + this.getColumnValue(line, "tumor_seq_allele2"));
    } else if (this.hasColumn("tumor_seq_allele1")){
      mutation.setAlternateAllele(this.getColumnValue(line, "tumor_seq_allele1"));
    } else if (this.hasColumn("tumor_seq_allele")){
      mutation.setAlternateAllele(this.getColumnValue(line, "tumor_seq_allele"));
    }
    
    return mutation;
    
  }

  /**
   * Extracts a {@link Gene} records from identifiers in the current line.
   * 
   * @param line
   * @return
   * @throws DataImportException
   */
  @SuppressWarnings("unchecked")
  protected Optional<Gene> getRecordGene(String line) throws DataImportException {
    Optional<Gene> geneOptional = Optional.empty();
    if (this.hasColumn("entrez_gene_id")) geneOptional = geneRepository.bestGuess(getColumnValue(line, "entrez_gene_id"));
    if (!geneOptional.isPresent() && this.hasColumn("hugo_symbol")){
      geneOptional = geneRepository.bestGuess(getColumnValue(line, "hugo_symbol"));
    }
    return geneOptional;
  }

  /**
   * Extracts a {@link Sample} records from identifiers found in the current line.
   * 
   * @param line
   * @return
   * @throws DataImportException
   */
  @SuppressWarnings("unchecked")
  protected Optional<Sample> getRecordSample(String line) throws DataImportException {
    Optional<Sample> optional = Optional.empty();
    if (this.hasColumn("tumor_sample_barcode")){
      String name = getColumnValue(line, "tumor_sample_barcode");
      optional = sampleRepository.bestGuess(name);
      if (optional.isPresent()) sampleMap.put(name, optional.get());
    } 
    return optional;
  }

  @Override
  protected boolean isSkippableLine(String line) {
    return line.trim().startsWith("#");
  }

  @Override
  protected boolean isHeaderLine(String line) {
    return this.getHeaderMap().isEmpty();
  }

  @Override
  public List<Sample> getSamples() {
    return new ArrayList<>(sampleMap.values());
  }
}
