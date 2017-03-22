/*
 * Copyright 2016 the original author or authors
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.core.dataimport.impl.repositories.GeneRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.config.Security;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import com.blueprint.centromere.ws.config.WebApplicationConfig;
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
@ActiveProfiles({ Profiles.WEB_PROFILE, Security.NONE_PROFILE})
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
	
	@Test
	public void guessTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/guess?keyword=ABC"))
				.andExpect(status().isOk())
				.andDo(print())
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
	
	@Test
	public void distinctTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/distinct?field=geneType"))
				.andExpect(status().isOk())
				.andDo(print());
	}

}
