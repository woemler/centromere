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

package com.blueprint.centromere.tests.core.test.repository;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.mongodb.MongoConfiguration;
import com.blueprint.centromere.core.mongodb.model.MongoGene;
import com.blueprint.centromere.core.mongodb.model.MongoGeneExpression;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneExpressionRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneRepository;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
    MongoDataSourceConfig.class,
    CoreConfiguration.CommonConfiguration.class,
    MongoConfiguration.MongoRepositoryConfiguration.class
})
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT })
public class GenericMongoRepositoryTests extends AbstractRepositoryTests {

  @Autowired private MongoGeneRepository geneRepository;
  @Autowired private MongoGeneExpressionRepository expressionRepository;

  @Test
  public void findOneByBadIdTest(){
    Gene gene = geneRepository.findOne("abc");
    Assert.isNull(gene);
  }

  @Test
  public void findByIdTest(){

    List<MongoGene> genes = (List<MongoGene>) geneRepository.findAll();

    Optional<MongoGene> optional = geneRepository.findById(genes.get(0).getId());
    Assert.isTrue(optional.isPresent());
    Gene gene = optional.get();
    Assert.isTrue(gene.getGeneId().equals("1"));
    Assert.isTrue("GeneA".equals(gene.getSymbol()));
    Assert.notNull(gene.getAliases());
    Assert.notEmpty(gene.getAliases());
    Assert.isTrue(gene.getAliases().size() == 1);
    System.out.println("PKID: " + gene.getId());

  }

