package com.blueprint.centromere.tests.cli.test.parameters;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.parameters.BaseParameters;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateParameterParsingTests {
  
  private static final String COMMAND = "create";
  
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void createDataSetTest() throws Exception {
    
    CreateCommandParameters parameters = new CreateCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { COMMAND, "-m", "dataset", "--data", 
        "{\"shortName\": \"test\", \"source\": \"internal\", \"displayName\": \"This is a test\"}" };
    
    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand(COMMAND, parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    
    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue(COMMAND.equals(jc.getParsedCommand()), "Parsed command should be '" + COMMAND + "'");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Create parameters cannot be null");
    Assert.notNull(parameters.getModel(), "model must not be null");
    Assert.isTrue("dataset".equals(parameters.getModel()), "model does not match");
    Assert.notNull(parameters.getData(), "Data must not be null");
    Assert.isTrue(!parameters.isHelp(), "Help should not be true");

    DataSet dataSet = null;
    
    try {
      dataSet = objectMapper.readValue(parameters.getData(), DataSet.class);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.notNull(dataSet, "DataSet should not be null.");
    Assert.notNull(dataSet.getShortName(), "ShortName should not be null");
    Assert.notNull(dataSet.getDisplayName(), "Displayname should not be null");
    Assert.notNull(dataSet.getSource(), "Source should not be null");
    Assert.isTrue("test".equals(dataSet.getShortName()), "ShortName should be 'test'");
    Assert.isTrue("This is a test".equals(dataSet.getDisplayName()), "DisplayName should be 'test'");
    Assert.isTrue("internal".equals(dataSet.getSource()), "source should be 'test'");
    
  }

}
