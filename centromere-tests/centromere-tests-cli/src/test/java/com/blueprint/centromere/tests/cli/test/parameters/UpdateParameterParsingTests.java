package com.blueprint.centromere.tests.cli.test.parameters;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.blueprint.centromere.cli.parameters.BaseParameters;
import com.blueprint.centromere.cli.parameters.UpdateCommandParameters;
import com.blueprint.centromere.core.model.impl.DataSet;
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
public class UpdateParameterParsingTests {
  
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void updateDataSetTest() throws Exception {
    
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = {UpdateCommandParameters.COMMAND, "-i", "123", "-m", "dataset", "-r", "--data", 
        "{\"dataSetId\": \"test\", \"source\": \"internal\", \"name\": \"This is a test\"}" };
    
    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand(UpdateCommandParameters.COMMAND, parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    
    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue(UpdateCommandParameters.COMMAND.equals(jc.getParsedCommand()), 
        "Parsed command should be '" + UpdateCommandParameters.COMMAND + "'");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Create parameters cannot be null");
    Assert.notNull(parameters.getModel(), "model must not be null");
    Assert.isTrue("dataset".equals(parameters.getModel()), "model does not match");
    Assert.notNull(parameters.getData(), "Data must not be null");
    Assert.notNull(parameters.getId(), "ID must not be null");
    Assert.isTrue("123".equals(parameters.getId()), "ID should be 123");
    Assert.isTrue(parameters.isReplace(), "Merge should be true");
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
    Assert.notNull(dataSet.getDataSetId(), "ShortName should not be null");
    Assert.notNull(dataSet.getName(), "Displayname should not be null");
    Assert.notNull(dataSet.getSource(), "Source should not be null");
    Assert.isTrue("test".equals(dataSet.getDataSetId()), "ShortName should be 'test'");
    Assert.isTrue("This is a test".equals(dataSet.getName()), "DisplayName should be 'test'");
    Assert.isTrue("internal".equals(dataSet.getSource()), "source should be 'test'");
    
  }

  @Test
  public void missingRequiredParameterTest() throws Exception {

    UpdateCommandParameters parameters = new UpdateCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = {UpdateCommandParameters.COMMAND, "-m", "dataset", "-r", "--data",
        "{\"dataSetId\": \"test\", \"source\": \"internal\", \"name\": \"This is a test\"}" };

    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand(UpdateCommandParameters.COMMAND, parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof ParameterException, 
        String.format("Expected ParameterException, was %s", exception.getClass().getSimpleName()));
  }

}