  @Test
  public void findAllTest(){

    List<MongoGene> genes = (List<MongoGene>) geneRepository.findAll();
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 5);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("1"));
    Assert.isTrue("GeneA".equals(gene.getSymbol()));
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
  public void filteredCountQueryCriteriaTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");
    long count = geneRepository.count(Collections.singleton(criteria));
    Assert.notNull(count);
    Assert.isTrue(count == 3L);
  }

  @Test
  public void findBySimpleParamQueryCriteriaTest(){
    QueryCriteria criteria = new QueryCriteria("symbol", "GeneB");
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 1);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("2"));
    Assert.isTrue("GeneB".equals(gene.getSymbol()));

  }

  @Test
  public void findByMultipleParamsQueryCriteriaTest(){

    List<QueryCriteria> criterias = new ArrayList<>();
    criterias.add(new QueryCriteria("geneType", "protein-coding"));
    criterias.add(new QueryCriteria("chromosome", "5"));

    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(criterias);
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 1);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("2"));
    Assert.isTrue("GeneB".equals(gene.getSymbol()));

  }

  @Test
  public void findByNestedArrayParamsQueryCriteriaTest(){

    QueryCriteria criteria = new QueryCriteria("aliases", "DEF");
  
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 1);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("2"));
    Assert.isTrue("GeneB".equals(gene.getSymbol()));
    Assert.isTrue("DEF".equals(gene.getAliases().get(0)));

  }

  @Test
  public void findByNestedObjectParamsQueryCriteriaTest(){

    QueryCriteria criteria = new QueryCriteria("attributes.isKinase", "Y");
    
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 2);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("1"));
    Assert.isTrue("GeneA".equals(gene.getSymbol()));
    Assert.isTrue(gene.getAttributes().size() == 1);
    Assert.isTrue(gene.getAttributes().containsKey("isKinase"));
    Assert.isTrue("Y".equals(gene.getAttributes().get("isKinase")));

  }

  @Test
  public void findSortedTest(){
    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "geneId"));
    List<MongoGene> genes = (List<MongoGene>) geneRepository.findAll(sort);
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 5);
    Assert.isTrue(genes.get(0).getGeneId().equals("5"));
  }

  @Test
  public void findAndSortTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");
    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "geneId"));
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singletonList(criteria), sort);
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 3);
    Assert.isTrue(genes.get(0).getGeneId().equals("4"));
  }

  @Test
  public void findPagedTest(){

    PageRequest pageRequest = new PageRequest(1, 2);
    Page<MongoGene> page = geneRepository.findAll(pageRequest);
    Assert.notNull(page);
    Assert.isTrue(page.getTotalPages() == 3);
    Assert.isTrue(page.getTotalElements() == 5);

    List<MongoGene> genes = page.getContent();
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 2);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("3"));

  }

  @Test
  public void findByParamsQueryCriteriaPagedTest(){

    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");

    PageRequest pageRequest = new PageRequest(1, 2);
    Page<MongoGene> page = geneRepository.find(Collections.singletonList(criteria), pageRequest);
    Assert.notNull(page);
    Assert.isTrue(page.getTotalElements() == 3);
    Assert.isTrue(page.getTotalPages() == 2);

    List<MongoGene> genes = page.getContent();
    Assert.notNull(genes);
    Assert.notEmpty(genes);
    Assert.isTrue(genes.size() == 1);

    Gene gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(gene.getGeneId().equals("4"));

  }
  
  @Test
  public void findByCriteriaNotEqualsTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding", Evaluation.NOT_EQUALS);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 2, "Expected result set size of 2");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneC".equals(gene.getSymbol()), "Record does not have expected value");
  }

  @Test
  public void findByCriteriaInTest(){
    QueryCriteria criteria = new QueryCriteria("symbol", Arrays.asList("GeneA", "GeneB"), Evaluation.IN);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 2, "Expected result set size of 2");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneA".equals(gene.getSymbol()), "Record does not have expected value");
  }

  @Test
  public void findByCriteriaNotInTest(){
    QueryCriteria criteria = new QueryCriteria("symbol", Arrays.asList("GeneA", "GeneB"), Evaluation.NOT_IN);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 3, "Expected result set size of 3");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneC".equals(gene.getSymbol()), "Record does not have expected value");
  }

  @Test
  public void findByCriteriaLikeTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein", Evaluation.LIKE);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 3, "Expected result set size of 3");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneA".equals(gene.getSymbol()), "Record does not have expected value");
  }

  @Test
  public void findByCriteriaNotLikeTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein", Evaluation.NOT_LIKE);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 2, "Expected result set size of 2");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneC".equals(gene.getSymbol()), "Record does not have expected value");
  }

  @Test
  public void findByCriteriaStartsWithTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein", Evaluation.STARTS_WITH);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 3, "Expected result set size of 3");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneA".equals(gene.getSymbol()), "Record does not have expected value");
  }

  @Test
  public void findByCriteriaEndsWithTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "coding", Evaluation.ENDS_WITH);
    List<MongoGene> genes = (List<MongoGene>) geneRepository.find(Collections.singleton(criteria));
    Assert.notNull(genes, "Result set must not be null");
    Assert.notEmpty(genes, "Result set must not be empty");
    Assert.isTrue(genes.size() == 3, "Expected result set size of 3");
    Gene gene = genes.get(0);
    Assert.isTrue("GeneA".equals(gene.getSymbol()), "Record does not have expected value");
  }
  
  @Test
  public void findByNumberGreaterThanTest(){
    QueryCriteria criteria = new QueryCriteria("value", 5.0, Evaluation.GREATER_THAN);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 3, "Expected result set size of 3");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 6.78, "Record does not have expected value");
  }

  @Test
  public void findByNumberGreaterThanOrEqualsTest(){
    QueryCriteria criteria = new QueryCriteria("value", 4.56, Evaluation.GREATER_THAN_EQUALS);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 4, "Expected result set size of 4");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 4.56, "Record does not have expected value");
  }

  @Test
  public void findByNumberLessThanTest(){
    QueryCriteria criteria = new QueryCriteria("value", 5.0, Evaluation.LESS_THAN);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 3, "Expected result set size of 3");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 1.23, "Record does not have expected value");
  }

  @Test
  public void findByNumberLessThanOrEqualsTest(){
    QueryCriteria criteria = new QueryCriteria("value", 4.56, Evaluation.LESS_THAN_EQUALS);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 3, "Expected result set size of 3");
    GeneExpression record = records.get(2);
    Assert.isTrue(record.getValue() == 4.56, "Record does not have expected value");
  }

  @Test
  public void findByNumberBetweenTest(){
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(3.0, 7.0), Evaluation.BETWEEN);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 2, "Expected result set size of 2");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 4.56, "Record does not have expected value");
  }

  @Test
  public void findByNumberOutsideTest(){
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(3.0, 7.0), Evaluation.OUTSIDE);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 4, "Expected result set size of 2");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 1.23, "Record does not have expected value");
  }

  @Test
  public void findByNumberEqualsTest(){
    QueryCriteria criteria = new QueryCriteria("value", 4.56, Evaluation.EQUALS);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 1, "Expected result set size of 1");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 4.56, "Record does not have expected value");
  }

  @Test
  public void findByNumberInTest(){
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(2.34, 4.56), Evaluation.IN);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singleton(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 2, "Expected result set size of 2");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 2.34, "Record does not have expected value");
  }

  @Test
  public void findByNumberNotInTest(){
    QueryCriteria criteria = new QueryCriteria("value", Arrays.asList(2.34, 4.56), Evaluation.NOT_IN);
    List<MongoGeneExpression> records = (List<MongoGeneExpression>) expressionRepository.find(Collections.singletonList(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.notEmpty(records, "Result set must not be empty");
    Assert.isTrue(records.size() == 4, "Expected result set size of 4");
    GeneExpression record = records.get(0);
    Assert.isTrue(record.getValue() == 1.23, "Record does not have expected value");
  }

  @Test
  public void findByNumberNotInTest2(){
    QueryCriteria criteria = new QueryCriteria("taxId", Arrays.asList(9606, 1000), Evaluation.NOT_IN);
    List<MongoGene> records = (List<MongoGene>) geneRepository.find(Collections.singletonList(criteria));
    Assert.notNull(records, "Result set must not be null");
    Assert.isTrue(records.size() == 0, "Result set must be empty");
  }

  @Test
  public void insertTest(){
    MongoGene gene = new MongoGene();
    gene.setGeneId("100");
    gene.setSymbol("TEST");
    gene.setTaxId(9606);
    gene.setChromosome("1");
    gene.setGeneType("protein-coding");
    geneRepository.save(gene);

    MongoGene created = geneRepository.findByGeneId("100").get();
    Assert.notNull(created);
    Assert.isTrue(created.getGeneId().equals("100"));
    Assert.isTrue("TEST".equals(created.getSymbol()));

    geneRepository.delete(created);

  }

  @Test
  public void insertMultipleTest(){
    List<MongoGene> genes = new ArrayList<>();
    MongoGene gene1 = new MongoGene();
    gene1.setGeneId("100");
    gene1.setSymbol("TEST");
    gene1.setTaxId(9606);
    gene1.setChromosome("1");
    gene1.setGeneType("protein-coding");
    genes.add(gene1);
    MongoGene gene2 = new MongoGene();
    gene2.setGeneId("101");
    gene2.setSymbol("TEST2");
    gene2.setTaxId(9606);
    gene2.setChromosome("12");
    gene2.setGeneType("pseudo");
    genes.add(gene2);
    geneRepository.save(genes);
    Optional<MongoGene> optional = geneRepository.findByGeneId("100");
    Assert.notNull(optional);
    Assert.isTrue(optional.isPresent());
    Gene gene = optional.get();
    Assert.notNull(gene);
    optional = geneRepository.findByGeneId("101");
    Assert.notNull(genes);
    Assert.isTrue(optional.isPresent());
    gene = genes.get(0);
    Assert.notNull(gene);
    Assert.isTrue(geneRepository.count() == 7L);
  }

  @Test
  public void updateTest(){

    MongoGene gene = new MongoGene();
    gene.setGeneId("100");
    gene.setSymbol("TEST");
    gene.setTaxId(9606);
    gene.setChromosome("1");
    gene.setGeneType("protein-coding");
    geneRepository.save(gene);

    gene.setSymbol("TEST_TEST");
    gene.setGeneType("pseudogene");
    geneRepository.save(gene);

    Optional<MongoGene> optional = geneRepository.findByGeneId("100");
    Assert.notNull(optional);
    Assert.isTrue(optional.isPresent());
    MongoGene updated = optional.get();
    Assert.notNull(updated);
    Assert.isTrue("TEST_TEST".equals(updated.getSymbol()));
    Assert.isTrue("pseudogene".equals(updated.getGeneType()));

  }

  @Test
  public void updateMultipleTest(){
    List<MongoGene> genes = new ArrayList<>();
    MongoGene gene1 = new MongoGene();
    gene1.setGeneId("100");
    gene1.setSymbol("TEST");
    gene1.setTaxId(9606);
    gene1.setChromosome("1");
    gene1.setGeneType("protein-coding");
    genes.add(gene1);
    MongoGene gene2 = new MongoGene();
    gene2.setGeneId("101");
    gene2.setSymbol("TEST2");
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

    Optional<MongoGene> optional = geneRepository.findByGeneId("100");
    Assert.notNull(optional);
    Assert.isTrue(optional.isPresent());
    Gene gene = optional.get();
    Assert.notNull(gene);
    Assert.isTrue("TEST".equals(gene.getGeneType()));
    optional = geneRepository.findByGeneId("101");
    Assert.notNull(optional);
    Assert.isTrue(optional.isPresent());
    gene = optional.get();
    Assert.notNull(gene);
    Assert.isTrue("TEST".equals(gene.getGeneType()));
  }

  @Test
  public void deleteTest(){

    MongoGene gene = new MongoGene();
    gene.setGeneId("100");
    gene.setSymbol("TEST");
    gene.setTaxId(9606);
    gene.setChromosome("1");
    gene.setGeneType("protein-coding");
    geneRepository.save(gene);

    Optional<MongoGene> optional = geneRepository.findByGeneId("100");
    Assert.notNull(optional);
    Assert.isTrue(optional.isPresent());
    MongoGene created = optional.get();
    Assert.notNull(created);
    Assert.isTrue(created.getGeneId().equals("100"));

    geneRepository.delete(created);
    optional = geneRepository.findByGeneId("100");
    Assert.notNull(optional);
    Assert.isTrue(!optional.isPresent());

  }

  @Test
  public void distinctTest(){
    Set<Object> geneSymbols = geneRepository.distinct("symbol");
    Assert.notNull(geneSymbols);
    Assert.notEmpty(geneSymbols);
    Assert.isTrue(geneSymbols.size() == 5);
    Assert.isTrue(geneSymbols.contains("GeneA"));
  }

  @Test
  public void distinctQueryCriteriaTest(){
    QueryCriteria criteria = new QueryCriteria("geneType", "protein-coding");
    Set<Object> geneSymbols = geneRepository.distinct("symbol", Collections.singletonList(criteria));
    Assert.notNull(geneSymbols);
    Assert.notEmpty(geneSymbols);
    Assert.isTrue(geneSymbols.size() == 3);
    Assert.isTrue(geneSymbols.contains("GeneD"));
  }

  @Test
  public void guessGeneTest() throws Exception {

    List<MongoGene> genes = geneRepository.guess("GeneA");
    Assert.notNull(genes);
    Assert.notEmpty(genes);

    Gene gene = genes.get(0);
    Assert.isTrue(gene.getGeneId().equals("1"));

    genes = geneRepository.guess("MNO");
    Assert.notNull(genes);
    Assert.notEmpty(genes);

    gene = genes.get(0);
    Assert.isTrue(gene.getGeneId().equals("5"));

    genes = geneRepository.guess("XYZ");
    Assert.isTrue(genes.size() == 0);

  }

}
