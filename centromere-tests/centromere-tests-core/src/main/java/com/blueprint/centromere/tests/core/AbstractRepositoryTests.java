/*
 * Copyright 2016 the original author or authors
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

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.DataSet;
import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.repositories.DataFileRepository;
import com.blueprint.centromere.core.commons.repositories.DataSetRepository;
import com.blueprint.centromere.core.commons.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
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
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;

  @Before
  public void setup() throws Exception {
      
    geneRepository.deleteAll();
    dataSetRepository.deleteAll();
    dataFileRepository.deleteAll();
    subjectRepository.deleteAll();
    sampleRepository.deleteAll();
    geneExpressionRepository.deleteAll();
    
    // Genes

    List<Gene> genes = new ArrayList<>();

    Gene geneA = new Gene();
    geneA.setPrimaryReferenceId("1");
    geneA.setPrimaryGeneSymbol("GeneA");
    geneA.setTaxId(9606);
    geneA.setChromosome("1");
    geneA.setDescription("Test Gene A");
    geneA.setGeneType("protein-coding");
    geneA.addAttribute("isKinase","Y");
    geneA.addAlias("ABC");
    genes.add(geneA);

    Gene geneB = new Gene();
    geneB.setPrimaryReferenceId("2");
    geneB.setPrimaryGeneSymbol("GeneB");
    geneB.setTaxId(9606);
    geneB.setChromosome("5");
    geneB.setDescription("Test Gene B");
    geneB.setGeneType("protein-coding");
    geneB.addAttribute("isKinase", "N");
    geneB.addAlias("DEF");
    genes.add(geneB);

    Gene geneC = new Gene();
    geneC.setPrimaryReferenceId("3");
    geneC.setPrimaryGeneSymbol("GeneC");
    geneC.setTaxId(9606);
    geneC.setChromosome("9");
    geneC.setDescription("Test Gene C");
    geneC.setGeneType("pseudo");
    geneC.addAttribute("isKinase", "N");
    geneC.addAlias("GHI");
    genes.add(geneC);

    Gene geneD = new Gene();
    geneD.setPrimaryReferenceId("4");
    geneD.setPrimaryGeneSymbol("GeneD");
    geneD.setTaxId(9606);
    geneD.setChromosome("X");
    geneD.setDescription("Test Gene D");
    geneD.setGeneType("protein-coding");
    geneD.addAttribute("isKinase", "Y");
    geneD.addAlias("JKL");
    genes.add(geneD);

    Gene geneE = new Gene();
    geneE.setPrimaryReferenceId("5");
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
    dataSetA.setName("DataSetA");
    dataSetA.setSource("Internal");
    dataSetA.setVersion("1.0");
    dataSetA.setDescription("This is an example data set.");
    dataSets.add(dataSetA);

    DataSet dataSetB = new DataSet();
    dataSetB.setName("DataSetB");
    dataSetB.setSource("External");
    dataSetB.setVersion("1.0");
    dataSetB.setDescription("This is an example data set.");
    dataSets.add(dataSetB);

    DataSet dataSetC = new DataSet();
    dataSetC.setName("DataSetC");
    dataSetC.setSource("Internal");
    dataSetC.setVersion("2.0");
    dataSetC.setDescription("This is an example data set.");
    dataSets.add(dataSetC);

    DataSet dataSetD = new DataSet();
    dataSetD.setName("DataSetD");
    dataSetD.setSource("External");
    dataSetD.setVersion("1.0");
    dataSetD.setDescription("This is an example data set.");
    dataSets.add(dataSetD);

    DataSet dataSetE = new DataSet();
    dataSetE.setName("DataSetE");
    dataSetE.setSource("Internal");
    dataSetE.setVersion("1.0");
    dataSetE.setDescription("This is an example data set.");
    dataSets.add(dataSetE);
    
    dataSetRepository.insert(dataSets);
    
    // Data Files

    List<DataFile> dataFiles = new ArrayList<>();

    DataFile dataFileA = new DataFile();
    dataFileA.setFilePath("/path/to/fileA");
    dataFileA.setDataType("GCT RNA-Seq gene expression");
    dataFileA.setDateCreated(new Date());
    dataFileA.setDateUpdated(new Date());
    dataFileA.setDataSet(dataSetA);
    dataFiles.add(dataFileA);

    DataFile dataFileB = new DataFile();
    dataFileB.setFilePath("/path/to/fileB");
    dataFileB.setDataType("GCT RNA-Seq transcript expression");
    dataFileB.setDateCreated(new Date());
    dataFileB.setDateUpdated(new Date());
    dataFileB.setDataSet(dataSetA);
    dataFiles.add(dataFileB);

    DataFile dataFileC = new DataFile();
    dataFileC.setFilePath("/path/to/fileC");
    dataFileC.setDataType("MAF mutations");
    dataFileC.setDateCreated(new Date());
    dataFileC.setDateUpdated(new Date());
    dataFileC.setDataSet(dataSetA);
    dataFiles.add(dataFileC);

    DataFile dataFileD = new DataFile();
    dataFileD.setFilePath("/path/to/fileD");
    dataFileD.setDataType("Gene copy number");
    dataFileD.setDateCreated(new Date());
    dataFileD.setDateUpdated(new Date());
    dataFileD.setDataSet(dataSetB);
    dataFiles.add(dataFileD);

    DataFile dataFileE = new DataFile();
    dataFileE.setFilePath("/path/to/fileE");
    dataFileE.setDataType("Segment copy number");
    dataFileE.setDateCreated(new Date());
    dataFileE.setDateUpdated(new Date());
    dataFileE.setDataSet(dataSetB);
    dataFiles.add(dataFileE);
    
    dataFileRepository.insert(dataFiles);
    
    dataSetA.setDataFiles(Arrays.asList(dataFileA, dataFileB, dataFileC));
    dataSetB.setDataFiles(Arrays.asList(dataFileD, dataFileE));
    dataSetRepository.update(dataSetA);
    dataSetRepository.update(dataSetB);
    
    // Subjects

    List<Subject> subjects = new ArrayList<>();

    Subject subjectA = new Subject();
    subjectA.setName("SubjectA");
    subjectA.setSpecies("Human");
    subjectA.setGender("M");
    subjectA.setNotes("This is an example subject");
    subjectA.addAlias("subject_a");
    subjectA.addAttribute("tag", "tagA");
    subjects.add(subjectA);

    Subject subjectB = new Subject();
    subjectB.setName("SubjectB");
    subjectB.setSpecies("Human");
    subjectB.setGender("F");
    subjectB.setNotes("This is an example subject");
    subjectB.addAlias("subject_b");
    subjectB.addAttribute("tag", "tagA");
    subjects.add(subjectB);

    Subject subjectC = new Subject();
    subjectC.setName("SubjectC");
    subjectC.setSpecies("Mouse");
    subjectC.setGender("M");
    subjectC.setNotes("This is an example subject");
    subjectC.addAlias("subject_c");
    subjectC.addAttribute("tag", "tagB");
    subjects.add(subjectC);

    Subject subjectD = new Subject();
    subjectD.setName("SubjectD");
    subjectD.setSpecies("Human");
    subjectD.setGender("U");
    subjectD.setNotes("This is an example subject");
    subjectD.addAlias("subject_d");
    subjectD.addAttribute("tag", "tagB");
    subjects.add(subjectD);

    Subject subjectE = new Subject();
    subjectE.setName("SubjectE");
    subjectE.setSpecies("Mouse");
    subjectE.setGender("F");
    subjectE.setNotes("This is an example subject");
    subjectE.addAlias("subject_e");
    subjectE.addAttribute("tag", "tagA");
    subjects.add(subjectE);
    
    subjectRepository.insert(subjects);
    
    // Samples

    List<Sample> samples = new ArrayList<>();

    Sample sampleA = new Sample();
    sampleA.setName("SampleA");
    sampleA.setTissue("Lung");
    sampleA.setHistology("carcinoma");
    sampleA.setSampleType("cell line");
    sampleA.setNotes("This is an example sample.");
    sampleA.addAttribute("tag", "tagA");
    sampleA.setSubject(subjectA);
    sampleA.setDataSet(dataSetA);
    samples.add(sampleA);

    Sample sampleB = new Sample();
    sampleB.setName("SampleB");
    sampleB.setTissue("Liver");
    sampleB.setHistology("carcinoma");
    sampleB.setSampleType("cell line");
    sampleB.setNotes("This is an example sample.");
    sampleB.addAttribute("tag", "tagB");
    sampleB.setSubject(subjectA);
    sampleB.setDataSet(dataSetA);
    samples.add(sampleB);

    Sample sampleC = new Sample();
    sampleC.setName("SampleC");
    sampleC.setTissue("Liver");
    sampleC.setHistology("carcinoma: HCC");
    sampleC.setSampleType("PDX");
    sampleC.setNotes("This is an example sample.");
    sampleC.addAttribute("tag", "tagA");
    sampleC.setSubject(subjectA);
    sampleC.setDataSet(dataSetA);
    samples.add(sampleC);

    Sample sampleD = new Sample();
    sampleD.setName("SampleD");
    sampleD.setTissue("Breast");
    sampleD.setHistology("ductal carcinoma");
    sampleD.setSampleType("cell line");
    sampleD.setNotes("This is an example sample.");
    sampleD.addAttribute("tag", "tagA");
    sampleD.setSubject(subjectB);
    sampleD.setDataSet(dataSetA);
    samples.add(sampleD);

    Sample sampleE = new Sample();
    sampleE.setName("SampleE");
    sampleE.setTissue("Breast");
    sampleE.setHistology("ductal carcinoma");
    sampleE.setSampleType("PDX");
    sampleE.setNotes("This is an example sample.");
    sampleE.addAttribute("tag", "tagB");
    sampleE.setSubject(subjectB);
    sampleE.setDataSet(dataSetA);
    samples.add(sampleE);
    
    sampleRepository.insert(samples);
    
    subjectA.setSamples(Arrays.asList(sampleA, sampleB, sampleC));
    subjectB.setSamples(Arrays.asList(sampleD, sampleE));
    subjectRepository.update(subjectA);
    subjectRepository.update(subjectB);
    
    dataSetA.setSamples(samples);
    dataSetRepository.update(dataSetA);
    
    // Gene Expression

    List<GeneExpression> data = new ArrayList<>();

    GeneExpression geneExpression = new GeneExpression();
    geneExpression.setSample(sampleA);
    geneExpression.setGene(geneA);
    geneExpression.setDataFile(dataFileA);
    geneExpression.setValue(1.23);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSample(sampleA);
    geneExpression.setGene(geneB);
    geneExpression.setDataFile(dataFileA);
    geneExpression.setValue(2.34);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSample(sampleA);
    geneExpression.setGene(geneC);
    geneExpression.setDataFile(dataFileA);
    geneExpression.setValue(4.56);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSample(sampleB);
    geneExpression.setGene(geneA);
    geneExpression.setDataFile(dataFileA);
    geneExpression.setValue(6.78);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSample(sampleB);
    geneExpression.setGene(geneB);
    geneExpression.setDataFile(dataFileA);
    geneExpression.setValue(9.10);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSample(sampleB);
    geneExpression.setGene(geneC);
    geneExpression.setDataFile(dataFileA);
    geneExpression.setValue(12.34);
    data.add(geneExpression);
    
    geneExpressionRepository.insert(data);
    
  }

}