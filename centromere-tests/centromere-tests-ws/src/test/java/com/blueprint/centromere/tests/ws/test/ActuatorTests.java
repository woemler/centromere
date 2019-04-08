package com.blueprint.centromere.tests.ws.test;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class ActuatorTests extends AbstractRepositoryTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private Environment env;

    @Autowired(required = false) private MongoHealthIndicator mongoHealthIndicator;

    @Test
    public void configTest() {
        Assert.notNull(mongoHealthIndicator);
    }

    @Test
    public void actuatorRootTest() throws Exception {
        mockMvc.perform(get("/actuator"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("_links")))
            .andExpect(jsonPath("$._links", hasKey("self")))
            .andExpect(jsonPath("$._links", hasKey("health")))
            .andExpect(jsonPath("$._links", hasKey("info")));
    }

    @Test
    public void healthTest() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("status")))
            .andExpect(jsonPath("$.status", is("UP")))
            .andExpect(jsonPath("$", hasKey("details")))
            .andExpect(jsonPath("$.details", hasKey("mongo")))
            .andExpect(jsonPath("$.details.mongo", hasKey("status")))
            .andExpect(jsonPath("$.details.mongo.status", is("UP")));
    }

    @Test
    public void infoTest() throws Exception {
        mockMvc.perform(get("/actuator/info"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasKey("app")))
            .andExpect(jsonPath("$.app", hasKey("name")))
            .andExpect(jsonPath("$.app.name", is(env.getRequiredProperty("centromere.web.api.name"))))
            .andExpect(jsonPath("$", hasKey("api")))
            .andExpect(jsonPath("$", hasKey("contact")))
            .andExpect(jsonPath("$", hasKey("dependencies")))
            .andExpect(jsonPath("$.dependencies", hasKey("centromere")))
            .andExpect(jsonPath("$.dependencies.centromere", hasKey("version")));
    }

}
