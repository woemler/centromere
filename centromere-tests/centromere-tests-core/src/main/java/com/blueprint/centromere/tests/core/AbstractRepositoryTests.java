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

import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.GeneCopyNumber;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import com.blueprint.centromere.core.model.impl.Mutation;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.model.impl.SegmentCopyNumber;
import com.blueprint.centromere.core.model.impl.TranscriptExpression;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneExpressionRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
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
  @Autowired private DataSourceRepository dataSourceRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;

  @Before
  public void setup() throws Exception {
      
    geneRepository.deleteAll();
    dataSetRepository.deleteAll();
    dataSourceRepository.deleteAll();
    sampleRepository.deleteAll();
    geneExpressionRepository.deleteAll();
    
    // Genes

    List<Gene> genes = new ArrayList<>();

    Gene geneA = new Gene();
    geneA.setGeneId("1");
    geneA.setReferenceId("1");
    geneA.setSymbol("GeneA");
    geneA.setTaxId(9606);
    geneA.setChromosome("1");
    geneA.setDescription("Test Gene A");
    geneA.setGeneType("protein-coding");
    geneA.addAttribute("isKinase","Y");
    geneA.addAlias("ABC");
    genes.add(geneA);

    Gene geneB = new Gene();
    geneB.setGeneId("2");
    geneB.setReferenceId("2");
    geneB.setSymbol("GeneB");
    geneB.setTaxId(9606);
    geneB.setChromosome("5");
    geneB.setDescription("Test Gene B");
    geneB.setGeneType("protein-coding");
    geneB.addAttribute("isKinase", "N");
    geneB.addAlias("DEF");
    genes.add(geneB);

    Gene geneC = new Gene();
    geneC.setGeneId("3");
    geneC.setReferenceId("3");
    geneC.setSymbol("GeneC");
    geneC.setTaxId(9606);
    geneC.setChromosome("9");
    geneC.setDescription("Test Gene C");
    geneC.setGeneType("pseudo");
    geneC.addAttribute("isKinase", "N");
    geneC.addAlias("GHI");
    genes.add(geneC);

    Gene geneD = new Gene();
    geneD.setGeneId("4");
    geneD.setReferenceId("4");
    geneD.setSymbol("GeneD");
    geneD.setTaxId(9606);
    geneD.setChromosome("X");
    geneD.setDescription("Test Gene D");
    geneD.setGeneType("protein-coding");
    geneD.addAttribute("isKinase", "Y");
    geneD.addAlias("JKL");
    genes.add(geneD);

    Gene geneE = new Gene();
    geneE.setGeneId("5");
    geneE.setReferenceId("5");
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

    List<DataSource> dataSources = new ArrayList<>();

    DataSource dataSourceA = new DataSource();
    dataSourceA.setDataSetId(dataSetA.getDataSetId());
    dataSourceA.setSource("/path/to/fileA");
    dataSourceA.setDataType("GCT RNA-Seq gene expression");
    dataSourceA.setDateCreated(new Date());
    dataSourceA.setDateUpdated(new Date());
    dataSourceA.setModel(GeneExpression.class);
    dataSourceA.setDataSourceId("file-a");
    dataSources.add(dataSourceA);

    DataSource dataSourceB = new DataSource();
    dataSourceB.setDataSetId(dataSetA.getDataSetId());
    dataSourceB.setSource("/path/to/fileB");
    dataSourceB.setDataType("GCT RNA-Seq transcript expression");
    dataSourceB.setDateCreated(new Date());
    dataSourceB.setDateUpdated(new Date());
    dataSourceB.setModel(TranscriptExpression.class);
    dataSourceB.setDataSourceId("file-b");
    dataSources.add(dataSourceB);

    DataSource dataSourceC = new DataSource();
    dataSourceC.setDataSetId(dataSetA.getDataSetId());
    dataSourceC.setSource("/path/to/fileC");
    dataSourceC.setDataType("MAF mutations");
    dataSourceC.setDateCreated(new Date());
    dataSourceC.setDateUpdated(new Date());
    dataSourceC.setModel(Mutation.class);
    dataSourceC.setDataSourceId("file-c");
    dataSources.add(dataSourceC);

    DataSource dataSourceD = new DataSource();
    dataSourceD.setDataSetId(dataSetB.getDataSetId());
    dataSourceD.setSource("/path/to/fileD");
    dataSourceD.setDataType("Gene copy number");
    dataSourceD.setDateCreated(new Date());
    dataSourceD.setDateUpdated(new Date());
    dataSourceD.setModel(GeneCopyNumber.class);
    dataSourceD.setDataSourceId("file-d");
    dataSources.add(dataSourceD);

    DataSource dataSourceE = new DataSource();
    dataSourceE.setDataSetId(dataSetE.getDataSetId());
    dataSourceE.setSource("/path/to/fileE");
    dataSourceE.setDataType("Segment copy number");
    dataSourceE.setDateCreated(new Date());
    dataSourceE.setDateUpdated(new Date());
    dataSourceE.setModel(SegmentCopyNumber.class);
    dataSourceE.setDataSourceId("file-e");
    dataSources.add(dataSourceE);
    
    dataSourceRepository.insert(dataSources);
    
    dataSetA.setDataSourceIds(Arrays.asList(dataSourceA.getId(), dataSourceB.getId(), dataSourceC.getId()));
    dataSetB.setDataSourceIds(Arrays.asList(dataSourceD.getId(), dataSourceE.getId()));
    dataSetRepository.update(dataSetA);
    dataSetRepository.update(dataSetB);
    
    // Samples

    List<Sample> samples = new ArrayList<>();

    Sample sampleA = new Sample();
    sampleA.setSampleId("SampleA");
    sampleA.setName("SampleA");
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
    sampleB.setName("SampleB");
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
    sampleC.setName("SampleC");
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
    sampleD.setName("SampleD");
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
    sampleE.setName("SampleE");
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

    GeneExpression geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleA.getSampleId());
    geneExpression.setGeneId(geneA.getGeneId());
    geneExpression.setDataSourceId(dataSourceA.getDataSourceId());
    geneExpression.setDataSetId(dataSourceA.getDataSetId());
    geneExpression.setValue(1.23);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleA.getSampleId());
    geneExpression.setGeneId(geneB.getGeneId());
    geneExpression.setDataSourceId(dataSourceA.getDataSourceId());
    geneExpression.setDataSetId(dataSourceA.getDataSetId());
    geneExpression.setValue(2.34);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleA.getSampleId());
    geneExpression.setGeneId(geneC.getGeneId());
    geneExpression.setDataSourceId(dataSourceA.getDataSourceId());
    geneExpression.setDataSetId(dataSourceA.getDataSetId());
    geneExpression.setValue(4.56);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleB.getSampleId());
    geneExpression.setGeneId(geneA.getGeneId());
    geneExpression.setDataSourceId(dataSourceA.getDataSourceId());
    geneExpression.setDataSetId(dataSourceA.getDataSetId());
    geneExpression.setValue(6.78);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleB.getSampleId());
    geneExpression.setGeneId(geneB.getGeneId());
    geneExpression.setDataSourceId(dataSourceA.getDataSourceId());
    geneExpression.setDataSetId(dataSourceA.getDataSetId());
    geneExpression.setValue(9.10);
    data.add(geneExpression);

    geneExpression = new GeneExpression();
    geneExpression.setSampleId(sampleB.getSampleId());
    geneExpression.setGeneId(geneC.getGeneId());
    geneExpression.setDataSourceId(dataSourceA.getDataSourceId());
    geneExpression.setDataSetId(dataSourceA.getDataSetId());
    geneExpression.setValue(12.34);
    data.add(geneExpression);
    
    geneExpressionRepository.insert(data);
    
  }

}
