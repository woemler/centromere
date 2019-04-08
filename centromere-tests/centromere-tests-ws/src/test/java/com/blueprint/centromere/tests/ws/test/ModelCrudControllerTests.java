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

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.models.DataSet;
import com.blueprint.centromere.tests.core.models.Gene;
import com.blueprint.centromere.tests.core.models.GeneExpression;
import com.blueprint.centromere.tests.core.repositories.DataSetRepository;
import com.blueprint.centromere.tests.core.repositories.GeneExpressionRepository;
import com.blueprint.centromere.tests.core.repositories.GeneRepository;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.config.ModelResourceRegistry;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class ModelCrudControllerTests extends AbstractRepositoryTests {

  @Autowired private GeneRepository geneRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private MockMvc mockMvc;
  @Autowired private ModelResourceRegistry registry;

  @Test
  public void headTest() throws Exception {
    mockMvc.perform(head("/api/search/gene"))
        .andExpect(status().isOk());
  }
  
  // Find

  @Test
  public void findByIdWithHal() throws Exception {
    
    System.out.println("Gene URI: " + registry.getUriByModel(geneRepository.getModel()));
    System.out.println(String.format("Registered models: %s", registry.getRegisteredModels()));

    Gene gene = (Gene) geneRepository.findByEntrezGeneId(1).get();

    mockMvc.perform(get("/api/search/gene/{id}", gene.getId())
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entrezGeneId", is(1)))
        .andExpect(jsonPath("$.symbol", is("GeneA")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0].href", endsWith("/api/search/gene/" + gene.getId())));
  }

  @Test
  public void findByIdNoHal() throws Exception {

    Gene gene = (Gene) geneRepository.findByEntrezGeneId(1).get();

    mockMvc.perform(get("/api/search/gene/{id}", gene.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entrezGeneId", is(1)))
        .andExpect(jsonPath("$.symbol", is("GeneA")))
        .andExpect(jsonPath("$", not(hasKey("links"))));
  }

  @Test
  public void findByIdNotFound() throws Exception{
    mockMvc.perform(get("/api/search/gene/{id}", "abcd"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void findByIdFiltered() throws Exception {

    Gene gene = (Gene) geneRepository.findByEntrezGeneId(1).get();

    mockMvc.perform(get("/api/search/gene/{id}?exclude=links,symbol", gene.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entrezGeneId", is(1)))
        .andExpect(jsonPath("$", not(hasKey("symbol"))))
        .andExpect(jsonPath("$", not(hasKey("links"))));
  }

  @Test
  public void findAllWithHal() throws Exception {
    mockMvc.perform(get("/api/search/gene").accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.content[0].entrezGeneId", is(1)))
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0].href", endsWith("/api/search/gene")))
        .andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
  }

  @Test
  public void findAllWithoutHal() throws Exception {
    mockMvc.perform(get("/api/search/gene"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$[0].entrezGeneId", is(1)))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))))
        .andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
  }
  
  @Test
  public void findAllWithHalAndLinkedRefs() throws Exception {
    
    MvcResult result = mockMvc.perform(get("/api/search/geneexpression")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andReturn();
    
    DocumentContext document = JsonPath.parse(result.getResponse().getContentAsString());


    mockMvc.perform(get("/api/search/geneexpression")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", is("/api/search/geneexpression")))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(6)))
        .andExpect(jsonPath("$.content[0]", hasKey("geneId")))
        .andExpect(jsonPath("$.content[0]", hasKey("id")))
        .andExpect(jsonPath("$.content[0]", hasKey("links")))
        .andExpect(jsonPath("$.content[0].links", hasSize(5)))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.content[0].links[0].rel", is("self")))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("href")))
        .andExpect(jsonPath("$.content[0].links[0].href", is("/api/search/geneexpression/" + document.read("$.content[0].id").toString())))
        .andExpect(jsonPath("$.content[0].links[4]", hasKey("rel")))
        .andExpect(jsonPath("$.content[0].links[4].rel", is("gene")))
        .andExpect(jsonPath("$.content[0].links[4]", hasKey("href")))
        .andExpect(jsonPath("$.content[0].links[4].href", is("/api/search/gene/" + document.read("$.content[0].geneId").toString())));
    
    String geneUrl = document.read("$.content[0].links[4].href");
    
    mockMvc.perform(get(geneUrl))
        .andExpect(status().isOk());
    
  }

  @Test
  public void findFieldExcludedWithHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?exclude=links,symbol").accept(
        ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.content[0].entrezGeneId", is(1)))
        .andExpect(jsonPath("$.content[0]", not(hasKey("symbol"))))
        .andExpect(jsonPath("$.content[0]", not(hasKey("links"))));
  }

  @Test
  public void findFieldFilteredWithHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?fields=links,symbol").accept(
        ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0]", not(hasKey("geneId"))))
        .andExpect(jsonPath("$.content[0]", hasKey("symbol")))
        .andExpect(jsonPath("$.content[0]", hasKey("links")));
  }

  @Test
  public void findBySimpleParamWithHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?geneType=pseudo").accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.content[0].entrezGeneId", is(3)))
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0].href", endsWith("/api/search/gene?geneType=pseudo")))
        .andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
  }

  @Test
  public void findByCollectionItemNoHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?aliases=MNO").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$[0].entrezGeneId", is(5)))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findByMapItemNoHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?attributes.isKinase=Y").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$[0].entrezGeneId", is(1)))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findPagedWithHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?page=1&size=3").accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.content[0].entrezGeneId", is(4)))
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(4)))
        .andExpect(jsonPath("$.links[0].rel", is("first")))
        .andExpect(jsonPath("$", hasKey("page")))
        .andExpect(jsonPath("$.page.totalElements", is(5)))
        .andExpect(jsonPath("$.page.number", is(1)))
        .andExpect(jsonPath("$.page.size", is(3)))
        .andExpect(jsonPath("$.page.totalPages", is(2)));
  }

  @Test
  public void findPagedWithoutHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?page=1&size=3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.content[0].entrezGeneId", is(4)))
        .andExpect(jsonPath("$.content[0]", not(hasKey("links"))))
        .andExpect(jsonPath("$", not(hasKey("links"))));
  }

  @Test
  public void findSortedWithHal() throws Exception {
    mockMvc.perform(get("/api/search/gene?sort=symbol,desc").accept(
        ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0]", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.content[0].entrezGeneId", is(5)));
  }
  
  // Dynamic Find Params
  
  @Test
  public void findByStringLike() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolLike=eneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneB")));
  }

  @Test
  public void invalidFindByStringLike() throws Exception {
    mockMvc.perform(get("/api/search/gene?taxIdLike=06"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void findByStringStartsWith() throws Exception {
    mockMvc.perform(get("/api/search/gene?geneTypeStartsWith=protein"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneA")));
  }

  @Test
  public void invalidFindByStringStartsWith() throws Exception {
    mockMvc.perform(get("/api/search/gene?taxIdStartsWith=96"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void findByStringEndsWith() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolEndsWith=eneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneB")));
  }

  @Test
  public void invalidFindByStringEndsWith() throws Exception {
    mockMvc.perform(get("/api/search/gene?taxIdEndsWith=06"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void findByStringIn() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolIn=GeneA,GeneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneA")));
  }

  @Test
  public void findByNumberIn() throws Exception {
    mockMvc.perform(get("/api/search/gene?taxIdIn=9606,1000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneA")));
  }

  @Test
  public void findByStringNotIn() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolNotIn=GeneA,GeneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneC")));
  }

  @Test
  public void findByNumberNotIn() throws Exception {
    mockMvc.perform(get("/api/search/gene?taxIdNotIn=9606,1000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  public void findByNumberNotInTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueNotIn=2.34,4.56"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(1.23)));
  }
  
  @Test
  public void findByNumberGreaterThanTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueGreaterThan=5.0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(6.78)));
  }

  @Test
  public void findByNumberGreaterThanOrEqualsTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueGreaterThanOrEquals=9.1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(9.1)));
  }

  @Test
  public void findByNumberLessThanTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueLessThan=5.0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(1.23)));
  }

  @Test
  public void findByNumberLessThanOrEqualsTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueLessThanOrEquals=2.34"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(1.23)));
  }

  @Test
  public void invalidGreaterThanNumberTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolGreaterThan=100"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void invalidGreaterThanOrEqualsNumberTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolGreaterThanOrEquals=100"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void invalidLessThanNumberTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolLessThan=100"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void invalidLessThanOrEqualsNumberTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolLessThanOrEquals=100"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }
  
  @Test
  public void findByNumberBetweenTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueBetween=3.0,7.0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(4.56)));
  }

  @Test
  public void findByNumberOutsideTest() throws Exception {
    mockMvc.perform(get("/api/search/geneexpression?valueOutside=3.0,7.0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(1.23)));
  }

  @Test
  public void invalidOutsideNumberTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolOutside=100,10000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }

  @Test
  public void invalidBetweenNumberTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolBetween=100,10000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is(400)));
  }
  
  @Test
  public void isNullTest() throws Exception {
    
    Gene gene = ((List<Gene>) geneRepository.findAll()).get(0);
    gene.setGeneType(null);
    Serializable id = gene.getId();
    geneRepository.update(gene);
    
    mockMvc.perform(get("/api/search/gene?geneTypeIsNull"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("geneType")))
        .andExpect(jsonPath("$[0].geneType", nullValue()))
        .andExpect(jsonPath("$[0].id", is(id)));
  }

  @Test
  public void isNotNullTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?symbolIsNotNull"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", not(nullValue())));
  }
  
  @Test
  public void isTrueTest() throws Exception {
    mockMvc.perform(get("/api/search/user?enabledIsTrue"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("enabled")))
        .andExpect(jsonPath("$[0].enabled", is(true)));
  }

  @Test
  public void isFalseTest() throws Exception {
    mockMvc.perform(get("/api/search/user?enabledIsFalse"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
  
  // Ignored options
  
  @Test
  public void ignoredParameterTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?chromosomeLocation=xxx"))
        .andExpect(status().isBadRequest());
    mockMvc.perform(get("/api/search/gene"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("chromosomeLocation")));
  }
  
  // Create

  @Test
  public void createTest() throws Exception {

    Gene gene = (Gene) geneRepository.getModel().newInstance();
    gene.setEntrezGeneId(6);
    gene.setSymbol("GeneF");
    gene.setTaxId(9606);
    gene.setChromosome("10");
    gene.setGeneType("protein-coding");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("fieldFilter",
        SimpleBeanPropertyFilter.serializeAllExcept()).setFailOnUnknownId(false));
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mockMvc.perform(post("/api/search/gene")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(gene)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("entrezGeneId")))
        .andExpect(jsonPath("$.entrezGeneId", is(6)));

    Gene newGene = (Gene) geneRepository.findByEntrezGeneId(6).get();

    mockMvc.perform(get("/api/search/gene/{id}", newGene.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entrezGeneId", is(6)));

    geneRepository.delete(newGene);

  }
  
  // Update

  @Test
  public void updateTest() throws Exception {

    mockMvc.perform(get("/api/search/gene?symbol=GeneA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is("GeneA")));

    List<Gene> genes = geneRepository.findBySymbol("GeneA");
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Gene gene = genes.get(0);
    Assert.isTrue("GeneA".equals(gene.getSymbol()));
    gene.setSymbol("GeneX");
    Assert.notNull(gene.getId());

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("fieldFilter",
        SimpleBeanPropertyFilter.serializeAllExcept()).setFailOnUnknownId(false));
    //objectMapper.setSerializationInclusion(Include.NON_EMPTY);

    System.out.println(gene.toString());
    System.out.println(objectMapper.writeValueAsString(gene));

    mockMvc.perform(put("/api/search/gene/{id}", gene.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(gene)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("symbol")))
        .andExpect(jsonPath("$.symbol", is("GeneX")));

    mockMvc.perform(get("/api/search/gene/{id}", gene.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.symbol", is("GeneX")));

  }
  
  // Delete

  @Test
  public void deleteTest() throws Exception {

    Gene gene = (Gene) geneRepository.getModel().newInstance();
    gene.setEntrezGeneId(7);
    gene.setSymbol("GeneG");
    gene.setTaxId(9606);
    gene.setChromosome("10");
    gene.setGeneType("protein-coding");
    geneRepository.insert(gene);

    mockMvc.perform(delete("/api/search/gene/{id}", gene.getId()))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/search/gene/{id}", gene.getId()))
        .andExpect(status().isNotFound());

  }
  
  // Linked resources
  
  @Test
  public void oneToManyLinkedTest() throws Exception {

    DataSet dataSet = ((List<DataSet>) dataSetRepository.findAll()).get(0);
    Assert.notNull(dataSet);
    Assert.notNull(dataSet.getSampleIds());
    Assert.isTrue(!dataSet.getSampleIds().isEmpty());
    System.out.println(dataSet.getSampleIds().toString());
    
    mockMvc.perform(get("/api/search/dataset/" + dataSet.getId() + "/samples"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(hasKey("links"))))
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("sampleType")));
    
  }

  @Test
  public void oneToManyLinkedTestWithHal() throws Exception {

    DataSet dataSet = ((List<DataSet>) dataSetRepository.findAll()).get(0);
    Assert.notNull(dataSet);
    Assert.notNull(dataSet.getSampleIds());
    Assert.isTrue(!dataSet.getSampleIds().isEmpty());
    System.out.println(dataSet.getSampleIds().toString());

    mockMvc.perform(get("/api/search/dataset/" + dataSet.getId() + "/samples")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", is("/api/search/dataset/" + dataSet.getId() + "/samples")))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0]", hasKey("sampleType")))
        .andExpect(jsonPath("$.content[0]", hasKey("links")))
        .andExpect(jsonPath("$.content[0].links", hasSize(1)))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.content[0].links[0].rel", is("self")))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("href")))
        .andExpect(jsonPath("$.content[0].links[0].href", startsWith("/api/search/sample/")));

  }
  
  @Test
  public void manyToOneLinkedTest() throws Exception {

    GeneExpression geneExpression = ((List<GeneExpression>) geneExpressionRepository.findAll()).get(0);
    Assert.notNull(geneExpression);
    
    Optional<Gene> geneOptional = geneRepository.findById(geneExpression.getGeneId());
    Assert.isTrue(geneOptional.isPresent());
    Gene gene = geneOptional.get();
    
    mockMvc.perform(get("/api/search/geneexpression/" + geneExpression.getId() + "/gene"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(hasKey("links"))))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("symbol")))
        .andExpect(jsonPath("$[0].symbol", is(gene.getSymbol())));
    
  }

  @Test
  public void manyToOneLinkedTestWithHal() throws Exception {

    GeneExpression geneExpression = ((List<GeneExpression>) geneExpressionRepository.findAll()).get(0);
    Assert.notNull(geneExpression);

    Optional<Gene> geneOptional = geneRepository.findById(geneExpression.getGeneId());
    Assert.isTrue(geneOptional.isPresent());
    Gene gene = geneOptional.get();

    mockMvc.perform(get("/api/search/geneexpression/" + geneExpression.getId() + "/gene")
        .accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", is("/api/search/geneexpression/" + geneExpression.getId() + "/gene")))
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0]", hasKey("symbol")))
        .andExpect(jsonPath("$.content[0].symbol", is(gene.getSymbol())))
        .andExpect(jsonPath("$.content[0]", hasKey("links")))
        .andExpect(jsonPath("$.content[0].links", hasSize(1)))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.content[0].links[0].rel", is("self")))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("href")))
        .andExpect(jsonPath("$.content[0].links[0].href", is("/api/search/gene/" + gene.getId())));

  }
  
  // Exceptions

  @Test
  public void resourceNotFoundExceptionTest() throws Exception {
    mockMvc.perform(get("/api/search/gene/{id}", "abc123"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is("NOT_FOUND")))
        .andExpect(jsonPath("$.code", is(404)));
  }

  @Test
  public void invalidParameterExceptionTest() throws Exception {
    mockMvc.perform(get("/api/search/gene/{id}?bad=param", 1L))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
        .andExpect(jsonPath("$.code", is(400)));
    mockMvc.perform(get("/api/search/gene?bad=param"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
        .andExpect(jsonPath("$.code", is(400)))
        .andExpect(jsonPath("$", hasKey("message")));
  }

  @Test
  public void parameterMappingExceptionTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?taxId=bad"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
        .andExpect(jsonPath("$.code", is(400)))
        .andExpect(jsonPath("$", hasKey("message")));
  }
  
  
  // Compression
  
  // TODO: How to get this to pass? Do we need more data to get the compression to trigger?
//  @Test
//  public void defaultCompressionTest() throws Exception {
//
//    Assert.isTrue(environment.getProperty("server.compression.enabled").equals("true"));
//
//    mockMvc.perform(get(BASE_URL + "?size=1000").header("Accept-Encoding", "gzip, deflate"))
//        .andExpect(status().isOk())
//        .andDo(MockMvcResultHandlers.print())
//        .andExpect(MockMvcResultMatchers.header().string("Content-Encoding", "gzip"));
//  }
  
  // CORS
  
  @Test
  public void corsTest() throws Exception {
    mockMvc.perform(get("/api/search/gene?size=10").header("Origin", "http://www.someurl.com"))
        .andExpect(status().isOk());
  }

}
