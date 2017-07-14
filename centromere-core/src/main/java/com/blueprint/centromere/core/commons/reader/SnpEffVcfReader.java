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
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.commons.support.AminoAcidConverter;
import com.blueprint.centromere.core.commons.support.DataSetSupport;
import com.blueprint.centromere.core.commons.support.GenericDataSetSupport;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidDataFileException;
import com.blueprint.centromere.core.dataimport.exception.InvalidGeneException;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.dataimport.reader.MultiRecordLineFileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Reads variant call format (VCF) v4.x files and maps them to {@link Mutation} records.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class SnpEffVcfReader extends MultiRecordLineFileReader<Mutation> {
  
  private static final Logger logger = LoggerFactory.getLogger(SnpEffVcfReader.class);
  
  private final GeneRepository geneRepository;
  private final SampleRepository sampleRepository;
  private final DataSetSupport support;
  
  private boolean formatTestFlag = false;

  public SnpEffVcfReader(
      GeneRepository geneRepository,
      SampleRepository sampleRepository,
      DataSetSupport support
  ) {
    this.geneRepository = geneRepository;
    this.sampleRepository = sampleRepository;
    this.support = support;
  }
  
  public SnpEffVcfReader(
      GeneRepository geneRepository,
      SampleRepository sampleRepository,
      SubjectRepository subjectRepository
  ) {
    this.sampleRepository = sampleRepository;
    this.geneRepository = geneRepository;
    this.support = new GenericDataSetSupport(subjectRepository, sampleRepository);
  }

  /**
   * Extracts multiple records from a single line of the text file.  If no valid records are found,
   * an empty list should be returned.
   */
  @Override
  protected List<Mutation> getRecordsFromLine(String line) throws DataImportException {
    if (!formatTestFlag) testFileFormat();
    List<Mutation> mutations = new ArrayList<>();
    String[] bits = line.split(this.getDelimiter());
    for (int i = 9; i < bits.length; i++){
      
      Sample sample = getSample(this.getHeaders().get(i));
      if (sample == null){
        if (this.getImportOptions().skipInvalidSamples()){
          continue;
        } else {
          throw new InvalidSampleException(String.format("Unable to identify sample: %s", this.getHeaders().get(i)));
        }
      }
      
      Mutation mutation = new Mutation();
      mutation.setSampleId(sample.getId());
      mutation.setSubjectId(sample.getSubjectId());
      mutation.setDataSetId(this.getDataSet().getId());
      mutation.setDataFileId(this.getDataFile().getId());
      
      mutation.setChromosome(bits[0].trim());
      mutation.setDnaStartPosition(Integer.parseInt(bits[1].trim()));
      mutation.setReferenceAllele(bits[3].trim());
      mutation.setAlternateAllele(bits[4].trim());
      if (!bits[2].trim().replaceAll("\\.", "").equals("")) {
        mutation.setExternalReferenes(Arrays.asList(bits[2].trim().split(";")));
      }
      
      Map<String,String> info = parseInfo(bits[7].trim());
      mutation.addAttributes(info);

      Gene gene = getGene(info.getOrDefault("SNPEFF_GENE_NAME", "n/a"));
      if (gene != null){
        mutation.setGeneId(gene.getId());
      }

      mutation.setVariantClassification(info.getOrDefault("SNPEFF_FUNCTIONAL_CLASS", "n/a"));
      mutation.setVariantType(info.getOrDefault("SNPEFF_EFFECT", "n/a"));
      mutation.setCodonChange(info.getOrDefault("SNPEFF_CODON_CHANGE", null));
      
      if (info.containsKey("SNPEFF_AMINO_ACID_CHANGE")){
        for (String s: info.get("SNPEFF_AMINO_ACID_CHANGE").split("/")){
          if (s.toLowerCase().startsWith("p.")){
            try {
              mutation.setProteinChange(AminoAcidConverter.shortToSingleLetterMutation(s));
            } catch (Exception e){
              logger.error(String.format("Error parsing amino acid variant: %s", s));
              throw new DataImportException(e);
            }
          } else if (s.toLowerCase().startsWith("c.")){
            mutation.setNucleotideChange(s);
          }
        }
        
      }
      
      mutation.setNucleotideTranscript(info.getOrDefault("SNPEFF_TRANSCRIPT_ID", null));
      mutation.setProteinTranscript(null);
      
      mutation.setAlternateTranscripts(getAlternateTranscripts(info));
      mutation.addAttributes(info);
      
      mutations.add(mutation);
      
    }
    return mutations;
  }

  @Override
  protected boolean isSkippableLine(String line) {
    return line.startsWith("##");
  }
  
  protected List<Mutation.VariantTranscript> getAlternateTranscripts(Map<String, String> info) 
      throws DataImportException {
    List<Mutation.VariantTranscript> transcripts = new ArrayList<>();
    for (String variant: info.getOrDefault("EFF", "").split(",")){
      if (variant.startsWith("NON_SYNONYMOUS_CODING")){
        String[] bits = variant.replaceFirst("NON_SYNONYMOUS_CODING\\(", "").split("\\|");
        Mutation.VariantTranscript transcript = new Mutation.VariantTranscript();
        transcript.setTranscriptChange(bits[3].split("/")[0]);
        transcript.setTranscriptId(bits[7]);
        transcript.setVariantClassification(bits[1]);
        Gene gene = getGene(bits[4]);
        if (gene != null) transcript.setGeneId(gene.getId());
        transcripts.add(transcript);
      }
    }
    return transcripts;
  }
  
  protected Sample getSample(String name) throws InvalidSampleException {
    return support.findOrCreateSample(name, this.getDataSet()).orElse(null);
  }
  
  protected Gene getGene(String identifier) throws InvalidGeneException {
    Optional<Gene> optional = geneRepository.bestGuess(identifier);
    return optional.orElse(null);
  }

  /**
   * Extracts the INFO column and returns it as key-value attributes, for the {@link Mutation#attributes}
   *   field.
   * 
   * @param info INFO column value
   * @return attribute map
   */
  protected Map<String, String> parseInfo(String info){
    Map<String,String> attributes = new HashMap<>();
    for (String s: info.split(";")){
      String[] b = s.split("=");
      String key = b[0];
      String val = b.length > 1 ? b[1] : "";
      attributes.put(key, val);
    }
    return attributes;
  }
  
  protected void testFileFormat() throws InvalidDataFileException {
    try {
      Assert.isTrue(this.getHeaders().get(0).replaceAll("#", "").equalsIgnoreCase("chrom"), 
          String.format("First column should be 'CHROM', was: %s", this.getHeaders().get(0)));
      Assert.isTrue(this.getHeaders().get(1).equalsIgnoreCase("pos"),
          String.format("Second column should be 'POS', was: %s", this.getHeaders().get(1)));
      Assert.isTrue(this.getHeaders().get(2).equalsIgnoreCase("id"),
          String.format("Third column should be 'ID', was: %s", this.getHeaders().get(2)));
      Assert.isTrue(this.getHeaders().get(3).equalsIgnoreCase("ref"),
          String.format("Fourth column should be 'REF', was: %s", this.getHeaders().get(3)));
      Assert.isTrue(this.getHeaders().get(4).equalsIgnoreCase("alt"),
          String.format("Fifth column should be 'ALT', was: %s", this.getHeaders().get(4)));
      Assert.isTrue(this.getHeaders().get(5).equalsIgnoreCase("qual"),
          String.format("Sixth column should be 'QUAL', was: %s", this.getHeaders().get(5)));
      Assert.isTrue(this.getHeaders().get(6).equalsIgnoreCase("filter"),
          String.format("Seventh column should be 'FILTER', was: %s", this.getHeaders().get(6)));
      Assert.isTrue(this.getHeaders().get(7).equalsIgnoreCase("info"),
          String.format("Eight column should be 'INFO', was: %s", this.getHeaders().get(7)));
      Assert.isTrue(this.getHeaders().get(8).equalsIgnoreCase("format"),
          String.format("Ninth column should be 'FORMAT', was: %s", this.getHeaders().get(8)));
    } catch (Exception e){
      throw new InvalidDataFileException(e);
    }
    formatTestFlag = true;
  }

  public GeneRepository getGeneRepository() {
    return geneRepository;
  }

  public SampleRepository getSampleRepository() {
    return sampleRepository;
  }

  public DataSetSupport getSupport() {
    return support;
  }
}
