package com.blueprint.centromere.tests.cli.test.parameters;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.parameters.BaseParameters;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
  public void createDataSetJsonTest() throws Exception {
    
    CreateCommandParameters parameters = new CreateCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { COMMAND, "-m", "dataset", "--data", 
        "{\"dataSetId\": \"test\", \"source\": \"internal\", \"name\": \"This is a test\"}" };
    
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
    Assert.isTrue(parameters.getFields().isEmpty());

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
  public void createDataSetDynamicTest() throws Exception {

    CreateCommandParameters parameters = new CreateCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { COMMAND, "-m", "dataset", "-DdataSetId=test", "-Dsource=internal", 
        "-Dname=This is a test" };

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
    
    System.out.println(parameters.toString());

    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue(COMMAND.equals(jc.getParsedCommand()), "Parsed command should be '" + COMMAND + "'");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Create parameters cannot be null");
    Assert.notNull(parameters.getModel(), "model must not be null");
    Assert.isTrue("dataset".equals(parameters.getModel()), "model does not match");
    Assert.isTrue(parameters.getData() == null, "Data must be null");
    Assert.isTrue(!parameters.getFields().isEmpty(), "Fields should not be empty");
    Map<String, String> fields = parameters.getFields();
    Assert.isTrue(fields.containsKey("dataSetId"));
    Assert.isTrue("test".equals(fields.get("dataSetId")));
    Assert.isTrue(!parameters.isHelp(), "Help should not be true");

  }

}
