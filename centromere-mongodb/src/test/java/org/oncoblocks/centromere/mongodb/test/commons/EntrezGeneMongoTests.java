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

package org.oncoblocks.centromere.mongodb.test.commons;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.commons.EntrezGeneRepository;
import org.oncoblocks.centromere.mongodb.commons.MongoEntrezGene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { 
		EmbeddedMongoDataSourceConfig.class, 
		EntrezGeneMongoRepositoryConfig.class 
})
public class EntrezGeneMongoTests {
	
	@Autowired private EntrezGeneRepository entrezGeneRepository;
	
	@Before
	public void setup(){
		
	}
	
	@Test
	public void geneRepositoryTest() throws Exception {
		Assert.notNull(entrezGeneRepository);
		Assert.isTrue(entrezGeneRepository.count() == 0);
		Assert.isTrue(entrezGeneRepository.getModel().equals(MongoEntrezGene.class), 
				String.format("Expected %s, got %s", MongoEntrezGene.class.getName(), entrezGeneRepository.getModel().getName()));
		MongoEntrezGene gene = new MongoEntrezGene();
		gene.setEntrezGeneId(0L);
		gene.setPrimaryGeneSymbol("TEST");
		gene.setTaxId(9606);
		gene.addGeneSymbolAlias("alias");
		entrezGeneRepository.insert(gene);
		Assert.isTrue(entrezGeneRepository.count() == 1, 
				String.format("Expected 1, found %d", entrezGeneRepository.count()));
		List<MongoEntrezGene> genes = entrezGeneRepository.findByEntrezGeneId(0L);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.get(0).getEntrezGeneId().equals(0L));
		genes = entrezGeneRepository.guessGene("alias");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.get(0).getPrimaryGeneSymbol().equals("TEST"));
	}
	
	
}
