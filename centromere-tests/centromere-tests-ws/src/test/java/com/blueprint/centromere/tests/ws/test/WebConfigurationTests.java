package com.blueprint.centromere.tests.ws.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.tests.ws.WebTestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class WebConfigurationTests {

  @Autowired private MockMvc mockMvc;

  @Test
  public void corsEnabledTest() throws Exception {
    mockMvc.perform(options("/api/genes")
        .header("Access-Control-Request-Method", "GET")
        .header("Origin", "http://www.someurl.com"))
        .andExpect(status().isOk());
  }
  
}
