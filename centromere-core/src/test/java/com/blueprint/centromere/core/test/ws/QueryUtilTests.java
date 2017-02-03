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

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.test.jpa.EmbeddedH2DataSourceConfig;
import com.blueprint.centromere.core.test.model.EntrezGeneDataGenerator;
import com.blueprint.centromere.core.ws.config.WebApplicationConfig;
import com.blueprint.centromere.core.ws.controller.query.Evaluation;
import com.blueprint.centromere.core.ws.controller.query.QueryCriteria;
import com.blueprint.centromere.core.ws.controller.query.QueryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.StringPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
//@WebAppConfiguration
//@SpringBootTest(classes = {
//		EmbeddedH2DataSourceConfig.class,
//		WebApplicationConfig.class
//})
//@ActiveProfiles({ "default", Profiles.WEB_PROFILE })
public class QueryUtilTests {

//    @Test
//    public void paramToQueryCriteriaTest(){
//
//        QueryCriteria criteria = QueryUtil.getCriteriaFromParameter("primaryGeneSymbol", Gene.class);
//        Assert.notNull(criteria);
//        Assert.isTrue("primaryGeneSymbol".equals(criteria.getName()));
//        Assert.isTrue(String.class.equals(criteria.getType()));
//        Assert.isTrue(Gene.class.equals(criteria.getModel()));
//        Assert.isTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));
//        Assert.isTrue(criteria.getPath() instanceof StringPath);
//
//        criteria = QueryUtil.getCriteriaFromParameter("aliases", Gene.class);
//        Assert.notNull(criteria);
//        Assert.isTrue("aliases".equals(criteria.getName()));
//        Assert.isTrue(String.class.equals(criteria.getType()));
//        Assert.isTrue(Gene.class.equals(criteria.getModel()));
//        Assert.isTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));
//        Assert.isTrue(criteria.getPath() instanceof ListPath);
//
//        criteria = QueryUtil.getCriteriaFromParameter("attributes.isKinase", Gene.class);
//        Assert.notNull(criteria);
//        Assert.isTrue("attributes.isKinase".equals(criteria.getName()));
//        Assert.isTrue(String.class.equals(criteria.getType()));
//        Assert.isTrue(Gene.class.equals(criteria.getModel()));
//        Assert.isTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));
//        Assert.isTrue(criteria.getPath() instanceof MapPath);
//
//    }
//
//	@Test
//	public void paramSuffixTest(){
//        //QueryUtil.
//    }

}
