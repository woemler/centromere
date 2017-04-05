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
import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.TcgaSupport;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.StandardRecordFileReader;
import com.blueprint.centromere.core.model.ModelSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File reader for mutation annotation format (MAF) files.  Supports generic MAF files, as well
 *   as files from specific data sets (eg. TCGA).
 *
 * @author woemler
 */
public class MafReader extends StandardRecordFileReader<Mutation>
    implements ModelSupport<Mutation>, DataFileAware {

  private static final Logger logger = LoggerFactory.getLogger(MafReader.class);

  private final TcgaSupport tcgaSupport;
  private final GeneRepository geneRepository;
  private DataFile dataFile;
  private Class<Mutation> model = Mutation.class;

  private Map<String, Integer> columnMap = new HashMap<>();
  private Map<String, Sample> sampleMap = new HashMap<>();
  private String delimiter = "\t";
  private boolean headerFlag = true;

  public MafReader(GeneRepository geneRepository, TcgaSupport tcgaSupport) {
    this.tcgaSupport = tcgaSupport;
    this.geneRepository = geneRepository;
  }

  @Override
  protected Mutation getRecordFromLine(String line) throws DataImportException {

    Mutation mutation = null;
    mutation.setDataFile(dataFile);
    mutation.setDataFileId(dataFile.getId());

    Sample sample = getSampleFromLine(line);
    if (this.getImportOptions().isInvalidSample(sample)) {
      logger.info("Skipping line due to invalid sample: " + line);
      return null;
    } else {
      mutation.setSample(sample);
      mutation.setSampleId(sample.getId());
    }

    Gene gene = getGeneFromLine(line);
    if (this.getImportOptions().isInvalidGene(gene)){
      logger.info("Skipping line due to invalid gene: " + line);
      return null;
    } else {
      mutation.setGene(gene);
      mutation.setGeneId(gene.getId());
    }

    mutation.setReferenceGenome(parseReferenceGenome(line));
    mutation.setChromosome(getColumnValue(line, "chromosome"));
    mutation.setStrand(getColumnValue(line, "strand"));
    mutation.setDnaStartPosition(Integer.parseInt(getColumnValue(line, "start_position")));
    mutation.setDnaStopPosition(Integer.parseInt(getColumnValue(line, "end_position")));
    mutation.setReferenceAllele(getColumnValue(line, "reference_allele"));
    mutation.setAlternateAllele(getColumnValue(line, "tumor_seq_allele2"));
    mutation.setcDnaChange(getColumnValue(line, "cdna_change"));
    mutation.setCodonChange(getColumnValue(line, "codon_change"));
    mutation.setProteinChange(getColumnValue(line, "protein_change"));
    mutation.setMutationClassification(getColumnValue(line, "variant_classification"));
    mutation.setMutationType(getColumnValue(line, "variant_type"));

    return mutation;
  }

  private Gene getGeneFromLine(String line){
    Gene gene = null;
    if (hasColumn("entrez_gene_id")){
      List<Gene> genes = geneRepository.findByPrimaryReferenceId(getColumnValue(line, "entrez_gene_id"));
      if (!genes.isEmpty()) gene = genes.get(0);
    }
    if (gene == null && hasColumn("hugo_symbol")){
      List<Gene> genes = geneRepository.findByPrimaryGeneSymbol(getColumnValue(line, "hugo_symbol"));
      if (!genes.isEmpty()) gene = genes.get(0);
    }
    return gene;
  }

  private Sample getSampleFromLine(String line){
    String sampleName = null;
    if (hasColumn("tumor_sample_barcode")){
      sampleName = getColumnValue(line, "tumor_sample_barcode");
    } else {
      // TODO: get general sample name, not just TCGA
    }

    if (sampleMap.containsKey(sampleName)){
      return sampleMap.get(sampleName);
    }

    Sample sample = tcgaSupport.findSample(sampleName);
    if (sample == null) {
      sample = tcgaSupport.createSample(sampleName, dataFile.getDataSet());
    }
    sampleMap.put(sampleName, sample);

    return sample;

  }

  private String parseReferenceGenome(String line){
    String ref = null;
    if (hasColumn("ncbi_build")){
      ref = "hg" + getColumnValue(line, "ncbi_build");
    }
    // TODO: support for mroe than TCGA
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
  public void setDataFile(DataFile dataFile) {
    this.dataFile = dataFile;
  }

  @Override
  public DataFile getDataFile() {
    return dataFile;
  }

  @Override
  public Class<Mutation> getModel() {
    return model;
  }

  @Override
  public void setModel(Class<Mutation> model) {
    this.model = model;
  }
}
