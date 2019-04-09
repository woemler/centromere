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

package com.blueprint.centromere.tests.mongodb.test;

import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.models.Gene;
import com.blueprint.centromere.tests.core.models.GeneExpression;
import com.blueprint.centromere.tests.core.models.User;
import com.blueprint.centromere.tests.core.repositories.GeneExpressionRepository;
import com.blueprint.centromere.tests.core.repositories.GeneRepository;
import com.blueprint.centromere.tests.core.repositories.UserRepository;
import com.blueprint.centromere.tests.mongodb.EmbeddedMongoDataSourceConfig;
import com.blueprint.centromere.tests.mongodb.MongoRepositoryConfig;
import com.blueprint.centromere.tests.mongodb.models.MongoGene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
    EmbeddedMongoDataSourceConfig.class,
    MongoRepositoryConfig.class
})
public class GenericMongoRepositoryTests extends AbstractRepositoryTests {

  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository expressionRepository;
  @Autowired private UserRepository userRepository;

  @Test
  public void findByIdByBadIdTest() {
    Optional<Gene> optional = geneRepository.findById("abc");
    Assert.assertTrue(!optional.isPresent());
  }

  @Test
  public void findByIdTest() {

    List<Gene> genes = (List<Gene>) geneRepository.findAll();

    Optional<Gene> optional = geneRepository.findById(genes.get(0).getId());
    Assert.assertTrue(optional.isPresent());
    Gene gene = optional.get();
    Assert.assertEquals(gene.getId(), genes.get(0).getId());
    Assert.assertEquals("GeneA", gene.getSymbol());
    Assert.assertNotNull(gene.getAliases());
    Assert.assertTrue(!gene.getAliases().isEmpty());
    Assert.assertEquals(gene.getAliases().size(), 1);
    System.out.println("PKID: " + gene.getId());

  }

