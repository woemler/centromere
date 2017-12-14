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

package com.blueprint.centromere.tests.core.test;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.mongodb.model.MongoGene;
import com.blueprint.centromere.core.util.JsonModelConverter;
import com.blueprint.centromere.core.util.KeyValueMapModelConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ModelConverterTests {
	
	@Test
	public void jsonConverterTest() throws Exception {
		String json = "{\"geneId\": 1, \"symbol\": \"ABC\", \"taxId\": 9606}";
		JsonModelConverter converter = new JsonModelConverter(MongoGene.class);
    MongoGene gene = (MongoGene) converter.convert(json);
		Assert.notNull(gene);
		Assert.isTrue("1".equals(gene.getGeneId()));
		Assert.isTrue("ABC".equals(gene.getSymbol()));
		Assert.isNull(gene.getChromosome());
	}
	
	@Test
	public void badJsonConversiontest() throws Exception {
		String json = "{\"geneId\": 1, \"symbol\": \"ABC\", \"badField\": 0}";
		JsonModelConverter converter = new JsonModelConverter(new ObjectMapper(), Gene.class);
		Gene gene = (Gene) converter.convert(json);
		Assert.isNull(gene);
	}
	
	@Test
	public void mapConversionTest() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("geneId", "1");
		map.put("symbol", "ABC");
		map.put("taxId", "9606");
		KeyValueMapModelConverter converter = new KeyValueMapModelConverter(MongoGene.class);
    MongoGene gene = (MongoGene) converter.convert(map);
		Assert.notNull(gene);
		Assert.isTrue("1".equals(gene.getGeneId()));
		Assert.isTrue("ABC".equals(gene.getSymbol()));
		Assert.isNull(gene.getChromosome());
	}

	@Test
	public void badMapConversionTest() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("geneId", "1");
		map.put("symbol", "ABC");
		map.put("badField", "0");
		KeyValueMapModelConverter converter = new KeyValueMapModelConverter(new ObjectMapper(), Gene.class);
		Gene gene = (Gene) converter.convert(map);
		Assert.isNull(gene);
	}
	
}
