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

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = { Profiles.SCHEMA_DEFAULT, Profiles.WEB_PROFILE, Profiles.NO_SECURITY, Profiles.API_DOCUMENTATION_DISABLED_PROFILE })
@AutoConfigureMockMvc(secure = false)
public class ModelSearchControllerTests extends AbstractRepositoryTests {

  private static final String BASE_URL = "/api/gene/search";
  private static final String DATA_URL = "/api/geneexpression/search";

  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataSourceRepository dataSourceRepository;
  @Autowired private MockMvc mockMvc;

  // Distinct

  @Test
  public void findDistinct() throws Exception {
    mockMvc.perform(get(BASE_URL + "/distinct/geneType"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void invalidFindDistinct() throws Exception {
    mockMvc.perform(get(BASE_URL + "/distinct/badField"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void findDistinctFiltered() throws Exception {
    mockMvc.perform(get(BASE_URL + "/distinct/symbol?geneType=protein-coding"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[2]", is("GeneD")));
  }
  
  @Test
  public void guessTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/guess?keyword=DEF"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneB")));
  }
  
  @Test
  public void invalidGuessTest() throws Exception {
    mockMvc.perform(get(DATA_URL + "/guess?keyword=DEF"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void findDataByGeneMetadata() throws Exception {

    Gene gene = (Gene) geneRepository.findBySymbol("GeneB").get(0);
    
    mockMvc.perform(get(DATA_URL + "/gene?symbol=GeneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("geneId")))
        .andExpect(jsonPath("$[0].geneId", is(gene.getId())));
  }

  @Test
  public void findDataBySampleMetadata() throws Exception {

    Sample sample = (Sample) sampleRepository.findBySampleId("SampleA").get();

    mockMvc.perform(get(DATA_URL + "/sample?sampleId=SampleA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("sampleId")))
        .andExpect(jsonPath("$[0].sampleId", is(sample.getId())));
  }

  @Test
  public void findDataByDataSourceMetadata() throws Exception {

    DataSource dataSource = dataSourceRepository.findByDataType("GCT RNA-Seq gene expression").get(0);

    mockMvc.perform(get(DATA_URL + "/datasource?dataType=GCT RNA-Seq gene expression"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(6)))
        .andExpect(jsonPath("$[0]", hasKey("dataSourceId")))
        .andExpect(jsonPath("$[0].dataSourceId", is(dataSource.getId())));
  }

  @Test
  public void findDataByDataSetMetadata() throws Exception {

    DataSet dataSet = dataSetRepository.findByDataSetId("DataSetA").orElse(null);
    Assert.notNull(dataSet);

    mockMvc.perform(get(DATA_URL + "/dataset?dataSetId=DataSetA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(6)))
        .andExpect(jsonPath("$[0]", hasKey("dataSetId")))
        .andExpect(jsonPath("$[0].dataSetId", is(dataSet.getId())));
  }
  
  @Test
  public void findInvalidDataByMetadata() throws Exception {
    mockMvc.perform(get(BASE_URL + "/dataset?shortName=DataSetA"))
        .andExpect(status().isBadRequest());
  }
  
}
