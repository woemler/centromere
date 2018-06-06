package com.blueprint.centromere.tests.ws.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = { Profiles.SCHEMA_DEFAULT, Profiles.WEB_PROFILE, Profiles.NO_SECURITY, Profiles.API_DOCUMENTATION_DISABLED_PROFILE })
@TestPropertySource(properties = { "centromere.web.enable-static-content: false" })
@AutoConfigureMockMvc(secure = false)
public class StaticContentDisabledTests {

  @Autowired
  private Environment environment;
  @Autowired private MockMvc mockMvc;

  @Test
  public void homePageTest() throws Exception {

    Assert.isTrue(environment.getProperty("centromere.web.enable-static-content").equals("false"), "centromere.web.enable-static-content property should be set to false");

    mockMvc.perform(get("/index.html").accept(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound());
  }

}
