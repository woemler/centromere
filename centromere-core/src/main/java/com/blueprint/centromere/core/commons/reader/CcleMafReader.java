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
import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.support.CcleSupport;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.StandardRecordFileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author woemler
 * @since 0.5.0
 */
public class CcleMafReader extends StandardRecordFileReader<Mutation> 
    implements SampleAware {

  private static final Logger logger = LoggerFactory.getLogger(CcleMafReader.class);

  private final CcleSupport ccleSupport;
  private final GeneRepository geneRepository;
  private final DataImportProperties dataImportProperties;

  private Map<String, Integer> columnMap = new HashMap<>();
  private Map<String, Sample> sampleMap = new HashMap<>();
  private String delimiter = "\t";
  private boolean headerFlag = true;

  public CcleMafReader(CcleSupport ccleSupport,
      GeneRepository geneRepository,
      DataImportProperties dataImportProperties) {
    this.ccleSupport = ccleSupport;
    this.geneRepository = geneRepository;
    this.dataImportProperties = dataImportProperties;
  }

  /**
   * Closes any open reader and opens the new target file.  Assigns local variables, if available.
   */
  @Override
  public void doBefore() throws DataImportException {
    super.doBefore();
    try {
      Assert.notNull(ccleSupport, "CcleSupport is not set.");
      Assert.notNull(geneRepository, "GeneRepository is not set.");
    } catch (Exception e){
      throw new DataImportException(e);
    }
  }

  @Override
  protected Mutation getRecordFromLine(String line) throws DataImportException {

    Mutation mutation = new Mutation();
    mutation.setDataFileId(this.getDataFile().getId());
    mutation.setDataSetId(this.getDataSet().getId());

    Sample sample = getSampleFromLine(line);
    if (sample == null) {
      if (dataImportProperties.isSkipInvalidSamples()) {
        logger.info("Skipping line due to invalid sample: " + line);
        return null;
      } else {
        throw new DataImportException(String.format("Unable to identify sample from line: %s", line));
      }
    } else {
      mutation.setSampleId(sample.getId());
      mutation.setSubjectId(sample.getSubjectId());
    }

    Gene gene = getGeneFromLine(line);
    if (gene == null){
      if (dataImportProperties.isSkipInvalidGenes()) {
        logger.info("Skipping line due to invalid gene: " + line);
        return null;
      } else {
        throw new DataImportException(String.format("Unable to identify gene in line: %s", line));
      }
    } else {
      mutation.setGeneId(gene.getId());
    }

    // TODO
//    mutation.setReferenceGenome(parseReferenceGenome(line));
//    mutation.setChromosome(getColumnValue(line, "chromosome"));
//    mutation.setDnaStartPosition(Integer.parseInt(getColumnValue(line, "start_position")));
//    mutation.setDnaStopPosition(Integer.parseInt(getColumnValue(line, "end_position")));
//    mutation.setStrand(getColumnValue(line, "strand"));
//    mutation.setVariantClassification(getColumnValue(line, "variant_classification"));
//    mutation.setVariantType(getColumnValue(line, "variant_type"));
//    mutation.setReferenceAllele(getColumnValue(line, "reference_allele"));
//    mutation.setAlternateAllele(getColumnValue(line, "tumor_seq_allele2"));
//    mutation.setcDnaChange(getColumnValue(line, "cdna_change"));
//    mutation.setCodonChange(getColumnValue(line, "codon_change"));
//    mutation.setAminoAcidChange(getColumnValue(line, "protein_change"));
//    mutation.setMrnaTranscript(getColumnValue(line, "refseq_mrna_id"));
//    mutation.setProteinTranscript(getColumnValue(line, "refseq_prot_id"));
//    mutation.setAlternateTranscripts(parseAlternateTranscripts(line));
//    mutation.setExternalReferenes(null);
//    mutation.setAttributes(null);

    return mutation;
  }

  private Gene getGeneFromLine(String line){
    Gene gene = null;
    if (hasColumn("entrez_gene_id")){
      Optional<Gene> optional = geneRepository.findByPrimaryReferenceId(getColumnValue(line, "entrez_gene_id"));
      if (optional.isPresent()) gene = optional.get();
    }
    if (gene == null && hasColumn("hugo_symbol")){
      List<Gene> genes = geneRepository.findByPrimaryGeneSymbol(getColumnValue(line, "hugo_symbol"));
      if (!genes.isEmpty()) gene = genes.get(0);
    }
    return gene;
  }

  private Gene getGene(String identifier){
    Optional<Gene> optional = geneRepository.bestGuess(identifier);
    return optional.orElse(null);
  }

  private Sample getSampleFromLine(String line){
    String sampleName;
    if (hasColumn("tumor_sample_barcode")){
      sampleName = getColumnValue(line, "tumor_sample_barcode");
    } else {
      return null;
    }

    if (sampleMap.containsKey(sampleName)){
      return sampleMap.get(sampleName);
    }

    Sample sample;
    Optional<Sample> optional = ccleSupport.findSample(sampleName, this.getDataSet());
    if (!optional.isPresent()) {
      sample = ccleSupport.createSample(sampleName, this.getDataSet());
    } else {
      sample = optional.get();
    }
    if (sample != null) sampleMap.put(sampleName, sample);
      
    return sample;

  }

  private List<Mutation.VariantTranscript> parseAlternateTranscripts(String line){
    List<Mutation.VariantTranscript> transcripts = new ArrayList<>();
    String otherTranscripts = getColumnValue(line, "other_transcripts");
    if (otherTranscripts != null && !otherTranscripts.trim().equals("")) {
      for (String ot : otherTranscripts.split("\\|")) {
        String[] bits = ot.split("_");
        Mutation.VariantTranscript transcript = new Mutation.VariantTranscript();
        Gene gene = getGene(bits[0]);
        if (gene != null) transcript.setGeneId(gene.getId());
        transcript.setTranscriptId(bits[1]);
        //transcript.set
        transcripts.add(transcript);
      }
    }
    return transcripts;
  }

  private String parseReferenceGenome(String line){
    String ref = null;
    if (hasColumn("ncbi_build")){
      ref = "hg" + getColumnValue(line, "ncbi_build");
    }
    return ref;
  }

  @Override
  protected boolean isSkippableLine(String line) {
    return line.startsWith("#");
  }

  @Override
  protected void parseHeader(String line) {
    columnMap = new HashMap<>();
    String[] bits = line.trim().split(delimiter);
    for (int i = 0; i < bits.length; i++){
      if (!bits[i].trim().equals("")){
        columnMap.put(bits[i].trim().toLowerCase(), i);
      }
    }
    headerFlag = false;
  }

  @Override
  protected boolean isHeaderLine(String line) {
    return headerFlag;
  }

  private String getColumnValue(String line, String header) {
    header = header.toLowerCase();
    String value = null;
    if (columnMap.containsKey(header)){
      String[] bits = line.trim().split(delimiter);
      Integer index = columnMap.get(header);
      if (bits.length > index){
        value = bits[index];
      }
    }
    return value;
  }

  private boolean hasColumn(String column){
    return columnMap.containsKey(column);
  }

  @Override
  public List<Sample> getSamples() {
    return new ArrayList<>(sampleMap.values());
  }

}
