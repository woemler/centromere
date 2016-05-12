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

package org.oncoblocks.centromere.web.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.web.exceptions.RestError;
import org.oncoblocks.centromere.web.test.config.TestMongoConfig;
import org.oncoblocks.centromere.web.test.config.TestWebConfig;
import org.oncoblocks.centromere.web.test.models.EntrezGene;
import org.oncoblocks.centromere.web.test.repository.EntrezGeneRepository;
import org.oncoblocks.centromere.web.test.repository.MongoRepositoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestWebConfig.class, TestMongoConfig.class,
		MongoRepositoryConfig.class, ControllerIntegrationTestConfig.class})
@WebAppConfiguration
public class ExceptionTests {

	@Autowired private WebApplicationContext webApplicationContext;
	@Autowired private EntrezGeneRepository geneRepository;
	private MockMvc mockMvc;
	private static final String BASE_URL = "/genes/crud";
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		geneRepository.deleteAll();
		for (EntrezGene gene: EntrezGene.createDummyData()){
			geneRepository.insert(gene);
		}
	}
	
	@Test
	public void restErrorSerializationTest() throws Exception {
		RestError error = new RestError(HttpStatus.OK, 12345, "Everything is OK!");
		String json = objectMapper.writeValueAsString(error);
		Assert.isTrue(json.contains("status"));
		Assert.isTrue(!json.contains("developerMessage"));
		error = new RestError(HttpStatus.OK, 12345, "Everything is OK!", "Nothing to see here", "/some/url");
		json = objectMapper.writeValueAsString(error);
		Assert.isTrue(json.contains("status"));
		Assert.isTrue(json.contains("developerMessage"));
	}
	
	@Test
	public void resourceNotFoundExceptionTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{id}", 10L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status", is("NOT_FOUND")))
				.andExpect(jsonPath("$.code", is(40401)))
				.andExpect(jsonPath("$", not(hasKey("developerMessage"))));
	}
	
	@Test
	public void invalidParameterExceptionTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{id}?bad=param", 1L))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.code", is(40001)))
				.andExpect(jsonPath("$", not(hasKey("developerMessage"))));
		mockMvc.perform(get(BASE_URL + "?bad=param"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.code", is(40001)))
				.andExpect(jsonPath("$", not(hasKey("developerMessage"))));
	}
	
	@Test
	public void parameterMappingExceptionTest() throws Exception {
		mockMvc.perform(get(BASE_URL + "?entrezGeneId=bad"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.code", is(40002)))
				.andExpect(jsonPath("$", not(hasKey("developerMessage"))));
	}
	
//	@Test
//	public void malformedEntityExceptionTest() throws Exception {
//		Map<String,Object> entity = new HashMap<>();
//		entity.put("entrezGeneId", 9L);
//		entity.put("name", "test");
//		mockMvc.perform(post(BASE_URL)
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsBytes(entity)))
//				.andExpect(status().isNotAcceptable())
//				.andExpect(jsonPath("$.status", is("NOT_ACCEPTABLE")))
//				.andExpect(jsonPath("$.code", is(40601)));
//	}
	
	
	
}
