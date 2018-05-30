package com.blueprint.centromere.tests.cli.test.executor;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.JCommanderInputExecutor;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneExpressionRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.MutationRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.util.Optional;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandLineTestInitializer.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT, Profiles.CLI_PROFILE, CentromereCommandLineInitializer.SINGLE_COMMAND_PROFILE })
@FixMethodOrder
public class CreateArgExecutionTests extends AbstractRepositoryTests {
  
  @Autowired private JCommanderInputExecutor executor;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataSourceRepository dataSourceRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private MutationRepository mutationRepository;
  
  
  @Test
  public void helpTest(){
    String[] args = { "-h" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;
    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.isTrue(exception == null, "Exception must be null");
  }

  // Test simple metadata record creation
  @Test
  public void dataSetCreationTest() throws Exception {

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("test");
    Assert.isTrue(!optional.isPresent());
    
    String[] args = { "create", "-m", "dataset", "-DdataSetId=test", "-Dname=This is a test",
      "-Dsource=internal", "-DsampleIds=sample", "-Dattributes.flag=Y" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;

    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "Exception should be null");

    optional = dataSetRepository.findByDataSetId("test");
    Assert.isTrue(optional.isPresent());
    DataSet dataSet = optional.get();
    Assert.isTrue("test".equals(dataSet.getDataSetId()));
    Assert.isTrue("This is a test".equals(dataSet.getName()));
    Assert.isTrue("internal".equals(dataSet.getSource()));
    Assert.isTrue(dataSet.getDescription() == null);
    Assert.notNull(dataSet.getSampleIds());
    Assert.notEmpty(dataSet.getSampleIds());
    Assert.isTrue("sample".equals(dataSet.getSampleIds().get(0)));
    Assert.notNull(dataSet.getAttributes());
    Assert.notEmpty(dataSet.getAttributes());
    Assert.isTrue(dataSet.getAttributes().containsKey("flag"));
    Assert.isTrue("Y".equals(dataSet.getAttribute("flag")));
    
  }
  
  // Test data record creation
  @Test
  public void geneExpressionCreationTest() throws Exception {

  }

  // Test invalid model creation
  
  // Test invalid field assignment
  
  // Test invalid field type assignment
  
  // Test null required field assignment
  
}
