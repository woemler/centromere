package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.reader.EntrezGeneInfoReader;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
public abstract class AbstractEntrezGeneTests {

  private static final ClassPathResource GENE_INFO_FILE = new ClassPathResource("Homo_sapiens.gene_info");

  @Autowired private GeneRepository geneRepository;

  @Before
  public void setup(){
    geneRepository.deleteAll();
    EntrezGeneInfoReader reader = new EntrezGeneInfoReader();
    try {
      reader.doBefore(new String[] {GENE_INFO_FILE.getPath()});
      Gene gene = reader.readRecord();
      while (gene != null){
        geneRepository.insert(gene);
        gene = reader.readRecord();
      }
    } finally {
      reader.doAfter();
    }
    Assert.isTrue(geneRepository.count() == 5);
  }

  public GeneRepository getGeneRepository() {
    return geneRepository;
  }
}
