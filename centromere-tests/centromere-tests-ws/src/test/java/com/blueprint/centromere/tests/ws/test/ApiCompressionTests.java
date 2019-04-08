package com.blueprint.centromere.tests.ws.test;

import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.models.Gene;
import com.blueprint.centromere.tests.core.repositories.GeneRepository;
import com.blueprint.centromere.tests.ws.WebTestInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(secure = false)
public class ApiCompressionTests extends AbstractRepositoryTests {

  //private static final String BASE_URL = "/api/gene";

//  @Autowired
//  private MockMvc mockMvc;
  @Autowired private GeneRepository geneRepository;
//  @Autowired private Environment environment;

  @Before
  public void doBefore() throws Exception {
    geneRepository.deleteAll();
    for (Integer i = 0; i < 20000; i++) {
      Gene gene = (Gene) geneRepository.getModel().newInstance();
      gene.setSymbol("Gene"+i.toString());
      gene.setEntrezGeneId(i);
      gene.addAlias("gene"+i.toString());
      gene.setChromosome("X");
      gene.setChromosomeLocation("xq1");
      gene.setDescription("This is a test");
      gene.setTaxId(9606);
      gene.setGeneType("fake");
      geneRepository.insert(gene);
    }
  }
  
  @Test
  public void placeholderTest() {
    
  }

  // TODO: find way to enable compression in test.  Right now, doesn't seem to be happening
//  @Test
//  public void defaultCompressionTest() throws Exception {
//
//    Assert.isTrue(environment.getProperty("server.compression.enabled").equals("true"));
//
//    mockMvc.perform(get(BASE_URL).header("Accept-Encoding", "gzip,deflate"))
//        .andExpect(status().isOk())
//        .andDo(MockMvcResultHandlers.print())
//        .andExpect(MockMvcResultMatchers.header().string("Content-Encoding", "gzip"));
//  }

}
