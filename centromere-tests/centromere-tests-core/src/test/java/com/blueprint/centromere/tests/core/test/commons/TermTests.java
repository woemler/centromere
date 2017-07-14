package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.model.Term;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.TermRepository;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    CoreConfiguration.DefaultModelConfiguration.class
})
public class TermTests extends AbstractRepositoryTests {

  @Autowired private TermRepository termRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  
  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    termRepository.deleteAll();
    for (Gene gene: geneRepository.findAll()){
      termRepository.saveTerms(Term.getModelTerms(gene));
    }
    for (Sample sample: sampleRepository.findAll()){
      termRepository.saveTerms(Term.getModelTerms(sample));
    }
  }
  
  @Test
  public void termExtractionTest() throws Exception {
    Gene gene = geneRepository.findByPrimaryReferenceId("1").orElse(null);
    Assert.notNull(gene);
    List<Term> terms = Term.getModelTerms(gene);
    System.out.println(terms);
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == 3);
    for (Term term: terms){
      Assert.isTrue(term.getModelType().equals(Gene.class));
      if (term.getField().equals("primaryGeneSymbol")){
        Assert.isTrue("GeneA".equals(term.getTerm()));
      }
      else if (term.getField().equals("aliases")){
        Assert.isTrue("ABC".equals(term.getTerm()));
      } else if (term.getField().equals("primaryReferenceId")){
        Assert.isTrue("1".equals(term.getTerm()));
      }
    }
  }
  
  @Test
  public void findByTermTest() throws Exception {
    List<Term> terms = termRepository.findByTerm("carcinoma");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == 1);
    Assert.isTrue(terms.get(0).getModelType().equals(Sample.class));
    Assert.isTrue("histology".equals(terms.get(0).getField()));
    Assert.isTrue(terms.get(0).getReferenceIds().size() == 2);
  }

  @Test
  public void findByModelTest(){
    List<Term> terms = termRepository.findByModel(Gene.class);
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    String id = terms.get(0).getReferenceIds().get(0);
    Assert.notNull(id);
    Gene gene = geneRepository.findOne(id);
    Assert.notNull(gene);
  }
  
  @Test
  public void findByFieldTest() throws Exception {
    List<Term> terms = termRepository.findByField("name");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == sampleRepository.count());
    Assert.isTrue(Sample.class.equals(terms.get(0).getModelType()));
  }
  
  @Test
  public void guessTest() throws Exception {
    
    List<Term> terms = termRepository.guess("breast");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Term term = terms.get(0);
    Assert.isTrue(Sample.class.equals(term.getModelType()));
    Assert.isTrue("tissue".equals(term.getField()));

    terms = termRepository.guess("breast", "Gene");
    Assert.notNull(terms);
    Assert.isTrue(terms.isEmpty());
    
    terms = termRepository.guess("abc");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == 1);
    Assert.isTrue(Gene.class.equals(terms.get(0).getModelType()));

    terms = termRepository.guess("mplea");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(Sample.class.equals(terms.get(0).getModelType()));
    Assert.isTrue("SampleA".equals(terms.get(0).getTerm()));
    
  }
  
}
