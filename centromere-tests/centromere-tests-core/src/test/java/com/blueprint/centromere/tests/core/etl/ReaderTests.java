package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.tests.core.TestGene;
import com.blueprint.centromere.tests.core.models.Gene;
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
  
  private static final Resource GENE_INFO_FILE = new ClassPathResource("Homo_sapiens.gene_info");
  private static final Resource CSV_GENE_INFO_FILE = new ClassPathResource("Homo_sapiens.gene_info.csv");
  
  @Test
  public void geneInfoReaderTest() throws Exception {

    File file = GENE_INFO_FILE.getFile();
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
      e.printStackTrace();
      reader.doOnFailure(file, new HashMap<>());
    }
    
    Assert.assertNull(exception);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 5);

  }

  @Test
  public void csvGeneInfoReaderTest() throws Exception {

    File file = CSV_GENE_INFO_FILE.getFile();
    Assert.assertTrue(file.exists() && file.canRead());
    CSVGeneInfoReader<TestGene> reader = new CSVGeneInfoReader<>(TestGene.class);
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
      e.printStackTrace();
      reader.doOnFailure(file, new HashMap<>());
    }

    Assert.assertNull(exception);
    Assert.assertTrue(!genes.isEmpty());
    Assert.assertEquals(genes.size(), 5);
    for (Gene gene: genes){
      System.out.println(gene.toString());
    }

  }
  
}
