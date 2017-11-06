package com.blueprint.centromere.tests.cli.test;

import com.blueprint.centromere.core.commons.processor.EntrezGeneInfoProcessor;
import com.blueprint.centromere.core.commons.processor.GctGeneExpressionProcessor;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandLineTestInitializer.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({ Profiles.CLI_PROFILE, CommandLineTestInitializer.SINGLE_COMMAND_PROFILE })
public class ProcessorTests {
  
  @Autowired private EntrezGeneInfoProcessor entrezGeneInfoProcessor;
  @Autowired private GctGeneExpressionProcessor expressionProcessor;
  
  @Test
  public void optionsTest(){
//    Assert.notNull(entrezGeneInfoProcessor);
//    Assert.notNull(expressionProcessor);
//    List<Option> geneOptions = new ArrayList<>(entrezGeneInfoProcessor.getDataImportProperties());
//    Assert.notNull(geneOptions);
//    Assert.notEmpty(geneOptions);
//    List<Option> gctOptions = new ArrayList<>(expressionProcessor.getDataImportProperties());
//    Assert.notNull(gctOptions);
//    Assert.notEmpty(gctOptions);
//    Assert.isTrue(gctOptions.size() > geneOptions.size());
//    for (Option option: gctOptions){
//      System.out.println(String.format("%s : %s", option.value(), option.description()));
//    }
  }

}
