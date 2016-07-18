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

package org.oncoblocks.centromere.mongodb.test.basic;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.repository.Evaluation;
import org.oncoblocks.centromere.core.repository.QueryCriteria;
import org.oncoblocks.centromere.core.repository.QueryCriteriaBuilder;
import org.oncoblocks.centromere.mongodb.test.EntrezGene;
import org.oncoblocks.centromere.mongodb.test.config.TestMongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMongoConfig.class })
@FixMethodOrder
public class GenericMongoRepositoryTests {
		
	@Autowired private EntrezGeneRepository geneRepository;
	
	@Before
	public void setup(){
		geneRepository.deleteAll();
		for (EntrezGene gene: EntrezGene.createDummyData()){
			geneRepository.insert(gene);
		}
	}

	@Test
	public void findByIdTest(){
		
		EntrezGene gene = geneRepository.findOne(1L);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(1L));
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		Assert.notNull(gene.getAliases());
		Assert.notEmpty(gene.getAliases());
		Assert.isTrue(gene.getAliases().size() == 1);
		
	}

	@Test
	public void findAllTest(){
		
		List<EntrezGene> genes = geneRepository.findAll();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 5);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(1L));
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		Assert.notNull(gene.getAliases());
		Assert.notEmpty(gene.getAliases());
		Assert.isTrue(gene.getAliases().size() == 1);
		System.out.println(gene.toString());

	}

	@Test
	public void findAndSortTest(){
		List<QueryCriteria> criterias = QueryCriteriaBuilder.where("geneType").is("protein-coding").build();
		Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "entrezGeneId"));
		List<EntrezGene> genes = geneRepository.find(criterias, sort);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 3);
		Assert.isTrue(genes.get(0).getEntrezGeneId().equals(4L));
	}

	@Test
	public void countTest(){
		long count = geneRepository.count();
		Assert.notNull(count);
		Assert.isTrue(count == 5L);
	}

	@Test
	public void filteredCountTest(){
		List<QueryCriteria> criterias = QueryCriteriaBuilder.where("geneType").is("protein-coding").build();
		long count = geneRepository.count(criterias);
		Assert.notNull(count);
		Assert.isTrue(count == 3L);
	}

	@Test
	public void findBySimpleCriteriaTest(){

		List<QueryCriteria> searchCriterias = new ArrayList<>();
		searchCriterias.add(new QueryCriteria("primaryGeneSymbol", "GeneB"));
		List<EntrezGene> genes = geneRepository.find(searchCriterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(2L));
		Assert.isTrue("GeneB".equals(gene.getPrimaryGeneSymbol()));

	}

	@Test
	public void findByNestedArrayCriteriaTest(){

		List<QueryCriteria> searchCriterias = new ArrayList<>();
		searchCriterias.add(new QueryCriteria("aliases", "DEF"));
		List<EntrezGene> genes = geneRepository.find(searchCriterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(2L));
		Assert.isTrue("GeneB".equals(gene.getPrimaryGeneSymbol()));
		Assert.isTrue(gene.getAliases().contains("DEF"));

	}

	@Test
	public void findByNestedObjectCriteriaTest(){

		List<QueryCriteria> searchCriterias = new ArrayList<>();
		searchCriterias.add(new QueryCriteria("attributes.name", "isKinase"));
		searchCriterias.add(new QueryCriteria("attributes.value", "Y"));
		List<EntrezGene> genes = geneRepository.find(searchCriterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(1L));
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		Assert.isTrue(gene.getAttributes().size() == 1);
		Assert.isTrue(gene.getAttributes().get(0).getName().equals("isKinase"));
		Assert.isTrue(gene.getAttributes().get(0).getValue().equals("Y"));

	}

	@Test
	public void findSortedTest(){

		Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "primaryGeneSymbol"));
		List<EntrezGene> genes = geneRepository.findAll(sort);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 5);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(5L));
		Assert.isTrue("GeneE".equals(gene.getPrimaryGeneSymbol()));

	}

	@Test
	public void findPagedTest(){

		PageRequest pageRequest = new PageRequest(1, 2);
		Page<EntrezGene> page = geneRepository.findAll(pageRequest);
		Assert.notNull(page);
		Assert.isTrue(page.getTotalPages() == 3);
		Assert.isTrue(page.getTotalElements() == 5);

		List<EntrezGene> genes = page.getContent();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(3l));

	}

	@Test
	public void findByCriteriaPagedTest(){

		List<QueryCriteria> searchCriterias = new ArrayList<>();
		searchCriterias.add(new QueryCriteria("geneType", "protein-coding"));
		PageRequest pageRequest = new PageRequest(1, 2);
		Page<EntrezGene> page = geneRepository.find(searchCriterias, pageRequest);
		Assert.notNull(page);
		Assert.isTrue(page.getTotalElements() == 3);
		Assert.isTrue(page.getTotalPages() == 2);

		List<EntrezGene> genes = page.getContent();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		EntrezGene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(4L));

	}

	@Test
	public void insertTest(){

		EntrezGene
				gene = new EntrezGene(100L, "TEST", 9606, null, "1", "1", "Test gene", "protein-coding", null, null, null);
		geneRepository.insert(gene);

		EntrezGene created = geneRepository.findOne(100L);
		Assert.notNull(created);
		Assert.isTrue(created.getId().equals(100L));
		Assert.isTrue("TEST".equals(created.getPrimaryGeneSymbol()));

		geneRepository.delete(100L);

	}

	@Test
	public void insertMultipleTest(){
		List<EntrezGene> genes = new ArrayList<>();
		EntrezGene gene1 = new EntrezGene();
		gene1.setEntrezGeneId(100L);
		gene1.setPrimaryGeneSymbol("TEST");
		gene1.setTaxId(9606);
		gene1.setChromosome("1");
		gene1.setChromosomeLocation("1");
		gene1.setDescription("Test gene");
		gene1.setGeneType("protein-coding");
		genes.add(gene1);
		EntrezGene gene2 = new EntrezGene();
		gene2.setEntrezGeneId(101L);
		gene2.setPrimaryGeneSymbol("TEST2");
		gene2.setTaxId(9606);
		gene2.setChromosome("12");
		gene2.setChromosomeLocation("12");
		gene2.setDescription("Test gene 2");
		gene2.setGeneType("pseudo");
		genes.add(gene2);
		geneRepository.insert(genes);
		EntrezGene gene = geneRepository.findOne(100L);
		Assert.notNull(gene);
		gene = geneRepository.findOne(101L);
		Assert.notNull(gene);
		Assert.isTrue(geneRepository.count() == 7L);
	}

	@Test
	public void updateTest(){

		EntrezGene
				gene = new EntrezGene(100L, "TEST", 9606, null, "1", "1", "Test gene", "protein-coding", null, null, null);
		geneRepository.insert(gene);

		gene.setPrimaryGeneSymbol("TEST_TEST");
		gene.setGeneType("pseudogene");
		geneRepository.update(gene);

		EntrezGene updated = geneRepository.findOne(100L);
		Assert.notNull(updated);
		Assert.isTrue("TEST_TEST".equals(updated.getPrimaryGeneSymbol()));
		Assert.isTrue("pseudogene".equals(updated.getGeneType()));

		geneRepository.delete(100L);

	}

	@Test
	public void updateMultipleTest(){
		List<EntrezGene> genes = new ArrayList<>();
		EntrezGene gene1 = new EntrezGene();
		gene1.setEntrezGeneId(100L);
		gene1.setPrimaryGeneSymbol("TEST");
		gene1.setTaxId(9606);
		gene1.setChromosome("1");
		gene1.setChromosomeLocation("1");
		gene1.setDescription("Test gene");
		gene1.setGeneType("protein-coding");
		genes.add(gene1);
		EntrezGene gene2 = new EntrezGene();
		gene2.setEntrezGeneId(101L);
		gene2.setPrimaryGeneSymbol("TEST2");
		gene2.setTaxId(9606);
		gene2.setChromosome("12");
		gene2.setChromosomeLocation("12");
		gene2.setDescription("Test gene 2");
		gene2.setGeneType("pseudo");
		genes.add(gene2);
		geneRepository.insert(genes);
		Assert.isTrue(geneRepository.count() == 7L);

		genes = new ArrayList<>();
		gene1.setGeneType("TEST");
		gene2.setGeneType("TEST");
		genes.add(gene1);
		genes.add(gene2);
		geneRepository.update(genes);

		EntrezGene gene = geneRepository.findOne(100L);
		Assert.notNull(gene);
		Assert.isTrue("TEST".equals(gene.getGeneType()));
		gene = geneRepository.findOne(101L);
		Assert.notNull(gene);
		Assert.isTrue("TEST".equals(gene.getGeneType()));
	}

	@Test
	public void deleteTest(){

		EntrezGene
				gene = new EntrezGene(100L, "TEST", 9606, null, "1", "1", "Test gene", "protein-coding", null, null, null);
		geneRepository.insert(gene);

		EntrezGene created = geneRepository.findOne(100L);
		Assert.notNull(created);
		Assert.isTrue(created.getId().equals(100L));

		geneRepository.delete(100L);
		EntrezGene deleted = geneRepository.findOne(100L);
		Assert.isNull(deleted);

	}
	
	@Test
	public void distinctTest(){
		
		List<Object> geneSymbols = geneRepository.distinct("primaryGeneSymbol");
		Assert.notNull(geneSymbols);
		Assert.notEmpty(geneSymbols);
		Assert.isTrue(geneSymbols.size() == 5);
		String symbol = (String) geneSymbols.get(0);
		Assert.isTrue("GeneA".equals(symbol));
		
	}
	
	@Test
	public void distinctQueryTest(){
		
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("geneType", "protein-coding"));
		List<Object> geneSymbols = geneRepository.distinct("primaryGeneSymbol", criterias);
		Assert.notNull(geneSymbols);
		Assert.notEmpty(geneSymbols);
		Assert.isTrue(geneSymbols.size() == 3);
		String symbol = (String) geneSymbols.get(2);
		Assert.isTrue("GeneD".equals(symbol));
		
	}
	
	
	/* EntrezGene repository-specific tests */

	@Test
	public void findByPrimaryIdTest() throws Exception {
		
		EntrezGene gene = geneRepository.findByEntrezGeneId(1L);
		Assert.notNull(gene);
		Assert.isTrue(gene.getEntrezGeneId().equals(1L));
		
	}
	
	@Test
	public void findByPrimaryGeneSymbolTest() throws Exception {
		
		List<EntrezGene> genes = geneRepository.findByPrimaryGeneSymbol("GeneA");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);
		EntrezGene gene = genes.get(0);
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		
	}

	@Test
	public void findByAliasTest() throws Exception {

		List<EntrezGene> genes = geneRepository.findByAlias("ABC");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);
		EntrezGene gene = genes.get(0);
		Assert.isTrue(gene.getEntrezGeneId().equals(1L));

	}
	
	@Test
	public void guessGeneTest() throws Exception {
		
		List<EntrezGene> genes = geneRepository.guessGene("GeneA");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		
		EntrezGene gene = genes.get(0);
		Assert.isTrue(gene.getEntrezGeneId().equals(1L));
		
		genes = geneRepository.guessGene("MNO");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		
		gene = genes.get(0);
		Assert.isTrue(gene.getEntrezGeneId().equals(5L));
		
		genes = geneRepository.guessGene("XYZ");
		Assert.isNull(genes);
		
	}

	/* Query operation tests */
	
	@Test
	public void findInTest() throws Exception {
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("primaryGeneSymbol", Arrays.asList(new String[]{ "GeneA", "GeneB" }), Evaluation.IN));
		List<EntrezGene> genes = geneRepository.find(criterias);
		System.out.println(genes);
		Assert.notNull(genes);
		Assert.isTrue(genes.size() == 2);
		Assert.isTrue("GeneB".equals(genes.get(1).getPrimaryGeneSymbol()));
	}
	
	@Test
	public void findByBetweenTest() throws Exception {
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("entrezGeneId", new ArrayList<Long>(Arrays.asList(new Long[]{2L, 4L})),
				Evaluation.BETWEEN));
		List<EntrezGene> genes = geneRepository.find(criterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);
		Assert.isTrue(genes.get(0).getEntrezGeneId().equals(3L));
	}

	@Test
	public void findByTypeAndBetweenTest() throws Exception {

		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("geneType", "pseudo"));
		criterias.add(new QueryCriteria("entrezGeneId", new ArrayList<Long>(Arrays.asList(new Long[]{2L, 5L})),
				Evaluation.BETWEEN));
		List<EntrezGene> genes = geneRepository.find(criterias);

		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);
		Assert.isTrue(genes.get(0).getEntrezGeneId().equals(3L));
	}

	@Test
	public void findByOutsideTest() throws Exception {
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("entrezGeneId", new ArrayList<Long>(Arrays.asList(new Long[]{2L, 4L})),
				Evaluation.OUTSIDE));
		List<EntrezGene> genes = geneRepository.find(criterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);
		Assert.isTrue(genes.get(1).getEntrezGeneId().equals(5L));
	}

	@Test
	public void findByTypeAndOutsideTest() throws Exception {
		
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("geneType", "protein-coding"));
		criterias.add(new QueryCriteria("entrezGeneId", new ArrayList<Long>(Arrays.asList(new Long[]{2L, 3L})),
				Evaluation.OUTSIDE));
		List<EntrezGene> genes = geneRepository.find(criterias);
		
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);
		Assert.isTrue(genes.get(1).getEntrezGeneId().equals(4L));
	}
	
	@Test
	public void findByLikeTest() throws Exception {
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("primaryGeneSymbol", "eC", Evaluation.LIKE));
		List<EntrezGene> genes = geneRepository.find(criterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);
		EntrezGene gene = genes.get(0);
		Assert.isTrue("GeneC".equals(gene.getPrimaryGeneSymbol()));
	}

	@Test
	public void findByStartsWithTest() throws Exception {
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("geneType", "protein", Evaluation.STARTS_WITH));
		List<EntrezGene> genes = geneRepository.find(criterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 3);
		EntrezGene gene = genes.get(0);
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
	}

	@Test
	public void findByEndsWithTest() throws Exception {
		List<QueryCriteria> criterias = new ArrayList<>();
		criterias.add(new QueryCriteria("primaryGeneSymbol", "neD", Evaluation.ENDS_WITH));
		List<EntrezGene> genes = geneRepository.find(criterias);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);
		EntrezGene gene = genes.get(0);
		Assert.isTrue("GeneD".equals(gene.getPrimaryGeneSymbol()));
	}

	@Test
	public void modelSupportTest(){
		Assert.notNull(geneRepository.getModel());
		Assert.isTrue(EntrezGene.class.equals(geneRepository.getModel()));
	}
	
}
