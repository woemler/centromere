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

package com.blueprint.centromere.tests.ws.test;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.models.DataFile;
import com.blueprint.centromere.tests.core.models.DataSet;
import com.blueprint.centromere.tests.core.models.Gene;
import com.blueprint.centromere.tests.core.models.Sample;
import com.blueprint.centromere.tests.core.repositories.DataFileRepository;
import com.blueprint.centromere.tests.core.repositories.DataSetRepository;
import com.blueprint.centromere.tests.core.repositories.GeneRepository;
import com.blueprint.centromere.tests.core.repositories.SampleRepository;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class ModelSearchControllerTests extends AbstractRepositoryTests {

  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private MockMvc mockMvc;

  // Distinct

  @Test
  public void findDistinct() throws Exception {
    mockMvc.perform(get("/api/gene/search/distinct/geneType"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void invalidFindDistinct() throws Exception {
    mockMvc.perform(get("/api/gene/search/distinct/badField"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void findDistinctFiltered() throws Exception {
    mockMvc.perform(get("/api/gene/search/distinct/symbol?geneType=protein-coding"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[2]", is("GeneD")));
  }
  
  @Test
  public void guessTest() throws Exception {
    mockMvc.perform(get("/api/gene/search/guess?keyword=DEF"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneB")));
  }
  
  @Test
  public void invalidGuessTest() throws Exception {
    mockMvc.perform(get("/api/geneexpression/search/guess?keyword=DEF"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void findDataByGeneMetadata() throws Exception {

    Gene gene = (Gene) geneRepository.findBySymbol("GeneB").get(0);
    
    mockMvc.perform(get("/api/geneexpression/search/gene?symbol=GeneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0].id", is(gene.getId())));
  }

  @Test
  public void findDataBySampleMetadata() throws Exception {

    Sample sample = (Sample) sampleRepository.findByName("SampleA").get(0);

    mockMvc.perform(get("/api/geneexpression/search/sample?name=SampleA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0].id", is(sample.getId())));
  }

  @Test
  public void findDataByDataSourceMetadata() throws Exception {

    DataFile dataSource = (DataFile) dataFileRepository.findByDataType("GCT RNA-Seq gene expression").get(0);

    mockMvc.perform(get("/api/geneexpression/search/datafile?dataType=GCT RNA-Seq gene expression"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(6)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0].id", is(dataSource.getId())));
  }

  @Test
  public void findDataByDataSetMetadata() throws Exception {

    DataSet dataSet = (DataSet) dataSetRepository.findByName("DataSetA").orElse(null);
    Assert.notNull(dataSet);

    mockMvc.perform(get("/api/geneexpression/search/dataset?name=DataSetA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(6)))
        .andExpect(jsonPath("$[0]", hasKey("id")))
        .andExpect(jsonPath("$[0].id", is(dataSet.getId())));
  }
  
  @Test
  public void findInvalidDataByMetadata() throws Exception {
    mockMvc.perform(get("/api/gene/search/dataset?name=DataSetA"))
        .andExpect(status().isBadRequest());
  }
  
}
