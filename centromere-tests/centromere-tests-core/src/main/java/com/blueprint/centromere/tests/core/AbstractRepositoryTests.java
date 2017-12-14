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
import com.blueprint.centromere.core.mongodb.model.MongoDataFile;
import com.blueprint.centromere.core.mongodb.model.MongoDataSet;
import com.blueprint.centromere.core.mongodb.model.MongoGene;
import com.blueprint.centromere.core.mongodb.model.MongoGeneExpression;
import com.blueprint.centromere.core.mongodb.model.MongoSample;
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

    Gene geneA = new MongoGene();
    geneA.setGeneId("1");
    geneA.setSymbol("GeneA");
    geneA.setTaxId(9606);
    geneA.setChromosome("1");
    geneA.setDescription("Test Gene A");
    geneA.setGeneType("protein-coding");
    geneA.addAttribute("isKinase","Y");
    geneA.addAlias("ABC");
    genes.add(geneA);

    Gene geneB = new MongoGene();
    geneB.setGeneId("2");
    geneB.setSymbol("GeneB");
    geneB.setTaxId(9606);
    geneB.setChromosome("5");
    geneB.setDescription("Test Gene B");
    geneB.setGeneType("protein-coding");
    geneB.addAttribute("isKinase", "N");
    geneB.addAlias("DEF");
    genes.add(geneB);

    Gene geneC = new MongoGene();
    geneC.setGeneId("3");
    geneC.setSymbol("GeneC");
    geneC.setTaxId(9606);
    geneC.setChromosome("9");
    geneC.setDescription("Test Gene C");
    geneC.setGeneType("pseudo");
    geneC.addAttribute("isKinase", "N");
    geneC.addAlias("GHI");
    genes.add(geneC);

    Gene geneD = new MongoGene();
    geneD.setGeneId("4");
    geneD.setSymbol("GeneD");
    geneD.setTaxId(9606);
    geneD.setChromosome("X");
    geneD.setDescription("Test Gene D");
    geneD.setGeneType("protein-coding");
    geneD.addAttribute("isKinase", "Y");
    geneD.addAlias("JKL");
    genes.add(geneD);

    Gene geneE = new MongoGene();
    geneE.setGeneId("5");
    geneE.setSymbol("GeneE");
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

    DataSet dataSetA = new MongoDataSet();
    dataSetA.setDataSetId("DataSetA");
    dataSetA.setName("DataSetA");
    dataSetA.setSource("Internal");
    dataSetA.setVersion("1.0");
    dataSetA.setDescription("This is an example data set.");
    dataSets.add(dataSetA);

    DataSet dataSetB = new MongoDataSet();
    dataSetB.setDataSetId("DataSetB");
    dataSetA.setName("DataSetB");
    dataSetB.setSource("External");
    dataSetB.setVersion("1.0");
    dataSetB.setDescription("This is an example data set.");
    dataSets.add(dataSetB);

    DataSet dataSetC = new MongoDataSet();
    dataSetC.setDataSetId("DataSetC");
    dataSetC.setName("DataSetC");
    dataSetC.setSource("Internal");
    dataSetC.setVersion("2.0");
    dataSetC.setDescription("This is an example data set.");
    dataSets.add(dataSetC);

    DataSet dataSetD = new MongoDataSet();
    dataSetD.setDataSetId("DataSetD");
    dataSetD.setName("DataSetD");
    dataSetD.setSource("External");
    dataSetD.setVersion("1.0");
    dataSetD.setDescription("This is an example data set.");
    dataSets.add(dataSetD);

    DataSet dataSetE = new MongoDataSet();
    dataSetE.setDataSetId("DataSetE");
    dataSetE.setName("DataSetE");
    dataSetE.setSource("Internal");
    dataSetE.setVersion("1.0");
    dataSetE.setDescription("This is an example data set.");
    dataSets.add(dataSetE);
    
    dataSetRepository.insert(dataSets);
    
    // Data Files

    List<DataFile> dataFiles = new ArrayList<>();

    DataFile dataFileA = new MongoDataFile();
    dataFileA.setDataSetId(dataSetA.getDataSetId());
    dataFileA.setFilePath("/path/to/fileA");
    dataFileA.setDataType("GCT RNA-Seq gene expression");
    dataFileA.setDateCreated(new Date());
    dataFileA.setDateUpdated(new Date());
    dataFileA.setModel(GeneExpression.class);
    dataFileA.setDataFileId(DataFile.generateFileId(dataFileA));
    dataFiles.add(dataFileA);

    DataFile dataFileB = new MongoDataFile();
    dataFileB.setDataSetId(dataSetA.getDataSetId());
    dataFileB.setFilePath("/path/to/fileB");
    dataFileB.setDataType("GCT RNA-Seq transcript expression");
    dataFileB.setDateCreated(new Date());
    dataFileB.setDateUpdated(new Date());
    dataFileB.setModel(TranscriptExpression.class);
    dataFileB.setDataFileId(DataFile.generateFileId(dataFileB));
    dataFiles.add(dataFileB);

    DataFile dataFileC = new MongoDataFile();
    dataFileC.setDataSetId(dataSetA.getDataSetId());
    dataFileC.setFilePath("/path/to/fileC");
    dataFileC.setDataType("MAF mutations");
    dataFileC.setDateCreated(new Date());
    dataFileC.setDateUpdated(new Date());
    dataFileC.setModel(Mutation.class);
    dataFileC.setDataFileId(DataFile.generateFileId(dataFileC));
    dataFiles.add(dataFileC);

    DataFile dataFileD = new MongoDataFile();
    dataFileD.setDataSetId(dataSetB.getDataSetId());
    dataFileD.setFilePath("/path/to/fileD");
    dataFileD.setDataType("Gene copy number");
    dataFileD.setDateCreated(new Date());
    dataFileD.setDateUpdated(new Date());
    dataFileD.setModel(GeneCopyNumber.class);
    dataFileD.setDataFileId(DataFile.generateFileId(dataFileD));
    dataFiles.add(dataFileD);

    DataFile dataFileE = new MongoDataFile();
    dataFileE.setDataSetId(dataSetE.getDataSetId());
    dataFileE.setFilePath("/path/to/fileE");
    dataFileE.setDataType("Segment copy number");
    dataFileE.setDateCreated(new Date());
    dataFileE.setDateUpdated(new Date());
    dataFileE.setModel(SegmentCopyNumber.class);
    dataFileE.setDataFileId(DataFile.generateFileId(dataFileE));
    dataFiles.add(dataFileE);
    
    dataFileRepository.insert(dataFiles);
    
    dataSetA.setDataFileIds(Arrays.asList(dataFileA.getId(), dataFileB.getId(), dataFileC.getId()));
    dataSetB.setDataFileIds(Arrays.asList(dataFileD.getId(), dataFileE.getId()));
    dataSetRepository.update(dataSetA);
    dataSetRepository.update(dataSetB);
    
    // Samples

    List<Sample> samples = new ArrayList<>();

    Sample sampleA = new MongoSample();
    sampleA.setSampleId("SampleA");
    sampleA.setTissue("Lung");
    sampleA.setHistology("carcinoma");
    sampleA.setSampleType("cell line");
    sampleA.setNotes("This is an example sample.");
    sampleA.addAttribute("tag", "tagA");
    sampleA.setSpecies("H sapiens");
    sampleA.setGender("M");
    samples.add(sampleA);

    Sample sampleB = new MongoSample();
    sampleB.setSampleId("SampleB");
    sampleB.setTissue("Liver");
    sampleB.setHistology("carcinoma");
    sampleB.setSampleType("cell line");
    sampleB.setNotes("This is an example sample.");
    sampleB.addAttribute("tag", "tagB");
    sampleB.setSpecies("H sapiens");
    sampleB.setGender("F");
    samples.add(sampleB);

    Sample sampleC = new MongoSample();
    sampleC.setSampleId("SampleC");
    sampleC.setTissue("Liver");
    sampleC.setHistology("carcinoma: HCC");
    sampleC.setSampleType("PDX");
    sampleC.setNotes("This is an example sample.");
    sampleC.addAttribute("tag", "tagA");
    sampleC.setSpecies("H sapiens");
    sampleC.setGender("F");
    samples.add(sampleC);

    Sample sampleD = new MongoSample();
    sampleD.setSampleId("SampleD");
    sampleD.setTissue("Breast");
    sampleD.setHistology("ductal carcinoma");
    sampleD.setSampleType("cell line");
    sampleD.setNotes("This is an example sample.");
    sampleD.addAttribute("tag", "tagA");
    sampleD.setSpecies("H sapiens");
    sampleD.setGender("U");
    samples.add(sampleD);

    Sample sampleE = new MongoSample();
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
      sampleIds.add(sample.getSampleId());
    }
    
    dataSetA.setSampleIds(sampleIds);
    dataSetRepository.update(dataSetA);
    
    // Gene Expression

    List<GeneExpression> data = new ArrayList<>();

    GeneExpression geneExpression = new MongoGeneExpression();
    geneExpression.setSampleId(sampleA.getSampleId());
    geneExpression.setGeneId(geneA.getGeneId());
    geneExpression.setDataFileId(dataFileA.getDataFileId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(1.23);
    data.add(geneExpression);

    geneExpression = new MongoGeneExpression();
    geneExpression.setSampleId(sampleA.getSampleId());
    geneExpression.setGeneId(geneB.getGeneId());
    geneExpression.setDataFileId(dataFileA.getDataFileId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(2.34);
    data.add(geneExpression);

    geneExpression = new MongoGeneExpression();
    geneExpression.setSampleId(sampleA.getSampleId());
    geneExpression.setGeneId(geneC.getGeneId());
    geneExpression.setDataFileId(dataFileA.getDataFileId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(4.56);
    data.add(geneExpression);

    geneExpression = new MongoGeneExpression();
    geneExpression.setSampleId(sampleB.getSampleId());
    geneExpression.setGeneId(geneA.getGeneId());
    geneExpression.setDataFileId(dataFileA.getDataFileId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(6.78);
    data.add(geneExpression);

    geneExpression = new MongoGeneExpression();
    geneExpression.setSampleId(sampleB.getSampleId());
    geneExpression.setGeneId(geneB.getGeneId());
    geneExpression.setDataFileId(dataFileA.getDataFileId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(9.10);
    data.add(geneExpression);

    geneExpression = new MongoGeneExpression();
    geneExpression.setSampleId(sampleB.getSampleId());
    geneExpression.setGeneId(geneC.getGeneId());
    geneExpression.setDataFileId(dataFileA.getDataFileId());
    geneExpression.setDataSetId(dataFileA.getDataSetId());
    geneExpression.setValue(12.34);
    data.add(geneExpression);
    
    geneExpressionRepository.insert(data);
    
  }

}
