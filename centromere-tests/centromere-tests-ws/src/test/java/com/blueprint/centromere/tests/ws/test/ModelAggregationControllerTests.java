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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class ModelAggregationControllerTests extends AbstractRepositoryTests {

  @Autowired private MockMvc mockMvc;

  // Distinct

  @Test
  public void findDistinct() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/distinct/geneType"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void invalidFindDistinct() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/distinct/badField"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void findDistinctFiltered() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/distinct/symbol?geneType=protein-coding"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[2]", is("GeneD")));
  }
  
  @Test
  public void findDistinctWithHal() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/distinct/geneType")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", is("/api/aggregation/gene/distinct/geneType")))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)));
    
  }
  
  // Count
  
  @Test
  public void countResources() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/count"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("count")))
        .andExpect(jsonPath("$.count", is(5)));
  }

  @Test
  public void countResourcesWithFilter() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/count?geneType=protein-coding"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("count")))
        .andExpect(jsonPath("$.count", is(3)));
  }
  
  @Test
  public void countInvalidResource() throws Exception {
    mockMvc.perform(get("/api/aggregation/bad/count"))
        .andExpect(status().isNotFound());
  }
  
  @Test
  public void countWithHal() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/count?geneType=protein-coding")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", is("/api/aggregation/gene/count?geneType=protein-coding")))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasKey("count")))
        .andExpect(jsonPath("$.content.count", is(3)));
  }
  
  // Group
  @Test
  public void groupByField() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/group/geneType"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("protein-coding")))
        .andExpect(jsonPath("$.protein-coding", hasSize(3)))
        .andExpect(jsonPath("$.protein-coding[0]", hasKey("geneType")))
        .andExpect(jsonPath("$.protein-coding[0].geneType", is("protein-coding")))
        .andExpect(jsonPath("$", hasKey("pseudo")))
        .andExpect(jsonPath("$.pseudo", hasSize(2)))
        .andExpect(jsonPath("$.pseudo[0]", hasKey("geneType")))
        .andExpect(jsonPath("$.pseudo[0].geneType", is("pseudo")));
  }

  @Test
  public void groupByFieldWithFiltering() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/group/geneType?geneType=protein-coding"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("protein-coding")))
        .andExpect(jsonPath("$.protein-coding", hasSize(3)))
        .andExpect(jsonPath("$.protein-coding[0]", hasKey("geneType")))
        .andExpect(jsonPath("$.protein-coding[0].geneType", is("protein-coding")))
        .andExpect(jsonPath("$", not(hasKey("pseudo"))));
  }
  
  @Test
  public void groupByInvalidField() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/group/bad"))
        .andExpect(status().isBadRequest());
  }
  
  @Test
  public void groupByWithHal() throws Exception {
    mockMvc.perform(get("/api/aggregation/gene/group/geneType")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", is("/api/aggregation/gene/group/geneType")))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasKey("protein-coding")))
        .andExpect(jsonPath("$.content.protein-coding", hasSize(3)))
        .andExpect(jsonPath("$.content.protein-coding[0]", hasKey("geneType")))
        .andExpect(jsonPath("$.content.protein-coding[0].geneType", is("protein-coding")))
        .andExpect(jsonPath("$.content", hasKey("pseudo")))
        .andExpect(jsonPath("$.content.pseudo", hasSize(2)))
        .andExpect(jsonPath("$.content.pseudo[0]", hasKey("geneType")))
        .andExpect(jsonPath("$.content.pseudo[0].geneType", is("pseudo")));
  }
  
}
