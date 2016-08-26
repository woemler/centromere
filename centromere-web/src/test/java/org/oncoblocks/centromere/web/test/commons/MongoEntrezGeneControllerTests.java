/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.web.test.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.repository.QueryParameterDescriptor;
import org.oncoblocks.centromere.core.commons.testing.EntrezGeneDataGenerator;
import org.oncoblocks.centromere.mongodb.commons.models.MongoGene;
import org.oncoblocks.centromere.mongodb.commons.repositories.MongoGeneRepository;
import org.oncoblocks.centromere.web.controller.RequestUtils;
import org.oncoblocks.centromere.web.test.config.TestWebConfig;
import org.oncoblocks.centromere.web.util.ApiMediaTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		CommonsMongoDataSourceConfig.class, TestWebConfig.class, CommonsControllerConfig.class})
@FixMethodOrder
public class MongoEntrezGeneControllerTests {

	@Autowired private MongoGeneRepository geneRepository;
	private MockMvc mockMvc;
	@Autowired private WebApplicationContext webApplicationContext;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private static final String BASE_URL = "/commons/mongo/entrezgene";

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		geneRepository.deleteAll();
		geneRepository.insert(new EntrezGeneDataGenerator<MongoGene>().generateData(MongoGene.class));
	}
	
	private String getGeneIdByEntrezGeneId(Long id){
		List<MongoGene> genes = geneRepository.findByPrimaryReferenceId(id.toString());
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		MongoGene gene = genes.get(0);
		Assert.notNull(gene.getId());
		return gene.getId();
	}

	@Test
	public void requestUtilTest() throws Exception {
		Map<String, QueryParameterDescriptor>
				params = RequestUtils.getAvailableQueryParameters(MongoGene.class);
		Assert.notNull(params);
		Assert.notEmpty(params);
		for (Map.Entry entry: params.entrySet()){
			System.out.println(String.format("Param: %s, Type: %s", entry.getKey(), (entry.getValue()).toString()));
		}
		Assert.isTrue(params.containsKey("id"));
		Assert.isTrue(params.containsKey("primaryGeneSymbol"));
		Assert.isTrue(params.containsKey("alias"));
		Assert.isTrue(params.containsKey("attributes"));
		Assert.isTrue(params.containsKey("primaryReferenceId"));
	}

	@Test
	public void headTest() throws Exception {
		mockMvc.perform(head(BASE_URL))
				.andExpect(status().isOk());
	}

	@Test
	public void findById() throws Exception {
		String geneId = getGeneIdByEntrezGeneId(1L);
		mockMvc.perform(get(BASE_URL + "/{id}", geneId)
				.accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.primaryReferenceId", is("1")))
				.andExpect(jsonPath("$.primaryGeneSymbol", is("GeneA")))
				.andExpect(jsonPath("$.links", hasSize(1)))
				.andExpect(jsonPath("$.links[0].rel", is("self")))
				.andExpect(jsonPath("$.links[0].href", endsWith(BASE_URL + "/" + geneId)));
	}

	@Test
	public void findByIdFiltered() throws Exception {
		String geneId = getGeneIdByEntrezGeneId(1L);
		mockMvc.perform(get(BASE_URL + "/{id}?exclude=links,primaryGeneSymbol", geneId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.primaryReferenceId", is("1")))
				.andExpect(jsonPath("$", not(hasKey("primaryGeneSymbol"))))
				.andExpect(jsonPath("$", not(hasKey("links"))));
	}

	@Test
	public void findAll() throws Exception {
		mockMvc.perform(get(BASE_URL).accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.content[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$", hasKey("links")))
				.andExpect(jsonPath("$.links", hasSize(1)))
				.andExpect(jsonPath("$.links[0].rel", is("self")))
				.andExpect(jsonPath("$.links[0].href", endsWith(BASE_URL)))
				.andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
	}

	@Test
	public void findFiltered() throws Exception {
		mockMvc.perform(get(BASE_URL + "?exclude=links,primaryGeneSymbol").accept(
				ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.content[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$.content[0]", not(hasKey("primaryGeneSymbol"))))
				.andExpect(jsonPath("$.content[0]", not(hasKey("links"))));
	}

	@Test
	public void findByKeyValueAttributes() throws Exception {
		mockMvc.perform(get(BASE_URL + "?attributes.isKinase=Y").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$[0].primaryReferenceId", is("1")))
				.andExpect(jsonPath("$[0]", not(hasKey("links"))));
	}

	@Test
	public void findFieldFiltered() throws Exception {
		mockMvc.perform(get(BASE_URL + "?fields=links,primaryGeneSymbol").accept(
				ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", not(hasKey("primaryReferenceId"))))
				.andExpect(jsonPath("$.content[0]", hasKey("primaryGeneSymbol")))
				.andExpect(jsonPath("$.content[0]", hasKey("links")));
	}

	@Test
	public void findMultipleByParams() throws Exception {
		mockMvc.perform(get(BASE_URL + "?geneType=pseudo").accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.content[0].primaryReferenceId", is("3")))
				.andExpect(jsonPath("$", hasKey("links")))
				.andExpect(jsonPath("$.links", hasSize(1)))
				.andExpect(jsonPath("$.links[0].rel", is("self")))
				.andExpect(jsonPath("$.links[0].href", endsWith(BASE_URL + "?geneType=pseudo")))
				.andExpect(jsonPath("$", not(hasKey("pageMetadata"))));
	}

	@Test
	public void findByAlias() throws Exception {
		mockMvc.perform(get(BASE_URL + "?alias=MNO").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$[0].primaryReferenceId", is("5")))
				.andExpect(jsonPath("$[0]", not(hasKey("links"))));
	}

	@Test
	public void findPaged() throws Exception {
		mockMvc.perform(get(BASE_URL + "?page=1&size=3").accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.content[0].primaryReferenceId", is("4")))
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
	public void findSorted() throws Exception {
		mockMvc.perform(get(BASE_URL + "?sort=primaryGeneSymbol,desc").accept(
				ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("content")))
				.andExpect(jsonPath("$.content", hasSize(5)))
				.andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.content[0].primaryReferenceId", is("5")));
	}
	
	@Test
	public void findDistinct() throws Exception {
		mockMvc.perform(get(BASE_URL + "/distinct?field=geneType"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void findDistinctFiltered() throws Exception {
		mockMvc.perform(get(BASE_URL + "/distinct?field=primaryGeneSymbol&geneType=protein-coding"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[2]", is("GeneD")));
	}

	@Test
	public void createTest() throws Exception {

		MongoGene gene = new MongoGene(); 
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

		String geneId = getGeneIdByEntrezGeneId(6L);
		
		mockMvc.perform(get(BASE_URL + "/{id}", geneId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.primaryReferenceId", is("6")));

		geneRepository.delete(geneId);

	}

	@Test
	public void updateTest() throws Exception {

		MongoGene gene = new MongoGene();
		gene.setPrimaryReferenceId("7");
		gene.setPrimaryGeneSymbol("GeneG");
		gene.setTaxId(9606);
		gene.setChromosome("10");
		gene.setGeneType("protein-coding");
		objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("fieldFilter",
						SimpleBeanPropertyFilter.serializeAllExcept()).setFailOnUnknownId(false));
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(gene)))
				.andExpect(status().isCreated());
	
		gene = geneRepository.findByPrimaryReferenceId("7").get(0);
		gene.setPrimaryGeneSymbol("TEST_GENE");
		
		mockMvc.perform(put(BASE_URL + "/{id}", gene.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(gene)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$", hasKey("primaryReferenceId")))
				.andExpect(jsonPath("$.primaryReferenceId", is("7")))
				.andExpect(jsonPath("$.primaryGeneSymbol", is("TEST_GENE")));

		mockMvc.perform(get(BASE_URL + "/{id}", gene.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.primaryReferenceId", is("7")))
				.andExpect(jsonPath("$.primaryGeneSymbol", is("TEST_GENE")));

		geneRepository.delete(gene.getId());

	}

	@Test
	public void deleteTest() throws Exception {

		MongoGene gene = new MongoGene();
		gene.setPrimaryReferenceId("8");
		gene.setPrimaryGeneSymbol("GeneG");
		gene.setTaxId(9606);
		gene.setChromosome("10");
		gene.setGeneType("protein-coding");
		geneRepository.insert(gene);
		String geneId = getGeneIdByEntrezGeneId(8L);

		mockMvc.perform(delete(BASE_URL + "/{id}", geneId))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_URL + "/{id}", geneId))
				.andExpect(status().isNotFound());

	}

}
