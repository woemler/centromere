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

import com.blueprint.centromere.tests.core.models.DataFile;
import com.blueprint.centromere.tests.core.models.DataSet;
import com.blueprint.centromere.tests.core.models.Gene;
import com.blueprint.centromere.tests.core.models.GeneExpression;
import com.blueprint.centromere.tests.core.models.Sample;
import com.blueprint.centromere.tests.core.models.User;
import com.blueprint.centromere.tests.core.repositories.DataFileRepository;
import com.blueprint.centromere.tests.core.repositories.DataSetRepository;
import com.blueprint.centromere.tests.core.repositories.GeneExpressionRepository;
import com.blueprint.centromere.tests.core.repositories.GeneRepository;
import com.blueprint.centromere.tests.core.repositories.SampleRepository;
import com.blueprint.centromere.tests.core.repositories.UserRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author woemler
 */
public abstract class AbstractRepositoryTests {

    @Autowired 
    private GeneRepository geneRepository;
    
    @Autowired 
    private DataSetRepository dataSetRepository;
    
    @Autowired 
    private DataFileRepository dataFileRepository;
    
    @Autowired 
    private SampleRepository sampleRepository;
    
    @Autowired 
    private GeneExpressionRepository geneExpressionRepository;
    
    @Autowired 
    private UserRepository userRepository;

