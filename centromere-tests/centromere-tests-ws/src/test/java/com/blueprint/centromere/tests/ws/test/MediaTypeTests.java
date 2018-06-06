package com.blueprint.centromere.tests.ws.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = { Profiles.SCHEMA_DEFAULT, Profiles.WEB_PROFILE, Profiles.NO_SECURITY, Profiles.API_DOCUMENTATION_DISABLED_PROFILE })
@AutoConfigureMockMvc(secure = false)
public class MediaTypeTests extends AbstractRepositoryTests {

  private static final String BASE_URL = "/api/gene";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void defaultMediaTypeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?size=10"))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  public void halJsonTypeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?size=10").accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
  }

  @Test
  public void xmlMediaTypeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?size=10").accept(MediaType.APPLICATION_XML))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
  }

  @Test
  public void halXmlMediaTypeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?size=10").accept(ApiMediaTypes.APPLICATION_HAL_XML_VALUE))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(ApiMediaTypes.APPLICATION_HAL_XML_VALUE));
  }

  @Test
  public void textTableMediaTypeTest() throws Exception {
    mockMvc.perform(get(BASE_URL + "?size=10").accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
  }

}
