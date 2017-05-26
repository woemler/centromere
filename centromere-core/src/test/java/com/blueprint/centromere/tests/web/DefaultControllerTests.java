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

package com.blueprint.centromere.tests.web;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.web.config.ApiMediaTypes;
import com.blueprint.centromere.tests.common.AbstractRepositoryTests;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = { Profiles.WEB_PROFILE, Profiles.NO_SECURITY, Profiles.API_DOCUMENTATION_DISABLED_PROFILE })
@AutoConfigureMockMvc
public class DefaultControllerTests extends AbstractRepositoryTests {

  private static final String BASE_URL = "/api/genes";
  private static final String EXPRESSION_URL = "/api/geneexpression";

  @Autowired private SampleRepository sampleRepository;
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired @Qualifier("objectMapper") private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$[0]", Matchers.not(hasKey("links"))));
  }

  @Test
  public void findAllWithHalTest() throws Exception {
    mockMvc.perform(get(BASE_URL).accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$.content[0]", hasKey("links")))
        .andExpect(jsonPath("$.content[0].links", hasSize(1)))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.content[0].links[0].rel", is("self")))
        .andExpect(jsonPath("$.content[0].links[0]", hasKey("href")))
        .andExpect(jsonPath("$", hasKey("links")))
        .andExpect(jsonPath("$.links", hasSize(1)))
        .andExpect(jsonPath("$.links[0]", hasKey("rel")))
        .andExpect(jsonPath("$.links[0].rel", is("self")))
        .andExpect(jsonPath("$.links[0]", hasKey("href")))
        .andExpect(jsonPath("$.links[0].href", endsWith("genes")));
  }

  @Test
  public void findBySimpleStringAttributeTest() throws Exception {

    mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=GeneB").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("2")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("DEF")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findBySimpleCollectionElement() throws Exception {
    mockMvc.perform(get(BASE_URL + "?aliases=GHI").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("3")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("GHI")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findBySimpleMapElement() throws Exception {
    mockMvc.perform(get(BASE_URL + "?attributes=isKinase:Y").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findByMultipleStringAttributesTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=GeneB,GeneD").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[1]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[1].primaryReferenceId", is("4")))
        .andExpect(jsonPath("$[1]", hasKey("aliases")))
        .andExpect(jsonPath("$[1].aliases", hasSize(1)))
        .andExpect(jsonPath("$[1].aliases[0]", is("JKL")))
        .andExpect(jsonPath("$[1]", hasKey("attributes")))
        .andExpect(jsonPath("$[1].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[1].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$[1]", not(hasKey("links"))));
  }

  @Test
  public void findByMultipleCollectionElements() throws Exception {
    mockMvc.perform(get(BASE_URL + "?aliases=DEF,GHI").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[1]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[1].primaryReferenceId", is("3")))
        .andExpect(jsonPath("$[1]", hasKey("aliases")))
        .andExpect(jsonPath("$[1].aliases", hasSize(1)))
        .andExpect(jsonPath("$[1].aliases[0]", is("GHI")))
        .andExpect(jsonPath("$[1]", hasKey("attributes")))
        .andExpect(jsonPath("$[1].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[1].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$[1]", not(hasKey("links"))));
  }

  @Test
  public void findByMultipleMapElements() throws Exception {
    mockMvc.perform(get(BASE_URL + "?attributes=isKinase:Y,N").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findByStringLikeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=*B").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("2")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("DEF")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findByStringNotLikeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=!*B").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findByStringEqualsTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?geneType=!protein-coding").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$[0].primaryReferenceId", is("3")))
        .andExpect(jsonPath("$[0]", hasKey("aliases")))
        .andExpect(jsonPath("$[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$[0].aliases[0]", is("GHI")))
        .andExpect(jsonPath("$[0]", hasKey("attributes")))
        .andExpect(jsonPath("$[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$[0]", not(hasKey("links"))));
  }

  @Test
  public void findByNumberEqualsTest() throws Exception {
    mockMvc.perform(get(EXPRESSION_URL + "?value=1.23").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("geneExpression")))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("value")))
        .andExpect(jsonPath("$[0].value", is(1.23)));
  }

