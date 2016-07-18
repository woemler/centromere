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

package org.oncoblocks.centromere.mongodb.test.custom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.mongodb.test.EntrezGene;
import org.oncoblocks.centromere.mongodb.test.config.CustomCentromereMongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { 
		CustomCentromereMongoConfig.DatabaseOneConfig.class,
		CustomCentromereMongoConfig.DatabaseTwoConfig.class,
		CustomCentromereMongoConfig.RepositoryConfig.class
})
public class CustomCentromereMongoRepositoryTests {
	
	@Autowired @Qualifier("geneRepositoryOne") CustomEntrezGeneRepository geneRepositoryOne;
	@Autowired @Qualifier("geneRepositoryTwo") CustomEntrezGeneRepository geneRepositoryTwo;
	
	@Before
	public void doBefore(){
		geneRepositoryOne.deleteAll();
		geneRepositoryTwo.deleteAll();
		geneRepositoryOne.insert(EntrezGene.createDummyData());
	}
	
	@Test
	public void dataChecktest() throws Exception {
		Assert.isTrue(geneRepositoryOne.count() > 0);
		Assert.isTrue(geneRepositoryTwo.count() == 0);
	}
	
	@Test
	public void customMethodTest() throws Exception {
		List<EntrezGene> genes = geneRepositoryOne.guessGene("JKL");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryGeneSymbol().equals("GeneD"));
	}
	
}
