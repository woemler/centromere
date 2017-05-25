package com.blueprint.centromere.tests.ws.test;

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.ws.TestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestInitializer.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = {Profiles.WEB_PROFILE, Profiles.NO_SECURITY, Profiles.API_DOCUMENTATION_ENABLED_PROFILE})
public class SwaggerTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private Environment env;

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
