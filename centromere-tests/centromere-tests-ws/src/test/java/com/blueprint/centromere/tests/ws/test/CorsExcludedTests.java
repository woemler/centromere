package com.blueprint.centromere.tests.ws.test;

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
public class CorsExcludedTests {

  @Autowired private MockMvc mockMvc;

  @Test
  public void placeholderTest() {
    
  }
  
  //TODO: Find way to mock cross-origin request. Right now, request is always successful.
//  @Test
//  public void corsDisabledTest() throws Exception {
//    mockMvc.perform(options("/api/genes")
//        .header("Access-Control-Request-Method", "GET")
//        .header("Origin", "http://www.someurl.com"))
//        .andExpect(status().is4xxClientError());
//  }
  
}
