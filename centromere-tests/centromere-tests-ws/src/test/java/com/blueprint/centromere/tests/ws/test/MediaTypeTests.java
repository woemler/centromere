package com.blueprint.centromere.tests.ws.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class MediaTypeTests extends AbstractRepositoryTests {

  private static final String TEST_URL = "/api/search/gene?_size=10";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void defaultMediaTypeTest() throws Exception {
    mockMvc.perform(get(TEST_URL))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }
  
  @Test
  public void jsonAcceptHeaderTest() throws Exception {
    mockMvc.perform(get(TEST_URL).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  public void jsonQueryParameterTest() throws Exception {
    mockMvc.perform(get(TEST_URL + "&_format=json"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  public void halJsonAcceptHeaderTest() throws Exception {
    mockMvc.perform(get(TEST_URL).accept(ApiMediaTypes.APPLICATION_HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
  }

  @Test
  public void halJsonQueryParameterTest() throws Exception {
    mockMvc.perform(get(TEST_URL + "&_format=haljson"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/hal+json"));
  }

  @Test
  public void xmlAcceptHeaderTest() throws Exception {
    mockMvc.perform(get(TEST_URL).accept(MediaType.APPLICATION_XML))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
  }

  @Test
  public void xmlQueryParameterTest() throws Exception {
    mockMvc.perform(get(TEST_URL + "&_format=xml"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
  }

  @Test
  public void halXmlAcceptHeaderTest() throws Exception {
    mockMvc.perform(get(TEST_URL).accept(ApiMediaTypes.APPLICATION_HAL_XML_VALUE))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(ApiMediaTypes.APPLICATION_HAL_XML_VALUE));
  }

  @Test
  public void halXmlQueryParameterTest() throws Exception {
    mockMvc.perform(get(TEST_URL + "&_format=halxml"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(ApiMediaTypes.APPLICATION_HAL_XML_VALUE));
  }

  @Test
  public void textTableAcceptHeaderTest() throws Exception {
    mockMvc.perform(get(TEST_URL).accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
  }

  @Test
  public void textTableQueryParameterTest() throws Exception {
    mockMvc.perform(get(TEST_URL + "&_format=text"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
  }

}