    @Before
    public void setup() throws Exception {

        geneRepository.deleteAll();
        dataSetRepository.deleteAll();
        dataFileRepository.deleteAll();
        sampleRepository.deleteAll();
        geneExpressionRepository.deleteAll();
        userRepository.deleteAll();

        // Genes

        List<Gene> genes = new ArrayList<>();

        Gene geneA = (Gene) geneRepository.getModel().newInstance();
        geneA.setEntrezGeneId(1);
        geneA.setSymbol("GeneA");
        geneA.setTaxId(9606);
        geneA.setChromosome("1");
        geneA.setDescription("Test Gene A");
        geneA.setGeneType("protein-coding");
        geneA.addAttribute("isKinase", "Y");
        geneA.addAlias("ABC");
        genes.add(geneA);

        Gene geneB = (Gene) geneRepository.getModel().newInstance();
        geneB.setEntrezGeneId(2);
        geneB.setSymbol("GeneB");
        geneB.setTaxId(9606);
        geneB.setChromosome("5");
        geneB.setDescription("Test Gene B");
        geneB.setGeneType("protein-coding");
        geneB.addAttribute("isKinase", "N");
        geneB.addAlias("DEF");
        genes.add(geneB);

        Gene geneC = (Gene) geneRepository.getModel().newInstance();
        geneC.setEntrezGeneId(3);
        geneC.setSymbol("GeneC");
        geneC.setTaxId(9606);
        geneC.setChromosome("9");
        geneC.setDescription("Test Gene C");
        geneC.setGeneType("pseudo");
        geneC.addAttribute("isKinase", "N");
        geneC.addAlias("GHI");
        genes.add(geneC);

        Gene geneD = (Gene) geneRepository.getModel().newInstance();
        geneD.setEntrezGeneId(4);
        geneD.setSymbol("GeneD");
        geneD.setTaxId(9606);
        geneD.setChromosome("X");
        geneD.setDescription("Test Gene D");
        geneD.setGeneType("protein-coding");
        geneD.addAttribute("isKinase", "Y");
        geneD.addAlias("JKL");
        genes.add(geneD);

        Gene geneE = (Gene) geneRepository.getModel().newInstance();
        geneE.setEntrezGeneId(5);
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

        DataSet dataSetA = (DataSet) dataSetRepository.getModel().newInstance();
        dataSetA.setName("DataSetA");
        dataSetA.setSource("Internal");
        dataSetA.setVersion("1.0");
        dataSetA.setDescription("This is an example data set.");
        dataSets.add(dataSetA);

        DataSet dataSetB = (DataSet) dataSetRepository.getModel().newInstance();
        dataSetB.setName("DataSetB");
        dataSetB.setSource("External");
        dataSetB.setVersion("1.0");
        dataSetB.setDescription("This is an example data set.");
        dataSets.add(dataSetB);

        DataSet dataSetC = (DataSet) dataSetRepository.getModel().newInstance();
        dataSetC.setName("DataSetC");
        dataSetC.setSource("Internal");
        dataSetC.setVersion("2.0");
        dataSetC.setDescription("This is an example data set.");
        dataSets.add(dataSetC);

        DataSet dataSetD = (DataSet) dataSetRepository.getModel().newInstance();
        dataSetD.setName("DataSetD");
        dataSetD.setSource("External");
        dataSetD.setVersion("1.0");
        dataSetD.setDescription("This is an example data set.");
        dataSets.add(dataSetD);

        DataSet dataSetE = (DataSet) dataSetRepository.getModel().newInstance();
        dataSetE.setName("DataSetE");
        dataSetE.setSource("Internal");
        dataSetE.setVersion("1.0");
        dataSetE.setDescription("This is an example data set.");
        dataSets.add(dataSetE);

        dataSetRepository.insert(dataSets);

        // Data Files

        List<DataFile> dataFiles = new ArrayList<>();

        DataFile dataFileA = (DataFile) dataFileRepository.getModel().newInstance();
        dataFileA.setDataSetId(dataSetA.getId());
        dataFileA.setFilePath("/path/to/fileA");
        dataFileA.setDataType("GCT RNA-Seq gene expression");
        dataFileA.setDateCreated(new Date());
        dataFileA.setDateUpdated(new Date());
        dataFileA.setModel(GeneExpression.class);
        dataFiles.add(dataFileA);

        DataFile dataFileB = (DataFile) dataFileRepository.getModel().newInstance();
        dataFileB.setDataSetId(dataSetA.getId());
        dataFileB.setFilePath("/path/to/fileB");
        dataFileB.setDataType("GCT RNA-Seq gene expression");
        dataFileB.setDateCreated(new Date());
        dataFileB.setDateUpdated(new Date());
        dataFileB.setModel(GeneExpression.class);
        dataFiles.add(dataFileB);

        DataFile dataFileC = (DataFile) dataFileRepository.getModel().newInstance();
        dataFileC.setDataSetId(dataSetA.getId());
        dataFileC.setFilePath("/path/to/fileC");
        dataFileC.setDataType("MAF mutations");
        dataFileC.setDateCreated(new Date());
        dataFileC.setDateUpdated(new Date());
        dataFileC.setModel(GeneExpression.class);
        dataFiles.add(dataFileC);

        DataFile dataFileD = (DataFile) dataFileRepository.getModel().newInstance();
        dataFileD.setDataSetId(dataSetB.getId());
        dataFileD.setFilePath("/path/to/fileD");
        dataFileD.setDataType("Gene copy number");
        dataFileD.setDateCreated(new Date());
        dataFileD.setDateUpdated(new Date());
        dataFileD.setModel(GeneExpression.class);
        dataFiles.add(dataFileD);

        DataFile dataFileE = (DataFile) dataFileRepository.getModel().newInstance();
        dataFileE.setDataSetId(dataSetE.getId());
        dataFileE.setFilePath("/path/to/fileE");
        dataFileE.setDataType("Segment copy number");
        dataFileE.setDateCreated(new Date());
        dataFileE.setDateUpdated(new Date());
        dataFileE.setModel(GeneExpression.class);
        dataFiles.add(dataFileE);

        dataFileRepository.insert(dataFiles);

        dataSetA.setDataFileIds(Arrays.asList(dataFileA.getId(), dataFileB.getId(), dataFileC.getId()));
        dataSetB.setDataFileIds(Arrays.asList(dataFileD.getId(), dataFileE.getId()));
        dataSetRepository.update(dataSetA);
        dataSetRepository.update(dataSetB);

        // Samples

        List<Sample> samples = new ArrayList<>();

        Sample sampleA = (Sample) sampleRepository.getModel().newInstance();
        sampleA.setName("SampleA");
        sampleA.setTissue("Lung");
        sampleA.setHistology("carcinoma");
        sampleA.setSampleType("cell line");
        sampleA.setNotes("This is an example sample.");
        sampleA.addAttribute("tag", "tagA");
        sampleA.setSpecies("H sapiens");
        sampleA.setGender("M");
        samples.add(sampleA);

        Sample sampleB = (Sample) sampleRepository.getModel().newInstance();
        sampleB.setName("SampleB");
        sampleB.setTissue("Liver");
        sampleB.setHistology("carcinoma");
        sampleB.setSampleType("cell line");
        sampleB.setNotes("This is an example sample.");
        sampleB.addAttribute("tag", "tagB");
        sampleB.setSpecies("H sapiens");
        sampleB.setGender("F");
        samples.add(sampleB);

        Sample sampleC = (Sample) sampleRepository.getModel().newInstance();
        sampleC.setName("SampleC");
        sampleC.setTissue("Liver");
        sampleC.setHistology("carcinoma: HCC");
        sampleC.setSampleType("PDX");
        sampleC.setNotes("This is an example sample.");
        sampleC.addAttribute("tag", "tagA");
        sampleC.setSpecies("H sapiens");
        sampleC.setGender("F");
        samples.add(sampleC);

        Sample sampleD = (Sample) sampleRepository.getModel().newInstance();
        sampleD.setName("SampleD");
        sampleD.setTissue("Breast");
        sampleD.setHistology("ductal carcinoma");
        sampleD.setSampleType("cell line");
        sampleD.setNotes("This is an example sample.");
        sampleD.addAttribute("tag", "tagA");
        sampleD.setSpecies("H sapiens");
        sampleD.setGender("U");
        samples.add(sampleD);

        Sample sampleE = (Sample) sampleRepository.getModel().newInstance();
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

        List<Serializable> sampleIds = new ArrayList<>();
        for (Sample sample: samples) {
            sampleIds.add(sample.getId());
        }

        dataSetA.setSampleIds(sampleIds);
        dataSetRepository.update(dataSetA);

        // Gene Expression

        List<GeneExpression> data = new ArrayList<>();

        GeneExpression geneExpression = (GeneExpression) geneExpressionRepository.getModel().newInstance();
        geneExpression.setSampleId(sampleA.getId());
        geneExpression.setGeneId(geneA.getId());
        geneExpression.setDataFileId(dataFileA.getId());
        geneExpression.setDataSetId(dataFileA.getDataSetId());
        geneExpression.setValue(1.23);
        data.add(geneExpression);

        geneExpression = (GeneExpression) geneExpressionRepository.getModel().newInstance();
        geneExpression.setSampleId(sampleA.getId());
        geneExpression.setGeneId(geneB.getId());
        geneExpression.setDataFileId(dataFileA.getId());
        geneExpression.setDataSetId(dataFileA.getDataSetId());
        geneExpression.setValue(2.34);
        data.add(geneExpression);

        geneExpression = (GeneExpression) geneExpressionRepository.getModel().newInstance();
        geneExpression.setSampleId(sampleA.getId());
        geneExpression.setGeneId(geneC.getId());
        geneExpression.setDataFileId(dataFileA.getId());
        geneExpression.setDataSetId(dataFileA.getDataSetId());
        geneExpression.setValue(4.56);
        data.add(geneExpression);

        geneExpression = (GeneExpression) geneExpressionRepository.getModel().newInstance();
        geneExpression.setSampleId(sampleB.getId());
        geneExpression.setGeneId(geneA.getId());
        geneExpression.setDataFileId(dataFileA.getId());
        geneExpression.setDataSetId(dataFileA.getDataSetId());
        geneExpression.setValue(6.78);
        data.add(geneExpression);

        geneExpression = (GeneExpression) geneExpressionRepository.getModel().newInstance();
        geneExpression.setSampleId(sampleB.getId());
        geneExpression.setGeneId(geneB.getId());
        geneExpression.setDataFileId(dataFileA.getId());
        geneExpression.setDataSetId(dataFileA.getDataSetId());
        geneExpression.setValue(9.10);
        data.add(geneExpression);

        geneExpression = (GeneExpression) geneExpressionRepository.getModel().newInstance();
        geneExpression.setSampleId(sampleB.getId());
        geneExpression.setGeneId(geneC.getId());
        geneExpression.setDataFileId(dataFileA.getId());
        geneExpression.setDataSetId(dataFileA.getDataSetId());
        geneExpression.setValue(12.34);
        data.add(geneExpression);

        geneExpressionRepository.insert(data);

        // Users

        User user = (User) userRepository.getModel().newInstance();
        user.setUsername("user");
        user.setPassword(new BCryptPasswordEncoder().encode("password"));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        userRepository.insert(user);

    }

}
