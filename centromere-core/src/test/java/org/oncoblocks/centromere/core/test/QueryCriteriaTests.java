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

package org.oncoblocks.centromere.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.repository.Evaluation;
import org.oncoblocks.centromere.core.repository.QueryCriteria;
import org.oncoblocks.centromere.core.repository.QueryCriteriaBuilder;
import org.oncoblocks.centromere.core.repository.QueryParameterDescriptor;
import org.oncoblocks.centromere.core.util.QueryParameterUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class QueryCriteriaTests {
	
	@Test
	public void criteriaTest(){
		QueryCriteriaBuilder builder = new QueryCriteriaBuilder("key")
				.is("value")
				.and("num")
				.greaterThanOrEqual(3);
		List<QueryCriteria> criterias = builder.build();
		Assert.notNull(criterias);
		Assert.notEmpty(criterias);
		Assert.isTrue(criterias.size() == 2);
		QueryCriteria criteria = criterias.get(0);
		Assert.isTrue("key".equals(criteria.getKey()));
		Assert.isTrue("value".equals(criteria.getValue()));
		Assert.isTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));
		criteria = criterias.get(1);
		Assert.notNull(criteria);
		Assert.isTrue("num".equals(criteria.getKey()));
		Assert.isTrue((int) criteria.getValue() == 3);
		Assert.isTrue(Evaluation.GREATER_THAN_EQUALS.equals(criteria.getEvaluation()));
	}
	
	@Test
	public void standardQueryParameterDescriptorTest(){
		QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
		descriptor.setParamName("param");
		descriptor.setFieldName("field");
		descriptor.setType(Integer.class);
		descriptor.setEvaluation(Evaluation.GREATER_THAN);
		QueryCriteria criteria = descriptor.createQueryCriteria(4);
		Assert.notNull(criteria);
		Assert.isTrue("field".equals(criteria.getKey()));
		Assert.isTrue((int) criteria.getValue() == 4);
		Assert.isTrue(Evaluation.GREATER_THAN.equals(criteria.getEvaluation()));
	}
	
	@Test
	public void regexQueryParameterDescriptorTest(){
		QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
		descriptor.setParamName("attributes.\\w+");
		descriptor.setEvaluation(Evaluation.EQUALS);
		descriptor.setRegexMatch(true);
		Assert.isTrue(descriptor.parameterNameMatches("attributes.isKinase"));
		Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getDynamicEvaluation("attributes.isKinase")), 
				String.format("Expected EQUALS, got %s", descriptor.getDynamicEvaluation("attributes.isKinase").toString()));
		Assert.isTrue(!descriptor.parameterNameMatches("attributes"));
		Assert.isTrue(descriptor.parameterNameMatches("attributes.nameIsNull"));
		Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getDynamicEvaluation("attributes.nameIsNull")));
	}
	
	@Test
	public void modelToDescriptorTest(){
		Map<String,QueryParameterDescriptor> descriptorMap 
				= QueryParameterUtil.getAvailableQueryParameters(EntrezGene.class);
		for (Map.Entry entry: descriptorMap.entrySet()){
			System.out.println(String.format("param: %s   descriptor: %s", entry.getKey(),
					(entry.getValue()).toString()));
		}
		Assert.notNull(descriptorMap);
		Assert.notEmpty(descriptorMap);
		Assert.isTrue(descriptorMap.size() == 9, String.format("Size is actually %s", descriptorMap.size()));
		Assert.isTrue(descriptorMap.containsKey("attributes.\\w+"));
		Assert.isTrue(!descriptorMap.containsKey("attributes"));
		QueryParameterDescriptor descriptor = descriptorMap.get("entrezGeneId");
		Assert.notNull(descriptor);
		Assert.isTrue(descriptor.getType().equals(Long.class));
		descriptor = descriptorMap.get("symbol");
		Assert.notNull(descriptor);
		Assert.isTrue("symbol".equals(descriptor.getParamName()));
		Assert.isTrue("primaryGeneSymbol".equals(descriptor.getFieldName()));
		Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getEvaluation()));
	}
	
}
