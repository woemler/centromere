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

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.web.test.commons.CommonsMongoDataSourceConfig;
import org.oncoblocks.centromere.web.test.config.TestWebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		CommonsMongoDataSourceConfig.class, TestWebConfig.class, 
		ManualControllerMappingConfig.ControllerMappingConfig.class})
@FixMethodOrder
public class ManualControllerMappingTests {

	private MockMvc mockMvc;
	@Autowired private WebApplicationContext webApplicationContext;

	private static final String BASE_URL = "/manual/gene";

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void findAll() throws Exception {
//		mockMvc.perform(get(BASE_URL).accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
//				.andExpect(status().isOk());
	}

}
