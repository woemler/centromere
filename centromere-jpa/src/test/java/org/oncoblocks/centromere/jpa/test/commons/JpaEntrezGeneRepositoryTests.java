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

package org.oncoblocks.centromere.jpa.test.commons;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.testing.EntrezGeneDataGenerator;
import org.oncoblocks.centromere.jpa.commons.JpaEntrezGene;
import org.oncoblocks.centromere.jpa.commons.JpaEntrezGeneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaCommonsTestConfig.class })
@FixMethodOrder
public class JpaEntrezGeneRepositoryTests {
	
	@Autowired private JpaEntrezGeneRepository geneRepository;
	@Autowired private EntityManager entityManager;
	
	@Before
	public void setup() throws Exception{
		deleteAll();
		insertDummyData();
	}
	
	@Transactional
	private void deleteAll(){
		geneRepository.deleteAll();
	}
	
	private void insertDummyData() throws Exception {
		for (JpaEntrezGene gene: EntrezGeneDataGenerator.generateDummyData(JpaEntrezGene.class)){
			insertRecord(gene);
		}
	}
	
	@Transactional
	private JpaEntrezGene insertRecord(JpaEntrezGene record) throws Exception {
		return geneRepository.insert(record);
	}
	
	@Test
	@Transactional(readOnly = true)
	public void jpaGeneRepositoryTest() throws Exception {
		Assert.notNull(geneRepository);
		Assert.isTrue(geneRepository.count() == 5);
		Assert.isTrue(geneRepository.getModel().equals(JpaEntrezGene.class),
				String.format("Expected %s, got %s", JpaEntrezGene.class.getName(),
						geneRepository.getModel().getName()));

		for (JpaEntrezGene gene : geneRepository.findAll()) {
			System.out.println(gene.toString());
		}

	}
	
	@Test
	@Transactional(readOnly = true)
	public void jpaRepositoryCustomSearchTest() throws Exception {
		List<JpaEntrezGene> genes = geneRepository.findByPrimaryGeneSymbol("GeneA");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		JpaEntrezGene gene = genes.get(0);
		Assert.notNull(gene.getId());
		Assert.isTrue(gene.getPrimaryGeneSymbol().equals("GeneA"));
		Assert.notNull(gene.getGeneSymbolAliases());
		Assert.notEmpty(gene.getGeneSymbolAliases());
		Assert.isTrue(((List<String>) gene.getGeneSymbolAliases()).get(0).equals("ABC"));
		Assert.notNull(gene.getAttributes());
		Assert.notEmpty(gene.getAttributes());
		Assert.isTrue(gene.getAttributes().get("isKinase").equals("Y"));
	}

	@Test
	@Transactional(readOnly = true)
	public void jpaRepositoryInsertTest() throws Exception {
		JpaEntrezGene gene = new JpaEntrezGene();
		gene.setEntrezGeneId(0L);
		gene.setPrimaryGeneSymbol("TEST");
		gene.setTaxId(9606);
		gene.addGeneSymbolAlias("alias");
		gene.setChromosome("11");
		gene = insertRecord(gene);
		Assert.notNull(gene);
		Assert.notNull(gene.getId());

	}

	@Test
	@Transactional(readOnly = true)
	public void jpaRepositoryGuessGeneTest() throws Exception {
		List<JpaEntrezGene> genes = (List<JpaEntrezGene>) geneRepository.findAll();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue("GeneA".equals(genes.get(0).getPrimaryGeneSymbol()));
		Assert.isTrue("ABC".equals(genes.get(0).getJpaEntrezGeneSymbolAliases().get(0).getSymbol()));
		
		genes = geneRepository.guessGene("GeneA");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.get(0).getPrimaryGeneSymbol().equals("GeneA"));
		
	}
	
}
