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

package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.GeneCopyNumber;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.model.SegmentCopyNumber;
import com.blueprint.centromere.core.commons.model.TranscriptExpression;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author woemler
 */
public abstract class AbstractRepositoryTests {

  @Autowired private GeneRepository geneRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;

  @Before
  public void setup() throws Exception {
      
    geneRepository.deleteAll();
    dataSetRepository.deleteAll();
    dataFileRepository.deleteAll();
    sampleRepository.deleteAll();
    geneExpressionRepository.deleteAll();
    
    // Genes

    List<Gene> genes = new ArrayList<>();

    Gene geneA = new Gene();
    geneA.setGeneId("1");
    geneA.setPrimaryGeneSymbol("GeneA");
    geneA.setTaxId(9606);
    geneA.setChromosome("1");
    geneA.setDescription("Test Gene A");
    geneA.setGeneType("protein-coding");
    geneA.addAttribute("isKinase","Y");
    geneA.addAlias("ABC");
    genes.add(geneA);

    Gene geneB = new Gene();
    geneB.setGeneId("2");
    geneB.setPrimaryGeneSymbol("GeneB");
    geneB.setTaxId(9606);
    geneB.setChromosome("5");
    geneB.setDescription("Test Gene B");
    geneB.setGeneType("protein-coding");
    geneB.addAttribute("isKinase", "N");
    geneB.addAlias("DEF");
    genes.add(geneB);

    Gene geneC = new Gene();
    geneC.setGeneId("3");
    geneC.setPrimaryGeneSymbol("GeneC");
    geneC.setTaxId(9606);
    geneC.setChromosome("9");
    geneC.setDescription("Test Gene C");
    geneC.setGeneType("pseudo");
    geneC.addAttribute("isKinase", "N");
    geneC.addAlias("GHI");
    genes.add(geneC);

    Gene geneD = new Gene();
    geneD.setGeneId("4");
    geneD.setPrimaryGeneSymbol("GeneD");
    geneD.setTaxId(9606);
    geneD.setChromosome("X");
    geneD.setDescription("Test Gene D");
    geneD.setGeneType("protein-coding");
    geneD.addAttribute("isKinase", "Y");
    geneD.addAlias("JKL");
    genes.add(geneD);

    Gene geneE = new Gene();
    geneE.setGeneId("5");
    geneE.setPrimaryGeneSymbol("GeneE");
    geneE.setTaxId(9606);
    geneE.setChromosome("13");
    geneE.setDescription("Test Gene E");
    geneE.setGeneType("pseudo");
    geneE.addAttribute("isKinase", "N");
    geneE.addAlias("MNO");
    genes.add(geneE);
    
    geneRepository.insert(genes);
    
    // Data Sets

    List<DataSet> dataSets = new ArrayList<>();

    DataSet dataSetA = new DataSet();
    dataSetA.setDataSetId("DataSetA");
    dataSetA.setName("DataSetA");
    dataSetA.setSource("Internal");
    dataSetA.setVersion("1.0");
    dataSetA.setDescription("This is an example data set.");
    dataSets.add(dataSetA);

    DataSet dataSetB = new DataSet();
    dataSetB.setDataSetId("DataSetB");
    dataSetA.setName("DataSetB");
    dataSetB.setSource("External");
    dataSetB.setVersion("1.0");
    dataSetB.setDescription("This is an example data set.");
    dataSets.add(dataSetB);

    DataSet dataSetC = new DataSet();
    dataSetC.setDataSetId("DataSetC");
    dataSetC.setName("DataSetC");
    dataSetC.setSource("Internal");
    dataSetC.setVersion("2.0");
    dataSetC.setDescription("This is an example data set.");
    dataSets.add(dataSetC);

    DataSet dataSetD = new DataSet();
    dataSetD.setDataSetId("DataSetD");
    dataSetD.setName("DataSetD");
    dataSetD.setSource("External");
    dataSetD.setVersion("1.0");
    dataSetD.setDescription("This is an example data set.");
    dataSets.add(dataSetD);

    DataSet dataSetE = new DataSet();
    dataSetE.setDataSetId("DataSetE");
    dataSetE.setName("DataSetE");
    dataSetE.setSource("Internal");
    dataSetE.setVersion("1.0");
    dataSetE.setDescription("This is an example data set.");
    dataSets.add(dataSetE);
    
    dataSetRepository.insert(dataSets);
    
    // Data Files

    List<DataFile> dataFiles = new ArrayList<>();

    DataFile dataFileA = new DataFile();
    dataFileA.setDataSetId(dataSetA.getId());
    dataFileA.setFilePath("/path/to/fileA");
    dataFileA.setDataType("GCT RNA-Seq gene expression");
    dataFileA.setDateCreated(new Date());
    dataFileA.setDateUpdated(new Date());
    dataFileA.setDataSetId(dataSetA.getId());
    dataFileA.setModel(GeneExpression.class);
    dataFiles.add(dataFileA);

    DataFile dataFileB = new DataFile();
    dataFileB.setDataSetId(dataSetA.getId());
    dataFileB.setFilePath("/path/to/fileB");
    dataFileB.setDataType("GCT RNA-Seq transcript expression");
    dataFileB.setDateCreated(new Date());
    dataFileB.setDateUpdated(new Date());
    dataFileB.setDataSetId(dataSetA.getId());
    dataFileB.setModel(TranscriptExpression.class);
    dataFiles.add(dataFileB);

    DataFile dataFileC = new DataFile();
    dataFileC.setDataSetId(dataSetA.getId());
    dataFileC.setFilePath("/path/to/fileC");
    dataFileC.setDataType("MAF mutations");
    dataFileC.setDateCreated(new Date());
    dataFileC.setDateUpdated(new Date());
    dataFileC.setDataSetId(dataSetA.getId());
    dataFileC.setModel(Mutation.class);
    dataFiles.add(dataFileC);

    DataFile dataFileD = new DataFile();
    dataFileD.setDataSetId(dataSetB.getId());
    dataFileD.setFilePath("/path/to/fileD");
    dataFileD.setDataType("Gene copy number");
    dataFileD.setDateCreated(new Date());
    dataFileD.setDateUpdated(new Date());
    dataFileD.setDataSetId(dataSetB.getId());
    dataFileD.setModel(GeneCopyNumber.class);
    dataFiles.add(dataFileD);

    DataFile dataFileE = new DataFile();
    dataFileE.setDataSetId(dataSetE.getId());
    dataFileE.setFilePath("/path/to/fileE");
    dataFileE.setDataType("Segment copy number");
    dataFileE.setDateCreated(new Date());
    dataFileE.setDateUpdated(new Date());
    dataFileE.setDataSetId(dataSetB.getId());
    dataFileE.setModel(SegmentCopyNumber.class);
    dataFiles.add(dataFileE);
    
    dataFileRepository.insert(dataFiles);
    
    dataSetA.setDataFileIds(Arrays.asList(dataFileA.getId(), dataFileB.getId(), dataFileC.getId()));
    dataSetB.setDataFileIds(Arrays.asList(dataFileD.getId(), dataFileE.getId()));
    dataSetRepository.update(dataSetA);
    dataSetRepository.update(dataSetB);
    
    // Samples

    List<Sample> samples = new ArrayList<>();

    Sample sampleA = new Sample();
    sampleA.setSampleId("SampleA");
    sampleA.setTissue("Lung");
    sampleA.setHistology("carcinoma");
    sampleA.setSampleType("cell line");
    sampleA.setNotes("This is an example sample.");
    sampleA.addAttribute("tag", "tagA");
    sampleA.setSpecies("H sapiens");
    sampleA.setGender("M");
    samples.add(sampleA);

    Sample sampleB = new Sample();
    sampleB.setSampleId("SampleB");
    sampleB.setTissue("Liver");
    sampleB.setHistology("carcinoma");
    sampleB.setSampleType("cell line");
    sampleB.setNotes("This is an example sample.");
    sampleB.addAttribute("tag", "tagB");
    sampleB.setSpecies("H sapiens");
    sampleB.setGender("F");
    samples.add(sampleB);

    Sample sampleC = new Sample();
    sampleC.setSampleId("SampleC");
    sampleC.setTissue("Liver");
    sampleC.setHistology("carcinoma: HCC");
    sampleC.setSampleType("PDX");
    sampleC.setNotes("This is an example sample.");
    sampleC.addAttribute("tag", "tagA");
    sampleC.setSpecies("H sapiens");
    sampleC.setGender("F");
    samples.add(sampleC);

    Sample sampleD = new Sample();
    sampleD.setSampleId("SampleD");
    sampleD.setTissue("Breast");
    sampleD.setHistology("ductal carcinoma");
    sampleD.setSampleType("cell line");
    sampleD.setNotes("This is an example sample.");
    sampleD.addAttribute("tag", "tagA");
    sampleD.setSpecies("H sapiens");
    sampleD.setGender("U");
    samples.add(sampleD);

    Sample sampleE = new Sample();
    sampleE.setSampleId("SampleE");
    sampleE.setTissue("Breast");
    sampleE.setHistology("ductal carcinoma");
    sampleE.setSampleType("PDX");
    sampleE.setNotes("This is an example sample.");
    sampleE.addAttribute("tag", "tagB");
    sampleE.setSpecies("H sapiens");
    sampleE.setGender("M");
    samples.add(sampleE);
    
    sampleRepository.insert(samples);
    
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: samples){
      sampleIds.add(sample.getId());
    }
    
    dataSetA.setSampleIds(sampleIds);
    dataSetRepository.update(dataSetA);
    
    // Gene Expression

    List<GeneExpression> data = new ArrayList<>();

    GeneExpression geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleA.getId());
    geneExpression.setGeneId(geneA.getId());
    geneExpression.setDataFileId(dataFileA.getId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(1.23);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleA.getId());
    geneExpression.setGeneId(geneB.getId());
    geneExpression.setDataFileId(dataFileA.getId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(2.34);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleA.getId());
    geneExpression.setGeneId(geneC.getId());
    geneExpression.setDataFileId(dataFileA.getId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(4.56);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleB.getId());
    geneExpression.setGeneId(geneA.getId());
    geneExpression.setDataFileId(dataFileA.getId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(6.78);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleB.getId());
    geneExpression.setGeneId(geneB.getId());
    geneExpression.setDataFileId(dataFileA.getId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(9.10);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleB.getId());
    geneExpression.setGeneId(geneC.getId());
    geneExpression.setDataFileId(dataFileA.getId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(12.34);
    data.add(geneExpression);
    
    geneExpressionRepository.insert(data);
    
  }

}
