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

package com.blueprint.centromere.core.test.ws;

import com.blueprint.centromere.core.commons.models.User;
import com.blueprint.centromere.core.commons.repositories.UserRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.config.Security;
import com.blueprint.centromere.core.test.jpa.EmbeddedH2DataSourceConfig;
import com.blueprint.centromere.core.ws.config.SpringWebCustomization;
import com.blueprint.centromere.core.ws.config.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.jayway.jsonassert.impl.matcher.IsMapContainingKey.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {
		EmbeddedH2DataSourceConfig.class, 
		RepositoryRestMvcConfiguration.class,
		SpringWebCustomization.WebServicesConfig.class,
		WebSecurityConfig.class
})
@ActiveProfiles(value = {Profiles.WEB_PROFILE, Security.SECURE_READ_WRITE_PROFILE})
public class WebSecurityTests {
	
	@Autowired private WebApplicationContext context;
	@Autowired private UserRepository userRepository;
	@Autowired private Environment env;
	@Autowired private FilterChainProxy springSecurityFilterChain;
	
	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static boolean isConfigured = false;
	
	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.addFilter(springSecurityFilterChain)
				.build();
		if (!isConfigured){
			userRepository.deleteAll();
			User user = new User();
			user.setUsername("user");
			user.setPassword(new BCryptPasswordEncoder().encode("password"));
			user.setEnabled(true);
			userRepository.save(user);
			isConfigured = true;
		}
	}
	
	@Test
	public void nonAuthenticatedGetRequestTest() throws Exception {
		mockMvc.perform(get("/api/genes"))
				.andExpect(status().isForbidden())
				;
	}
	
	@Test 
	public void invalidUserAuthenticationTest() throws Exception {
		mockMvc.perform(post("/authenticate")
				.with(httpBasic("not", "correct")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void invalidPasswordAuthenticationTest() throws Exception {
		mockMvc.perform(post("/authenticate")
				.with(httpBasic("user", "notpassword")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void validPasswordAuthenticationTest() throws Exception {
		mockMvc.perform(post("/authenticate")
				.with(httpBasic("user", "password")))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void userAuthenticationTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/authenticate")
				.with(httpBasic("user", "password")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasKey("token")))
				.andReturn();

		String json = result.getResponse().getContentAsString();
		System.out.println(json);
		String token = JsonPath.read(json, "$.token");

		mockMvc.perform(get("/api/genes")
				.header("X-Auth-Token", token))
				.andExpect(status().isOk());
	}
	
}
