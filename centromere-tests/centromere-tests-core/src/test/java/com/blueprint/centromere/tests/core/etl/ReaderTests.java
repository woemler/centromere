package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.tests.core.TestGene;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ReaderTests {
  
  private static final Resource geneInfoResource = new ClassPathResource("Homo_sapiens.gene_info");
  
  @Test
  public void geneInfoReaderTest() throws Exception {

    File file = geneInfoResource.getFile();
    Assert.assertTrue(file.exists() && file.canRead());
    GeneInfoReader<TestGene> reader = new GeneInfoReader<>(TestGene.class);
    Assert.assertNotNull(reader);

    Exception exception = null;
    List<TestGene> genes = new ArrayList<>();

    try {
      reader.doBefore(file, new HashMap<>());
      TestGene gene = reader.readRecord();
      while (gene != null) {
        genes.add(gene);
        gene = reader.readRecord();
      }
      reader.doOnSuccess(file, new HashMap<>());
    } catch (Exception e){
      exception = e;
      reader.doOnFailure(file, new HashMap<>());
    }
    
    Assert.assertNull(exception);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 5);

  }
}
