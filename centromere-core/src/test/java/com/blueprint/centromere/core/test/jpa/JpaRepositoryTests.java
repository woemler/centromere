/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.test.jpa;

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.test.model.EntrezGeneDataGenerator;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.PathBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmbeddedH2DataSourceConfig.class })
public class JpaRepositoryTests {
	
	@Autowired private GeneRepository geneRepository;
	private final Class<Gene> model = Gene.class;
	private final EntrezGeneDataGenerator dataGenerator = new EntrezGeneDataGenerator();

	@Before
	public void setup() throws Exception {
		geneRepository.deleteAll();
		geneRepository.save(dataGenerator.generateData());
	}

	@Test
	public void findByIdTest(){

		List<Gene> genes = (List<Gene>) geneRepository.findAll();
		
		Gene gene = geneRepository.findOne(genes.get(0).getId());
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("1"));
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		Assert.notNull(gene.getAliases());
		Assert.notEmpty(gene.getAliases());
		Assert.isTrue(gene.getAliases().size() == 1);
		System.out.println("PKID: " + gene.getId().toString());

	}

	@Test
	public void findAllTest(){

		List<Gene> genes = (List<Gene>) geneRepository.findAll();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 5);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("1"));
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		Assert.notNull(gene.getAliases());
		Assert.notEmpty(gene.getAliases());
		Assert.isTrue(gene.getAliases().size() == 1);
		System.out.println(gene.toString());

	}

	@Test
	public void countTest(){
		long count = geneRepository.count();
		Assert.notNull(count);
		Assert.isTrue(count == 5L);
	}
	
	@Test
	public void filteredCountTest(){
		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute = Expressions.stringPath(entity, "geneType");
		Expression<String> variable = Expressions.constant("protein-coding");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute, variable);
		long count = geneRepository.count(predicate);
		Assert.notNull(count);
		Assert.isTrue(count == 3L);
	}

	// gene.primaryGeneSymbol = 'GeneB'
	@Test
	public void findBySimpleCriteriaTest(){
		
		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute = Expressions.stringPath(entity, "primaryGeneSymbol");
		Expression<String> variable = Expressions.constant("GeneB");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute, variable);
		
		List<Gene> genes = (List<Gene>) geneRepository.findAll(predicate);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("2"));
		Assert.isTrue("GeneB".equals(gene.getPrimaryGeneSymbol()));

	}

	// gene.primaryGeneSymbol = 'GeneB,GeneD'
	@Test
	public void findByMultipleSimpleStringCriteriaTest(){

		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute = Expressions.stringPath(entity, "primaryGeneSymbol");
		//Expression<String> variable = Expressions.constant("GeneB");
		Expression<List<String>> variable = Expressions.constant(Arrays.asList("GeneB", "GeneD")); 
		Predicate predicate = Expressions.predicate(Ops.IN, attribute, variable);

		List<Gene> genes = (List<Gene>) geneRepository.findAll(predicate);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);

		Gene gene = genes.get(1);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("4"));
		Assert.isTrue("GeneD".equals(gene.getPrimaryGeneSymbol()));

	}

	// gene.geneType = 'protein-coding' && gene.chromosome = 5
	@Test
	public void findByMultipleCriteriaTest(){

		BooleanBuilder builder = new BooleanBuilder();
		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute1 = Expressions.stringPath(entity, "geneType");
		Expression<String> variable1 = Expressions.constant("protein-coding");
		Predicate predicate1 = Expressions.predicate(Ops.EQ, attribute1, variable1);
		Path<String> attribute2 = Expressions.stringPath(entity, "chromosome");
		Expression<String> variable2 = Expressions.constant("5");
		Predicate predicate2 = Expressions.predicate(Ops.EQ, attribute2, variable2);
		builder.and(predicate1).and(predicate2);

		List<Gene> genes = (List<Gene>) geneRepository.findAll(builder.getValue());
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("2"));
		Assert.isTrue("GeneB".equals(gene.getPrimaryGeneSymbol()));

	}

	// gene.aliases = 'DEF'
	@Test
	public void findByNestedArrayCriteriaTest(){

		PathBuilder<Gene> pathBuilder = new PathBuilder<>(model, model.getSimpleName().toLowerCase());
		ListPath<String, PathBuilder<String>> attribute = pathBuilder.getList("aliases", String.class);
		Expression<String> variable = Expressions.constant("DEF");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute.any(), variable);
		
		List<Gene> genes = (List<Gene>) geneRepository.findAll(predicate);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("2"));
		Assert.isTrue("GeneB".equals(gene.getPrimaryGeneSymbol()));
		Assert.isTrue("DEF".equals(gene.getAliases().get(0)));

	}

	// gene.attributes.isKinase = 'Y'
	@Test
	public void findByNestedObjectCriteriaTest(){

		PathBuilder<Gene> pathBuilder = new PathBuilder<>(model, model.getSimpleName().toLowerCase());
		MapPath<String, String, PathBuilder<String>> attribute 
				= pathBuilder.getMap("attributes", String.class, String.class);
		Expression<String> variable = Expressions.constant("Y");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute.get("isKinase"), variable);
		
		List<Gene> genes = (List<Gene>) geneRepository.findAll(predicate);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("1"));
		Assert.isTrue("GeneA".equals(gene.getPrimaryGeneSymbol()));
		Assert.isTrue(gene.getAttributes().size() == 1);
		Assert.isTrue(gene.getAttributes().containsKey("isKinase"));
		Assert.isTrue("Y".equals(gene.getAttributes().get("isKinase")));

	}

	@Test
	public void findSortedTest(){

		Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
		List<Gene> genes = (List<Gene>) geneRepository.findAll(sort);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 5);
		Assert.isTrue(genes.get(0).getPrimaryReferenceId().equals("5"));

	}


	@Test
	public void findAndSortTest(){
		
		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute = Expressions.stringPath(entity, "geneType");
		Expression<String> variable = Expressions.constant("protein-coding");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute, variable);

		Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
		List<Gene> genes = (List<Gene>) geneRepository.findAll(predicate, sort);
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 3);
		Assert.isTrue(genes.get(0).getPrimaryReferenceId().equals("4"));
	}

	@Test
	public void findPagedTest(){

		PageRequest pageRequest = new PageRequest(1, 2);
		Page<Gene> page = geneRepository.findAll(pageRequest);
		Assert.notNull(page);
		Assert.isTrue(page.getTotalPages() == 3);
		Assert.isTrue(page.getTotalElements() == 5);

		List<Gene> genes = page.getContent();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 2);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("3"));

	}

	@Test
	public void findByCriteriaPagedTest(){

		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute = Expressions.stringPath(entity, "geneType");
		Expression<String> variable = Expressions.constant("protein-coding");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute, variable);
		
		PageRequest pageRequest = new PageRequest(1, 2);
		Page<Gene> page = geneRepository.findAll(predicate, pageRequest);
		Assert.notNull(page);
		Assert.isTrue(page.getTotalElements() == 3);
		Assert.isTrue(page.getTotalPages() == 2);

		List<Gene> genes = page.getContent();
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Assert.isTrue(genes.size() == 1);

		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("4"));

	}

	@Test
	public void insertTest(){
		Gene gene = new Gene();
		gene.setPrimaryReferenceId("100");
		gene.setPrimaryGeneSymbol("TEST");
		gene.setTaxId(9606);
		gene.setChromosome("1");
		gene.setGeneType("protein-coding");
		geneRepository.save(gene);

		Gene created = geneRepository.findByPrimaryReferenceId("100").get(0);
		Assert.notNull(created);
		Assert.isTrue(created.getPrimaryReferenceId().equals("100"));
		Assert.isTrue("TEST".equals(created.getPrimaryGeneSymbol()));

		geneRepository.delete(created);

	}

	@Test
	public void insertMultipleTest(){
		List<Gene> genes = new ArrayList<>();
		Gene gene1 = new Gene();
		gene1.setPrimaryReferenceId("100");
		gene1.setPrimaryGeneSymbol("TEST");
		gene1.setTaxId(9606);
		gene1.setChromosome("1");
		gene1.setGeneType("protein-coding");
		genes.add(gene1);
		Gene gene2 = new Gene();
		gene2.setPrimaryReferenceId("101");
		gene2.setPrimaryGeneSymbol("TEST2");
		gene2.setTaxId(9606);
		gene2.setChromosome("12");
		gene2.setGeneType("pseudo");
		genes.add(gene2);
		geneRepository.save(genes);
		genes = geneRepository.findByPrimaryReferenceId("100");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Gene gene = genes.get(0);
		Assert.notNull(gene);
		genes = geneRepository.findByPrimaryReferenceId("101");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue(geneRepository.count() == 7L);
	}

	@Test
	public void updateTest(){

		Gene gene = new Gene();
		gene.setPrimaryReferenceId("100");
		gene.setPrimaryGeneSymbol("TEST");
		gene.setTaxId(9606);
		gene.setChromosome("1");
		gene.setGeneType("protein-coding");
		geneRepository.save(gene);

		gene.setPrimaryGeneSymbol("TEST_TEST");
		gene.setGeneType("pseudogene");
		geneRepository.save(gene);

		List<Gene> genes = geneRepository.findByPrimaryReferenceId("100");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Gene updated = genes.get(0);
		Assert.notNull(updated);
		Assert.isTrue("TEST_TEST".equals(updated.getPrimaryGeneSymbol()));
		Assert.isTrue("pseudogene".equals(updated.getGeneType()));

	}

	@Test
	public void updateMultipleTest(){
		List<Gene> genes = new ArrayList<>();
		Gene gene1 = new Gene();
		gene1.setPrimaryReferenceId("100");
		gene1.setPrimaryGeneSymbol("TEST");
		gene1.setTaxId(9606);
		gene1.setChromosome("1");
		gene1.setGeneType("protein-coding");
		genes.add(gene1);
		Gene gene2 = new Gene();
		gene2.setPrimaryReferenceId("101");
		gene2.setPrimaryGeneSymbol("TEST2");
		gene2.setTaxId(9606);
		gene2.setChromosome("12");
		gene2.setGeneType("pseudo");
		genes.add(gene2);
		geneRepository.save(genes);
		Assert.isTrue(geneRepository.count() == 7L);

		genes = new ArrayList<>();
		gene1.setGeneType("TEST");
		gene2.setGeneType("TEST");
		genes.add(gene1);
		genes.add(gene2);
		geneRepository.save(genes);

		genes = geneRepository.findByPrimaryReferenceId("100");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Gene gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue("TEST".equals(gene.getGeneType()));
		genes = geneRepository.findByPrimaryReferenceId("101");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		gene = genes.get(0);
		Assert.notNull(gene);
		Assert.isTrue("TEST".equals(gene.getGeneType()));
	}

	@Test
	public void deleteTest(){

		Gene gene = new Gene();
		gene.setPrimaryReferenceId("100");
		gene.setPrimaryGeneSymbol("TEST");
		gene.setTaxId(9606);
		gene.setChromosome("1");
		gene.setGeneType("protein-coding");
		geneRepository.save(gene);

		List<Gene> genes = geneRepository.findByPrimaryReferenceId("100");
		Assert.notNull(genes);
		Assert.notEmpty(genes);
		Gene created = genes.get(0);
		Assert.notNull(created);
		Assert.isTrue(created.getPrimaryReferenceId().equals("100"));

		geneRepository.delete(created);
		genes = geneRepository.findByPrimaryReferenceId("100");
		Assert.notNull(genes);
		Assert.isTrue(genes.isEmpty());

	}

	@Test
	public void distinctTest(){
		Set<Object> geneSymbols = geneRepository.distinct("primaryGeneSymbol");
		Assert.notNull(geneSymbols);
		Assert.notEmpty(geneSymbols);
		Assert.isTrue(geneSymbols.size() == 5);
		Assert.isTrue(geneSymbols.contains("GeneA"));

	}

	@Test
	public void distinctQueryTest(){
		Path<Gene> entity = Expressions.path(model, model.getSimpleName().toLowerCase());
		Path<String> attribute = Expressions.stringPath(entity, "geneType");
		Expression<String> variable = Expressions.constant("protein-coding");
		Predicate predicate = Expressions.predicate(Ops.EQ, attribute, variable);
		Set<Object> geneSymbols = geneRepository.distinct("primaryGeneSymbol", predicate);
		Assert.notNull(geneSymbols);
		Assert.notEmpty(geneSymbols);
		Assert.isTrue(geneSymbols.size() == 3);
		Assert.isTrue(geneSymbols.contains("GeneD"));

	}
	

	@Test
	public void guessGeneTest() throws Exception {

		List<Gene> genes = (List<Gene>) geneRepository.guess("GeneA");
		Assert.notNull(genes);
		Assert.notEmpty(genes);

		Gene gene = genes.get(0);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("1"));

		genes = (List<Gene>) geneRepository.guess("MNO");
		Assert.notNull(genes);
		Assert.notEmpty(genes);

		gene = genes.get(0);
		Assert.isTrue(gene.getPrimaryReferenceId().equals("5"));

		genes = (List<Gene>) geneRepository.guess("XYZ");
		Assert.isTrue(genes.size() == 0);

	}
	
}