  @Test
  public void findAllTest() {

    List<Gene> genes = (List<Gene>) geneRepository.findAll();
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 5);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertEquals((long) gene.getEntrezGeneId(), 1);
    Assert.assertEquals("GeneA", gene.getSymbol());
    Assert.assertNotNull(gene.getAliases());
    Assert.assertTrue(!gene.getAliases().isEmpty());
    Assert.assertEquals(gene.getAliases().size(), 1);
    System.out.println(gene.toString());

  }

  @Test
  public void countTest() {
    long count = geneRepository.count();
    Assert.assertNotNull(count);
    Assert.assertEquals(count, 5L);
  }

  @Test
  public void filteredCountQueryCriteriaTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");
    long count = geneRepository.count(Collections.singleton(criteria));
    Assert.assertNotNull(count);
    Assert.assertEquals(count, 3L);
  }

  @Test
  public void findBySimpleParamQueryCriteriaTest() {
    QueryCriteria criteria = new QueryCriteria("symbol", "GeneB");
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.assertNotNull(genes);
    Assert.assertNotNull(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 1);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertEquals((long) gene.getEntrezGeneId(), 2);
    Assert.assertEquals("GeneB", gene.getSymbol());

  }

  @Test
  public void findByMultipleParamsQueryCriteriaTest() {

    List<QueryCriteria> criterias = new ArrayList<>();
    criterias.add(new QueryCriteria("geneType", "protein-coding"));
    criterias.add(new QueryCriteria("chromosome", "5"));

    List<Gene> genes = (List<Gene>) geneRepository.find(criterias);
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 1);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertEquals((long) gene.getEntrezGeneId(), 2);
    Assert.assertEquals("GeneB", gene.getSymbol());

  }

  @Test
  public void findByNestedArrayParamsQueryCriteriaTest() {

    QueryCriteria criteria = new QueryCriteria("aliases", "DEF");
  
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 1);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertEquals((long) gene.getEntrezGeneId(), 2);
    Assert.assertEquals("GeneB", gene.getSymbol());
    Assert.assertEquals("DEF", gene.getAliases().get(0));

  }

  @Test
  public void findByNestedObjectParamsQueryCriteriaTest() {

    QueryCriteria criteria = new QueryCriteria("attributes.isKinase", "Y");
    
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertTrue(genes.size() == 2);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertTrue(gene.getEntrezGeneId().equals(1));
    Assert.assertTrue("GeneA".equals(gene.getSymbol()));
    Assert.assertTrue(gene.getAttributes().size() == 1);
    Assert.assertTrue(gene.getAttributes().containsKey("isKinase"));
    Assert.assertTrue("Y".equals(gene.getAttributes().get("isKinase")));

  }

  @Test
  public void findSortedTest() {
    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "entrezGeneId"));
    List<Gene> genes = (List<Gene>) geneRepository.findAll(sort);
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertTrue(genes.size() == 5);
    Assert.assertTrue(genes.get(0).getEntrezGeneId().equals(5));
  }

  @Test
  public void findAndSortTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");
    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "entrezGeneId"));
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singletonList(criteria), sort);
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertTrue(genes.size() == 3);
    Assert.assertTrue(genes.get(0).getEntrezGeneId().equals(4));
  }

  @Test
  public void findPagedTest() {

    PageRequest pageRequest = new PageRequest(1, 2);
    Page<Gene> page = geneRepository.findAll(pageRequest);
    Assert.assertNotNull(page);
    Assert.assertTrue(page.getTotalPages() == 3);
    Assert.assertTrue(page.getTotalElements() == 5);

    List<Gene> genes = page.getContent();
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertTrue(genes.size() == 2);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertTrue(gene.getEntrezGeneId().equals(3));

  }

  @Test
  public void findByParamsQueryCriteriaPagedTest() {

    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");

    PageRequest pageRequest = new PageRequest(1, 2);
    Page<Gene> page = geneRepository.find(Collections.singletonList(criteria), pageRequest);
    Assert.assertNotNull(page);
    Assert.assertTrue(page.getTotalElements() == 3);
    Assert.assertTrue(page.getTotalPages() == 2);

    List<Gene> genes = page.getContent();
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertTrue(genes.size() == 1);

    Gene gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertTrue(gene.getEntrezGeneId().equals(4));

  }
  
  @Test
  public void findByCriteriaNotEqualsTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding", Evaluation.NOT_EQUALS);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 2);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneC", gene.getSymbol());
  }

  @Test
  public void findByCriteriaInTest() {
    QueryCriteria criteria = new QueryCriteria("symbol", Arrays.asList("GeneA", "GeneB"), Evaluation.IN);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 2);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneA", gene.getSymbol());
  }

  @Test
  public void findByCriteriaNotInTest() {
    QueryCriteria criteria = new QueryCriteria("symbol", Arrays.asList("GeneA", "GeneB"), Evaluation.NOT_IN);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 3);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneC", gene.getSymbol());
  }

  @Test
  public void findByCriteriaLikeTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein", Evaluation.LIKE);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 3);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneA", gene.getSymbol());
  }

  @Test
  public void findByCriteriaNotLikeTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein", Evaluation.NOT_LIKE);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 2);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneC",gene.getSymbol());
  }

  @Test
  public void findByCriteriaStartsWithTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein", Evaluation.STARTS_WITH);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 3);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneA", gene.getSymbol());
  }

  @Test
  public void findByCriteriaEndsWithTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "coding", Evaluation.ENDS_WITH);
    List<Gene> genes = (List<Gene>) geneRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 3);
    Gene gene = genes.get(0);
    Assert.assertEquals("GeneA", gene.getSymbol());
  }
  
  @Test
  public void findByNumberGreaterThanTest() {
    QueryCriteria criteria = new QueryCriteria("value", 5.0, Evaluation.GREATER_THAN);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 3);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(6.78));
  }

  @Test
  public void findByNumberGreaterThanOrEqualsTest() {
    QueryCriteria criteria = new QueryCriteria("value", 4.56, Evaluation.GREATER_THAN_EQUALS);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 4);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(4.56));
  }

  @Test
  public void findByNumberLessThanTest() {
    QueryCriteria criteria = new QueryCriteria("value", 5.0, Evaluation.LESS_THAN);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 3);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(1.23));
  }

  @Test
  public void findByNumberLessThanOrEqualsTest() {
    QueryCriteria criteria = new QueryCriteria("value", 4.56, Evaluation.LESS_THAN_EQUALS);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 3);
    GeneExpression record = records.get(2);
    Assert.assertEquals(record.getValue(), Double.valueOf(4.56));
  }

  @Test
  public void findByNumberBetweenTest() {
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(3.0, 7.0), Evaluation.BETWEEN);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 2);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(4.56));
  }

  @Test
  public void findByNumberOutsideTest() {
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(3.0, 7.0), Evaluation.OUTSIDE);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 4);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(1.23));
  }

  @Test
  public void findByNumberEqualsTest() {
    QueryCriteria criteria = new QueryCriteria("value", 4.56, Evaluation.EQUALS);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 1);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(4.56));
  }

  @Test
  public void findByNumberInTest() {
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(2.34, 4.56), Evaluation.IN);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 2);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(2.34));
  }

  @Test
  public void findByNumberNotInTest() {
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(2.34, 4.56), Evaluation.NOT_IN);
    List<GeneExpression> records = (List<GeneExpression>) expressionRepository.find(Collections.singletonList(criteria));
    Assert.assertNotNull(records);
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 4);
    GeneExpression record = records.get(0);
    Assert.assertEquals(record.getValue(), Double.valueOf(1.23));
  }

  @Test
  public void findByNumberNotInTest2() {
    QueryCriteria criteria = new QueryCriteria("taxId", Arrays.asList(9606, 1000), Evaluation.NOT_IN);
    List<Gene> records = (List<Gene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.assertNotNull(records);
    Assert.assertEquals(records.size(), 0);
  }
  
  @Test
  public void findByAttributeNullTest() {
    QueryCriteria criteria = new QueryCriteria("chromosomeLocation", true, Evaluation.IS_NULL);
    List<Gene> records = (List<Gene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 5);
    Gene gene = records.get(0);
    Assert.assertNull(gene.getChromosomeLocation());
  }

  @Test
  public void findByAttributeNotNullTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", true, Evaluation.NOT_NULL);
    List<Gene> records = (List<Gene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 5);
    Gene gene = records.get(0);
    Assert.assertNotNull(gene.getGeneType());
  }

  @Test
  public void findByBooleanTrueTest() {
    QueryCriteria criteria = new QueryCriteria("enabled", true, Evaluation.IS_TRUE);
    List<User> records = (List<User>) userRepository.find(Collections.singletonList(criteria));
    Assert.assertTrue(!records.isEmpty());
    Assert.assertEquals(records.size(), 1);
    User user = records.get(0);
    Assert.assertTrue(user.isEnabled());
  }

  @Test
  public void findByBooleanFalseTest() {
    QueryCriteria criteria = new QueryCriteria("enabled", true, Evaluation.IS_FALSE);
    List<User> records = (List<User>) userRepository.find(Collections.singletonList(criteria));
    Assert.assertTrue(records.isEmpty());
  }

  @Test
  public void insertTest() {
    Gene gene = new MongoGene();
    gene.setEntrezGeneId(100);
    gene.setSymbol("TEST");
    gene.setTaxId(9606);
    gene.setChromosome("1");
    gene.setGeneType("protein-coding");
    geneRepository.save(gene);

    Gene created = (Gene) geneRepository.findByEntrezGeneId(100).get();
    Assert.assertNotNull(created);
    Assert.assertEquals((long) created.getEntrezGeneId(),100);
    Assert.assertEquals("TEST", created.getSymbol());

    geneRepository.delete(created);

  }

  @Test
  public void insertMultipleTest() {
    List<Gene> genes = new ArrayList<>();
    Gene gene1 = new MongoGene();
    gene1.setEntrezGeneId(100);
    gene1.setSymbol("TEST");
    gene1.setTaxId(9606);
    gene1.setChromosome("1");
    gene1.setGeneType("protein-coding");
    genes.add(gene1);
    Gene gene2 = new MongoGene();
    gene2.setEntrezGeneId(101);
    gene2.setSymbol("TEST2");
    gene2.setTaxId(9606);
    gene2.setChromosome("12");
    gene2.setGeneType("pseudo");
    genes.add(gene2);
    geneRepository.insert(genes);
    Optional<Gene> optional = geneRepository.findByEntrezGeneId(100);
    Assert.assertNotNull(optional);
    Assert.assertTrue(optional.isPresent());
    Gene gene = optional.get();
    Assert.assertNotNull(gene);
    optional = geneRepository.findByEntrezGeneId(101);
    Assert.assertNotNull(genes);
    Assert.assertTrue(optional.isPresent());
    gene = genes.get(0);
    Assert.assertNotNull(gene);
    Assert.assertTrue(geneRepository.count() == 7L);
  }

  @Test
  public void updateTest() {

    Gene gene = new MongoGene();
    gene.setEntrezGeneId(100);
    gene.setSymbol("TEST");
    gene.setTaxId(9606);
    gene.setChromosome("1");
    gene.setGeneType("protein-coding");
    geneRepository.save(gene);

    gene.setSymbol("TEST_TEST");
    gene.setGeneType("pseudogene");
    geneRepository.save(gene);

    Optional<Gene> optional = geneRepository.findByEntrezGeneId(100);
    Assert.assertNotNull(optional);
    Assert.assertTrue(optional.isPresent());
    Gene updated = optional.get();
    Assert.assertNotNull(updated);
    Assert.assertTrue("TEST_TEST".equals(updated.getSymbol()));
    Assert.assertTrue("pseudogene".equals(updated.getGeneType()));

  }

  @Test
  public void updateMultipleTest() {
    List<Gene> genes = new ArrayList<>();
    Gene gene1 = new MongoGene();
    gene1.setEntrezGeneId(100);
    gene1.setSymbol("TEST");
    gene1.setTaxId(9606);
    gene1.setChromosome("1");
    gene1.setGeneType("protein-coding");
    genes.add(gene1);
    Gene gene2 = new MongoGene();
    gene2.setEntrezGeneId(101);
    gene2.setSymbol("TEST2");
    gene2.setTaxId(9606);
    gene2.setChromosome("12");
    gene2.setGeneType("pseudo");
    genes.add(gene2);
    geneRepository.saveAll(genes);
    Assert.assertTrue(geneRepository.count() == 7L);

    genes = new ArrayList<>();
    gene1.setGeneType("TEST");
    gene2.setGeneType("TEST");
    genes.add(gene1);
    genes.add(gene2);
    geneRepository.saveAll(genes);

    Optional<Gene> optional = geneRepository.findByEntrezGeneId(100);
    Assert.assertNotNull(optional);
    Assert.assertTrue(optional.isPresent());
    Gene gene = optional.get();
    Assert.assertNotNull(gene);
    Assert.assertTrue("TEST".equals(gene.getGeneType()));
    optional = geneRepository.findByEntrezGeneId(101);
    Assert.assertNotNull(optional);
    Assert.assertTrue(optional.isPresent());
    gene = optional.get();
    Assert.assertNotNull(gene);
    Assert.assertTrue("TEST".equals(gene.getGeneType()));
  }

  @Test
  public void deleteTest() {

    Gene gene = new MongoGene();
    gene.setEntrezGeneId(100);
    gene.setSymbol("TEST");
    gene.setTaxId(9606);
    gene.setChromosome("1");
    gene.setGeneType("protein-coding");
    geneRepository.save(gene);

    Optional<Gene> optional = geneRepository.findByEntrezGeneId(100);
    Assert.assertNotNull(optional);
    Assert.assertTrue(optional.isPresent());
    Gene created = optional.get();
    Assert.assertNotNull(created);
    Assert.assertTrue(created.getEntrezGeneId().equals(100));

    geneRepository.delete(created);
    optional = geneRepository.findByEntrezGeneId(100);
    Assert.assertNotNull(optional);
    Assert.assertTrue(!optional.isPresent());

  }

  @Test
  public void distinctTest() {
    Set<Object> geneSymbols = geneRepository.distinct("symbol");
    Assert.assertNotNull(geneSymbols);
    Assert.assertTrue(!geneSymbols.isEmpty());
    Assert.assertTrue(geneSymbols.size() == 5);
    Assert.assertTrue(geneSymbols.contains("GeneA"));
  }

  @Test
  public void distinctQueryCriteriaTest() {
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");
    Set<Object> geneSymbols = geneRepository.distinct("symbol", Collections.singletonList(criteria));
    Assert.assertNotNull(geneSymbols);
    Assert.assertTrue(!geneSymbols.isEmpty());
    Assert.assertTrue(geneSymbols.size() == 3);
    Assert.assertTrue(geneSymbols.contains("GeneD"));
  }

  @Test
  public void guessGeneTest() throws Exception {

    List<Gene> genes = geneRepository.guess("GeneA");
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());

    Gene gene = genes.get(0);
    Assert.assertTrue(gene.getEntrezGeneId().equals(1));

    genes = geneRepository.guess("MNO");
    Assert.assertNotNull(genes);
    Assert.assertTrue(!genes.isEmpty());

    gene = genes.get(0);
    Assert.assertTrue(gene.getEntrezGeneId().equals(5));

    genes = geneRepository.guess("XYZ");
    Assert.assertTrue(genes.size() == 0);

  }

}
