package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.MongoConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.model.impl.Term;
import com.blueprint.centromere.core.model.impl.Term.TermGenerator;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.core.repository.impl.TermRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TermTests extends AbstractRepositoryTests {

  @Autowired private TermRepository termRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  
  private final TermGenerator termGenerator = new TermGenerator();
  
  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    termRepository.deleteAll();
    for (Gene gene: geneRepository.findAll()){
      termRepository.saveTerms(termGenerator.getModelTerms(gene));
    }
    for (Sample sample: sampleRepository.findAll()){
      termRepository.saveTerms(termGenerator.getModelTerms(sample));
    }
  }
  
  @Test
  public void termExtractionTest() throws Exception {
    Gene gene = geneRepository.findByGeneId("1").orElse(null);
    Assert.notNull(gene);
    List<Term> terms = termGenerator.getModelTerms(gene);
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
      } else if (term.getField().equals("geneId")){
        Assert.isTrue("1".equals(term.getTerm()));
      }
    }
  }
  
  @Test
  public void mapTermExtractiontest() throws Exception {
    Sample sample = new Sample();
    sample.setId("abc123");
    sample.setSampleId("SampleX");
    sample.setHistology("Histology Y");
    sample.setTissue("Tissue Z");
    Map<String, String> attributes = new HashMap<>();
    attributes.put("age", "70");
    attributes.put("type", "patient");
    sample.setAttributes(attributes);
    sample.setSampleType("blood");
    List<Term> terms = termGenerator.getModelTerms(sample);
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Map<String, Term> termMap = new HashMap<>();
    for (Term term: terms){
      termMap.put(term.getField(), term);
    }
    System.out.println(termMap.toString());
    Assert.isTrue(termMap.containsKey("age"));
    Assert.isTrue(termMap.containsKey("type"));
    Assert.isTrue(termMap.containsKey("sampleType"));
    Assert.isTrue(!termMap.containsKey("gender"));
  }
  
  @Test
  public void termInspectionTest() throws Exception {
    Assert.isTrue(termGenerator.modelHasManagedTerms(Gene.class));
    Assert.isTrue(!termGenerator.modelHasManagedTerms(GeneExpression.class));
  }
  
  @Test
  public void findByTermTest() throws Exception {
    List<Term> terms = termRepository.findByTerm("carcinoma");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == 1);
    Assert.isTrue(Sample.class.isAssignableFrom(terms.get(0).getModelType()));
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
    Optional<Gene> gene = geneRepository.findById(id);
    Assert.notNull(gene);
    Assert.isTrue(gene.isPresent());
  }
  
  @Test
  public void findByFieldTest() throws Exception {
    List<Term> terms = termRepository.findByField("name");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == sampleRepository.count());
    Assert.isTrue(Sample.class.isAssignableFrom(terms.get(0).getModelType()));
  }
  
  @Test
  public void guessTest() throws Exception {
    
    List<Term> terms = termRepository.guess("breast");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Term term = terms.get(0);
    Assert.isTrue(Sample.class.isAssignableFrom(term.getModelType()));
    Assert.isTrue("tissue".equals(term.getField()));

    terms = termRepository.guess("breast", "Gene");
    Assert.notNull(terms);
    Assert.isTrue(terms.isEmpty());
    
    terms = termRepository.guess("abc");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(terms.size() == 1);
    Assert.isTrue(Gene.class.isAssignableFrom(terms.get(0).getModelType()));

    terms = termRepository.guess("mplea");
    Assert.notNull(terms);
    Assert.notEmpty(terms);
    Assert.isTrue(Sample.class.isAssignableFrom(terms.get(0).getModelType()));
    Assert.isTrue("SampleA".equals(terms.get(0).getTerm()));
    
  }
  
}