//	@Test
//	public void findByNumberGreaterThanTest() throws Exception {
//			mockMvc.perform(get(EXPRESSION_URL + "?value=>5"))
//							.andExpect(status().isOk())
//							.andExpect(jsonPath("$", hasKey("_embedded")))
//							.andExpect(jsonPath("$._embedded", hasKey("geneExpression")))
//							.andExpect(jsonPath("$._embedded.geneExpression", hasSize(3)))
//							.andExpect(jsonPath("$._embedded.geneExpression[0]", hasKey("value")))
//							.andExpect(jsonPath("$._embedded.geneExpression[0].value", greaterThan(5.0)));
//	}

  @Test
  public void findById() throws Exception {

    List<Gene> genes = (List<Gene>) geneRepository.findAll();
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue("1".equals(gene.getPrimaryReferenceId()));
    String id = gene.getId();

    mockMvc.perform(get(BASE_URL + "/" + id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.primaryReferenceId", is("1")))
        .andExpect(jsonPath("$", hasKey("aliases")))
        .andExpect(jsonPath("$.aliases", hasSize(1)))
        .andExpect(jsonPath("$.aliases[0]", is("ABC")))
        .andExpect(jsonPath("$", hasKey("attributes")))
        .andExpect(jsonPath("$.attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$", not(hasKey("links"))));
  }

  @Test
  public void headTest() throws Exception {
    mockMvc.perform(head(BASE_URL))
        .andExpect(status().isNoContent());
  }

  @Test
  public void createTest() throws Exception {

    Gene gene = new Gene();
    gene.setPrimaryReferenceId("6");
    gene.setPrimaryGeneSymbol("GeneF");
    gene.setTaxId(9606);
    gene.setChromosome("10");
    gene.setGeneType("protein-coding");

    objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("fieldFilter",
        SimpleBeanPropertyFilter.serializeAllExcept()).setFailOnUnknownId(false));
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mockMvc.perform(post(BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(gene)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.primaryReferenceId", is("6")));

    mockMvc.perform(get(BASE_URL + "?primaryReferenceId=6").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]", hasKey("primaryGeneSymbol")))
        .andExpect(jsonPath("$[0].primaryGeneSymbol", is("GeneF")));

  }

  @Test
  public void updateTest() throws Exception {

    List<Gene> genes = geneRepository.findByPrimaryGeneSymbol("GeneA");
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Gene gene = genes.get(0);
    Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
    gene.setPrimaryGeneSymbol("GeneX");

    objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("fieldFilter",
        SimpleBeanPropertyFilter.serializeAllExcept()).setFailOnUnknownId(false));
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mockMvc.perform(put(BASE_URL + "/{id}", gene.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(gene)))
        //.andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.primaryReferenceId", is("1")))
        .andExpect(jsonPath("$.primaryGeneSymbol", is("GeneX")));

  }

  @Test
  public void deleteTest() throws Exception {

    geneExpressionRepository.deleteAll();

    List<Gene> genes = geneRepository.findByPrimaryGeneSymbol("GeneA");
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Gene gene = genes.get(0);

    mockMvc.perform(delete(BASE_URL + "/{id}", gene.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get(BASE_URL + "/{id}", gene.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

  }

//	// TODO: Fix Text output
//	@Test
//	public void delimtedTextTest() throws Exception {
//		mockMvc.perform(get(BASE_URL).accept(MediaType.TEXT_PLAIN))
//				.andExpect(status().isOk())
//				.andDo(print());
//	}


  public MockMvc getMockMvc() {
    return mockMvc;
  }

  public void setMockMvc(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }
}
