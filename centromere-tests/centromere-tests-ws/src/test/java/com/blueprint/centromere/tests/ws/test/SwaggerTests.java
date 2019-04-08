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

import com.blueprint.centromere.tests.ws.WebTestInitializer;
import com.blueprint.centromere.ws.config.ApiDocumentationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { WebTestInitializer.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ApiDocumentationConfig.SWAGGER_PROFILE})
@AutoConfigureMockMvc(secure = false)
public class SwaggerTests {

  @Autowired private MockMvc mockMvc;

  @Test
  public void swaggerEndpointTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs"))
        .andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/api-docs"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void swaggerUITest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui.html"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

}
