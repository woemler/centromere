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
import com.blueprint.centromere.core.test.ws.WebSecurityTests.SpringBootSetup;
import com.blueprint.centromere.core.ws.config.WebApplicationConfig;
import com.blueprint.centromere.core.ws.security.BasicTokenUtils;
import com.blueprint.centromere.core.ws.security.TokenDetails;
import com.jayway.jsonpath.JsonPath;
import org.h2.server.web.WebApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import static com.jayway.jsonassert.impl.matcher.IsMapContainingKey.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootSetup.class })
@ActiveProfiles(value = {Profiles.WEB_PROFILE, Security.SECURE_READ_WRITE_PROFILE})
public class WebSecurityTests {
	
	@Autowired private WebApplicationContext context;
	@Autowired private UserRepository userRepository;
	@Autowired private FilterChainProxy springSecurityFilterChain;
	@Autowired private BasicTokenUtils tokenUtils;
	
	private MockMvc mockMvc;
	private static boolean isConfigured = false;

	@SpringBootApplication
  @Import({ EmbeddedH2DataSourceConfig.class, WebApplicationConfig.class})
	public static class SpringBootSetup {
    public static void main(String[] args) {
      SpringApplication.run(SpringBootSetup.class, args);
    }
  }
	
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
				.andExpect(status().isForbidden());
	}

	@Test
	public void validTokenRequestTest() throws Exception {
		User user = userRepository.loadUserByUsername("user");
		Assert.notNull(user);
		Assert.isTrue("user".equals(user.getUsername()));
		TokenDetails tokenDetails = tokenUtils.createTokenAndDetails(user);
		mockMvc.perform(get("/api/genes")
				.header("X-Auth-Token", tokenDetails.getToken()))
				.andExpect(status().isOk());
	}

	@Test
	public void badTokenTest() throws Exception {
		mockMvc.perform(get("/api/genes")
				.header("X-Auth-Token", "user:23459837145:gwerhg97wr9tgwg"))
				.andExpect(status().isForbidden());
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
				.andDo(MockMvcResultHandlers.print())
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
