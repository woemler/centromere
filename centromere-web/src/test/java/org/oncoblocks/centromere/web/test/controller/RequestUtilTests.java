/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.web.test.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.util.QueryParameterUtil;
import org.oncoblocks.centromere.mongodb.commons.models.MongoGene;
import org.oncoblocks.centromere.web.test.config.DefaultModelRegistryConfig;
import org.oncoblocks.centromere.web.test.config.TestMongoConfig;
import org.oncoblocks.centromere.web.test.config.TestWebConfig;
import org.oncoblocks.centromere.web.test.models.CopyNumber;
import org.oncoblocks.centromere.web.test.repository.MongoRepositoryConfig;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		TestMongoConfig.class, 
		TestWebConfig.class, 
		MongoRepositoryConfig.class, 
		ControllerIntegrationTestConfig.class,
		DefaultModelRegistryConfig.class
})
public class RequestUtilTests {
	
	@Test
	public void requestUtilTest() throws Exception {
		Map<String, QueryParameterDescriptor> params = QueryParameterUtil.getAvailableQueryParameters(CopyNumber.class);
		Assert.notNull(params);
		Assert.notEmpty(params);
		for (Map.Entry entry: params.entrySet()){
			System.out.println(String.format("Param: %s, Type: %s", entry.getKey(), (entry.getValue()).toString()));
		}
		Assert.isTrue(params.containsKey("geneId"));
		Assert.isTrue(params.containsKey("gene.aliases"));
		Assert.isTrue(params.containsKey("gene"));
		Assert.isTrue(params.containsKey("signalOutside"));
		Assert.isTrue(params.get("signalOutside").getEvaluation().equals(Evaluation.OUTSIDE_INCLUSIVE));
	}
	
	@Test
	public void superclassQueryParameterTest() throws Exception {
		Map<String, QueryParameterDescriptor> map = QueryParameterUtil.getAvailableQueryParameters(MongoGene.class);
		Assert.notNull(map);
		Assert.notEmpty(map);
		for (Map.Entry<String, QueryParameterDescriptor> entry: map.entrySet()){
			System.out.println(String.format("param=%s  descriptor=%s", entry.getKey(), entry.getValue().toString()));	
		}
		Assert.isTrue(map.size() == 14, String.format("Expected 14, found %d", map.size()));
	}
	
	@Test
	public void wrappedSuperclassTest() throws Exception {
		BeanWrapperImpl wrapper = new BeanWrapperImpl(MongoGene.class);
		for (PropertyDescriptor descriptor: wrapper.getPropertyDescriptors()){
			System.out.println(descriptor.toString());
		}
	}
	
}
