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

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.web.config.WebApplicationConfig;
import com.blueprint.centromere.tests.common.AbstractRepositoryTests;
import com.blueprint.centromere.tests.common.MongoDataSourceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {
		MongoDataSourceConfig.class,
		WebApplicationConfig.class
})
@ActiveProfiles({ Profiles.WEB_PROFILE, Profiles.NO_SECURITY, Profiles.API_DOCUMENTATION_DISABLED_PROFILE })
public class ModelControllerTests extends AbstractRepositoryTests {
	
	private static final String BASE_URL = "/api/genes";
	
	@Autowired private WebApplicationContext context;
	@Autowired private PersistentEntities entities;
	@Autowired private ApplicationContext applicationContext;
	@Autowired private GeneRepository geneRepository;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Before
  @Override
	public void setup() throws Exception {
		super.setup();
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.build();
	}

	@Test
  public void test(){
    for (MappingContext<?, ?> context : BeanFactoryUtils
        .beansOfTypeIncludingAncestors(applicationContext, MappingContext.class).values()) {
      System.out.println(context.toString());
    }

  }
  
  // Guess tests
	
	@Test
	public void guessTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/guess?keyword=ABC"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("_embedded")))
				.andExpect(jsonPath("$._embedded", hasKey("genes")))
				.andExpect(jsonPath("$._embedded.genes", hasSize(1)))
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
	
	// Distinct Tests
	
	@Test
	public void distinctTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/distinct?field=geneType"))
				.andExpect(status().isOk())
				.andDo(print());
	}
	
	// Query tests
	
	@Test
  public void findAllQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(5)))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$", not(hasKey("_embedded"))))
        .andExpect(jsonPath("$", not(hasKey("_links"))));
  }
  // TODO
//  @Test
//  public void findAllFieldFilteredQueryTest() throws Exception {
//    mockMvc.perform(get(BASE_URL + "/query?fields=id,primaryGeneSymbol"))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$", hasKey("content")))
//        .andExpect(jsonPath("$.content", hasSize(5)))
//        .andExpect(jsonPath("$.content[0]", hasKey("id")))
//        .andExpect(jsonPath("$.content[0]", hasKey("primaryGeneSymbol")))
//        .andExpect(jsonPath("$.content[0]", not(hasKey("primaryReferenceId"))));
//  }

  @Test
  public void findBySimpleStringAttributeQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?primaryGeneSymbol=GeneB"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("2")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("DEF")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }

  @Test
  public void findBySimpleCollectionQueryElement() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?aliases=GHI"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("3")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("GHI")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }

  @Test
  public void findBySimpleMapElementQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?attributes=isKinase:Y"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }

  @Test
  public void findByMultipleStringAttributesQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?primaryGeneSymbol=GeneB,GeneD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[1]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[1].primaryReferenceId", is("4")))
        .andExpect(jsonPath("$.content[1]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[1].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[1].aliases[0]", is("JKL")))
        .andExpect(jsonPath("$.content[1]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[1].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[1].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$.content[1]", not(hasKey("_links"))));
  }

  @Test
  public void findByMultipleCollectionElementsQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?aliases=DEF,GHI"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[1]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[1].primaryReferenceId", is("3")))
        .andExpect(jsonPath("$.content[1]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[1].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[1].aliases[0]", is("GHI")))
        .andExpect(jsonPath("$.content[1]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[1].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[1].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$.content[1]", not(hasKey("_links"))));
  }

  @Test
  public void findByMultipleMapElementsQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?attributes=isKinase:Y,N"))
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
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }

  @Test
  public void findByStringLikeQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?primaryGeneSymbol=*B"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("2")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("DEF")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }

  @Test
  public void findByStringNotLikeQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?primaryGeneSymbol=!*B"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("1")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("ABC")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("Y")))
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }

  @Test
  public void findByStringEqualsQueryTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/query?geneType=!protein-coding"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0]", hasKey("primaryReferenceId")))
        .andExpect(jsonPath("$.content[0].primaryReferenceId", is("3")))
        .andExpect(jsonPath("$.content[0]", hasKey("aliases")))
        .andExpect(jsonPath("$.content[0].aliases", hasSize(1)))
        .andExpect(jsonPath("$.content[0].aliases[0]", is("GHI")))
        .andExpect(jsonPath("$.content[0]", hasKey("attributes")))
        .andExpect(jsonPath("$.content[0].attributes", hasKey("isKinase")))
        .andExpect(jsonPath("$.content[0].attributes.isKinase", is("N")))
        .andExpect(jsonPath("$.content[0]", not(hasKey("_links"))));
  }


}
