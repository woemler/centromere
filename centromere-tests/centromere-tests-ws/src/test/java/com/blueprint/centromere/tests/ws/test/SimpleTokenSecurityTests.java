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

import static com.jayway.jsonassert.impl.matcher.IsMapContainingKey.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.tests.core.models.User;
import com.blueprint.centromere.tests.core.repositories.UserRepository;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import com.blueprint.centromere.ws.config.WebSecurityConfig;
import com.blueprint.centromere.ws.security.TokenDetails;
import com.blueprint.centromere.ws.security.simple.SimpleTokenProvider;
import com.jayway.jsonpath.JsonPath;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles({WebSecurityConfig.SIMPLE_TOKEN_SECURITY_PROFILE})
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
public class SimpleTokenSecurityTests {

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired(required = false)
    private SimpleTokenProvider tokenUtils;
    @Autowired
    private MockMvc mockMvc;
    @Autowired(required = false)
    private UserDetailsService userDetailsService;

    @Before
    public void setup() throws Exception {
        userRepository.deleteAll();
        User user = (User) userRepository.getModel().newInstance();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    @Test
    public void configurationTest() throws Exception {
        Assert.assertNotNull(userDetailsService);
        Assert.assertNotNull(userRepository);
        Assert.assertTrue(userDetailsService.equals(userRepository));
        Assert.assertNotNull(passwordEncoder);
        Assert.assertNotNull(tokenUtils);
    }

    @Test
    public void nonAuthenticatedGetRequestTest() throws Exception {
        mockMvc.perform(get("/api/gene"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void validTokenRequestTest() throws Exception {
        User user = (User) userRepository.loadUserByUsername("user");
        Assert.assertNotNull(user);
        Assert.assertTrue("user".equals(user.getUsername()));
        TokenDetails tokenDetails = tokenUtils.createTokenAndDetails(user);
        mockMvc.perform(get("/api/search/gene")
            .header("X-Auth-Token", tokenDetails.getToken()))
            .andExpect(status().isOk());
    }

    @Test
    public void badTokenTest() throws Exception {
        mockMvc.perform(get("/api/gene")
            .header("X-Auth-Token", "user:23459837145:gwerhg97wr9tgwg"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidUserAuthenticationTest() throws Exception {
        mockMvc.perform(post("/authenticate")
            .with(httpBasic("not", "correct")))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/genes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidPasswordAuthenticationTest() throws Exception {
        mockMvc.perform(post("/authenticate")
            .with(httpBasic("user", "notpassword")))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/genes"))
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

        Optional<User> userOptional = userRepository.findByUsername("user");
        Assert.assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        Assert.assertTrue("user".equals(user.getUsername()));
        Assert.assertTrue(passwordEncoder.matches("password", user.getPassword()));

        mockMvc.perform(get("/api/gene"))
            .andExpect(status().isUnauthorized());

        MvcResult result = mockMvc.perform(post("/authenticate")
            .header("Accept", "application/json")
            .with(httpBasic("user", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("token")))
            .andReturn();

        String json = result.getResponse().getContentAsString();
        String token = JsonPath.read(json, "$.token");

        mockMvc.perform(get("/api/search/gene")
            .header("X-Auth-Token", token))
            .andExpect(status().isOk());
    }

    @Test
    public void actuatorSecurityTest() throws Exception {

        mockMvc.perform(get("/actuator"))
            .andExpect(status().isUnauthorized());

        MvcResult result = mockMvc.perform(post("/authenticate")
            .header("Accept", "application/json")
            .with(httpBasic("user", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("token")))
            .andReturn();

        String json = result.getResponse().getContentAsString();
        String token = JsonPath.read(json, "$.token");

        mockMvc.perform(get("/actuator")
            .header("X-Auth-Token", token))
            .andExpect(status().isOk());

    }

}
