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

import com.blueprint.centromere.tests.ws.WebTestInitializer;
import com.blueprint.centromere.ws.config.ApiDocumentationConfig;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebTestInitializer.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ApiDocumentationConfig.SWAGGER_PROFILE})
@AutoConfigureMockMvc(secure = false)
public class SwaggerApiDocumentationTests {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    public void apiInfoTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasKey("info")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info", Matchers.hasKey("title")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info", Matchers.hasKey("contact")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info", Matchers.hasKey("description")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.info", Matchers.hasKey("license")));
    }

    @Test
    public void modelPathInfoTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasKey("paths")))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.paths", Matchers.hasKey("/api/search/sample")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths", Matchers.hasKey("/api/search/sample/{id}")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths", Matchers.hasKey("/api/aggregate/sample/distinct/{field}")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths", Matchers.hasKey("/api/aggregate/sample/count")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths", Matchers.hasKey("/api/aggregate/sample/group/{field}")));
    }

    @Test
    public void findOneDocumentationTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasKey("paths")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths", Matchers.hasKey("/api/search/sample/{id}")))

            // GET operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}", Matchers.hasKey("get")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.operationId",
                    Matchers.is("FindById")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.consumes",
                    Matchers.containsInAnyOrder("application/json")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.produces", Matchers
                    .containsInAnyOrder("application/json", "application/xml",
                        "application/hal+json", "application/hal+xml",
                        "text/plain; charset=utf-8")))
            //// GET parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get", Matchers.hasKey("parameters")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters", Matchers.hasSize(4)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0].name",
                    Matchers.is("id")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0]",
                    Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0].in",
                    Matchers.is("path")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0].required",
                    Matchers.is(true)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0]",
                    Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[0].type",
                    Matchers.is("string")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1].name",
                    Matchers.is("_include")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1]",
                    Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1].in",
                    Matchers.is("query")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1].required",
                    Matchers.is(false)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1]",
                    Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[1].type",
                    Matchers.is("string")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[2]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[2].name",
                    Matchers.is("_exclude")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[3]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.parameters[3].name",
                    Matchers.is("_format")))
            //// GET responses
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.responses", Matchers.hasKey("200")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.responses", Matchers.hasKey("400")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.responses", Matchers.hasKey("401")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.get.responses", Matchers.hasKey("404")))

            // HEAD operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}", Matchers.hasKey("head")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head.operationId", Matchers.is("Head")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head.consumes",
                    Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head.produces",
                    Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head.responses", Matchers.hasKey("204")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head.responses", Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.head.responses", Matchers.hasKey("401")))

            // PUT operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}", Matchers.hasKey("put")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.operationId", Matchers.is("Update")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.consumes",
                    Matchers.containsInAnyOrder("application/json")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.produces", Matchers
                    .containsInAnyOrder("application/json", "application/xml",
                        "application/hal+json", "application/hal+xml",
                        "text/plain; charset=utf-8")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.responses", Matchers.hasKey("201")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.responses", Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.responses", Matchers.hasKey("401")))
            //// PUT parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put", Matchers.hasKey("parameters")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters", Matchers.hasSize(3)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0].name",
                    Matchers.is("id")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0]",
                    Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0].in",
                    Matchers.is("path")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0].required",
                    Matchers.is(true)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0]",
                    Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[0].type",
                    Matchers.is("string")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1].name",
                    Matchers.is("entity")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1]",
                    Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1].in",
                    Matchers.is("body")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1].required",
                    Matchers.is(true)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1]",
                    Matchers.hasKey("schema")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1].schema",
                    Matchers.hasKey("$ref")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[1].schema.$ref",
                    Matchers.endsWith("Sample")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[2]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.put.parameters[2].name",
                    Matchers.is("_format")))

            // DELETE operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}", Matchers.hasKey("delete")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.operationId",
                    Matchers.is("Delete")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.consumes",
                    Matchers.containsInAnyOrder("application/json")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.produces", Matchers
                    .containsInAnyOrder("application/json", "application/xml",
                        "application/hal+json", "application/hal+xml",
                        "text/plain; charset=utf-8")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.responses",
                    Matchers.hasKey("201")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.responses",
                    Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.responses",
                    Matchers.hasKey("401")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.responses",
                    Matchers.hasKey("404")))
            //// DELETE parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete", Matchers.hasKey("parameters")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters", Matchers.hasSize(2)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0].name",
                    Matchers.is("id")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0]",
                    Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0].in",
                    Matchers.is("path")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0].required",
                    Matchers.is(true)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0]",
                    Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[0].type",
                    Matchers.is("string")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[1]",
                    Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.delete.parameters[1].name",
                    Matchers.is("_format")))

            // OPTIONS operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}", Matchers.hasKey("options")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample/{id}.options",
                Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options.operationId",
                    Matchers.is("Options")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options.consumes",
                    Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options.produces",
                    Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options.responses",
                    Matchers.hasKey("204")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options.responses",
                    Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample/{id}.options.responses",
                    Matchers.hasKey("401")));

    }

    @Test
    public void findAllDocumentationTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasKey("paths")))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.paths", Matchers.hasKey("/api/search/sample")))

            // GET operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample", Matchers.hasKey("get")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample", Matchers.hasKey("get")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.operationId", Matchers.is("FindAll")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.get.consumes",
                Matchers.containsInAnyOrder("application/json")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.get.produces",
                Matchers.containsInAnyOrder("application/json", "application/xml",
                    "application/hal+json", "application/hal+xml", "text/plain; charset=utf-8")))
            //// GET parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get", Matchers.hasKey("parameters")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters", Matchers.hasSize(16)))

            ////// pagination parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0].name",
                    Matchers.is("_size")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0]", Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0].in", Matchers.is("query")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0].required",
                    Matchers.is(false)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0]", Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[0].type",
                    Matchers.is("integer")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1].name",
                    Matchers.is("_page")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1]", Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1].in", Matchers.is("query")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1].required",
                    Matchers.is(false)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1]", Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[1].type",
                    Matchers.is("integer")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2].name",
                    Matchers.is("_sort")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2]", Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2].in", Matchers.is("query")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2].required",
                    Matchers.is(false)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2]", Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[2].type",
                    Matchers.is("string")))

            ////// filtering parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3].name",
                    Matchers.is("_include")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3]", Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3].in", Matchers.is("query")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3].required",
                    Matchers.is(false)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3]", Matchers.hasKey("type")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[3].type",
                    Matchers.is("string")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[4]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[4].name",
                    Matchers.is("_exclude")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[5]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[5].name",
                    Matchers.is("_format")))

            ////// attribute parameters
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.paths./api/search/sample.get.parameters[?(@.name == \'name\' && @.in == \'query\' && @.required == false && @.type == \'string\')]")
                .exists())
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.paths./api/search/sample.get.parameters[?(@.name == \'aliases\' && @.in == \'query\' && @.required == false && @.type == \'string\')]")
                .exists())
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.paths./api/search/sample.get.parameters[?(@.name == \'attributes.\\\\w+\' && @.in == \'query\' && @.required == false && @.type == \'string\')]")
                .exists())
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.parameters[?(@.name == \'notes\' )]")
                .doesNotExist())

            //// GET responses
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.responses", Matchers.hasKey("200")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.responses", Matchers.hasKey("400")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.responses", Matchers.hasKey("401")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.get.responses", Matchers.hasKey("404")))

            // HEAD operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample", Matchers.hasKey("head")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head.operationId", Matchers.is("Head")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.head.consumes",
                Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.head.produces",
                Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head.responses", Matchers.hasKey("204")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head.responses", Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.head.responses", Matchers.hasKey("401")))

            // POST operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample", Matchers.hasKey("post")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.operationId", Matchers.is("Create")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.post.consumes",
                Matchers.containsInAnyOrder("application/json")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.post.produces",
                Matchers.containsInAnyOrder("application/json", "application/xml",
                    "application/hal+json", "application/hal+xml", "text/plain; charset=utf-8")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.responses", Matchers.hasKey("201")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.responses", Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.responses", Matchers.hasKey("401")))
            //// POST parameters
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post", Matchers.hasKey("parameters")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters", Matchers.hasSize(2)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0].name",
                    Matchers.is("entity")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0]", Matchers.hasKey("in")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0].in", Matchers.is("body")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0]",
                    Matchers.hasKey("required")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0].required",
                    Matchers.is(true)))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0]",
                    Matchers.hasKey("schema")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0].schema",
                    Matchers.hasKey("$ref")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[0].schema.$ref",
                    Matchers.endsWith("Sample")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[1]", Matchers.hasKey("name")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.post.parameters[1].name",
                    Matchers.is("_format")))

            // OPTIONS operations
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample", Matchers.hasKey("options")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options", Matchers.hasKey("operationId")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options.operationId", Matchers.is("Options")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options", Matchers.hasKey("consumes")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.options.consumes",
                Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options", Matchers.hasKey("produces")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths./api/search/sample.options.produces",
                Matchers.containsInAnyOrder("*/*")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options", Matchers.hasKey("responses")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options.responses", Matchers.hasKey("204")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options.responses", Matchers.hasKey("403")))
            .andExpect(MockMvcResultMatchers
                .jsonPath("$.paths./api/search/sample.options.responses", Matchers.hasKey("401")));

    }

    @Test
    public void findDistinctDocumentationTest() throws Exception {
        // TODO
    }

    @Test
    public void findGroupedDocumentationTest() throws Exception {
        // TODO
    }

    @Test
    public void countDocumentationTest() throws Exception {
        // TODO
    }
}
