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

package com.blueprint.centromere.tests.ws;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.repositories.DataFileRepository;
import com.blueprint.centromere.core.commons.repositories.DataSetRepository;
import com.blueprint.centromere.core.commons.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.Assert;

/**
 * @author woemler
 */

public abstract class AbstractControllerTests extends AbstractRepositoryTests {
	
	private static final String BASE_URL = "/api/genes";
	private static final String EXPRESSION_URL = "/api/geneexpression";
	
	@Autowired private SampleRepository sampleRepository;
	@Autowired private SubjectRepository subjectRepository;
	@Autowired private DataFileRepository dataFileRepository;
	@Autowired private DataSetRepository dataSetRepository;
	@Autowired private GeneRepository geneRepository;
	@Autowired private GeneExpressionRepository geneExpressionRepository;
	@Autowired @Qualifier("objectMapper") private ObjectMapper objectMapper;
	
	private MockMvc mockMvc;

	@Test
	public void findAllTest() throws Exception {
		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(5)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("ABC")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("Y")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}
	
	@Test
	public void findBySimpleStringAttributeTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=GeneB"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("2")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("DEF")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("N")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findBySimpleCollectionElement() throws Exception {
		mockMvc.perform(get(BASE_URL + "?aliases=GHI"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("3")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("GHI")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("N")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findBySimpleMapElement() throws Exception {
		mockMvc.perform(get(BASE_URL + "?attributes=isKinase:Y"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(2)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("ABC")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("Y")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findByMultipleStringAttributesTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=GeneB,GeneD"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(2)))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[1].primaryReferenceId", is("4")))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[1].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[1].aliases[0]", is("JKL")))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[1].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[1].attributes.isKinase", is("Y")))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[1]._links", hasKey("self")));
	}

	@Test
	public void findByMultipleCollectionElements() throws Exception {
		mockMvc.perform(get(BASE_URL + "?aliases=DEF,GHI"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(2)))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[1].primaryReferenceId", is("3")))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[1].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[1].aliases[0]", is("GHI")))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[1].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[1].attributes.isKinase", is("N")))
				.andExpect(jsonPath("$._embedded.genes[1]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[1]._links", hasKey("self")));
	}

	@Test
	public void findByMultipleMapElements() throws Exception {
		mockMvc.perform(get(BASE_URL + "?attributes=isKinase:Y,N"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(5)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("ABC")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("Y")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findByStringLikeTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=*B"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("2")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("DEF")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("N")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findByStringNotLikeTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "?primaryGeneSymbol=!*B"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(4)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("ABC")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("Y")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findByStringEqualsTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "?geneType=!protein-coding"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(2)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryReferenceId", is("3")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("aliases")))
				.andExpect(jsonPath("$._embedded.genes[0].aliases", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0].aliases[0]", is("GHI")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("attributes")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$._embedded.genes[0].attributes.isKinase", is("N")))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("_links")))
				.andExpect(jsonPath("$._embedded.genes[0]._links", hasKey("self")));
	}

	@Test
	public void findByNumberEqualsTest() throws Exception {
			mockMvc.perform(get(EXPRESSION_URL + "?value=1.23"))
							.andExpect(status().isOk())
							.andExpect(jsonPath("$", hasKey("_embedded")))
							.andExpect(jsonPath("$._embedded", hasKey("geneExpression")))
							.andExpect(jsonPath("$._embedded.geneExpression", hasSize(1)))
							.andExpect(jsonPath("$._embedded.geneExpression[0]", hasKey("value")))
							.andExpect(jsonPath("$._embedded.geneExpression[0].value", is(1.23)));
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

		mockMvc.perform(get(BASE_URL + "/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.primaryReferenceId", is("1")))
				.andExpect(jsonPath("$", hasKey("aliases")))
				.andExpect(jsonPath("$.aliases", hasSize(1)))
				.andExpect(jsonPath("$.aliases[0]", is("ABC")))
				.andExpect(jsonPath("$", hasKey("attributes")))
				.andExpect(jsonPath("$.attributes", hasKey("isKinase")))
				.andExpect(jsonPath("$.attributes.isKinase", is("Y")))
				.andExpect(jsonPath("$", hasKey("_links")))
				.andExpect(jsonPath("$._links", hasKey("self")));
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

		mockMvc.perform(get(BASE_URL + "?primaryReferenceId=6"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(1)))
				.andExpect(jsonPath("$._embedded.genes[0]", hasKey("primaryGeneSymbol")))
				.andExpect(jsonPath("$._embedded.genes[0].primaryGeneSymbol", is("GeneF")));

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
				.andDo(MockMvcResultHandlers.print())
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

		mockMvc.perform(get(BASE_URL + "/{id}", gene.getId()))
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
