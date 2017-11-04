package com.blueprint.centromere.tests.cli.test.commands;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.commands.CreateCommandExecutor;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@ActiveProfiles({ Profiles.CLI_PROFILE, CentromereCommandLineInitializer.SINGLE_COMMAND_PROFILE })
public class CreateCommandTests extends AbstractRepositoryTests {

  @Autowired private CreateCommandExecutor executor;
  @Autowired private DataSetRepository dataSetRepository;
  
  @Test
  public void helpTest(){
    CreateCommandParameters parameters = new CreateCommandParameters();
    parameters.setHelp(true);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.isTrue(exception == null, "Exception must be null");
  }

  @Test
  public void createDataSetTest(){

    Optional<DataSet> optional = dataSetRepository.findByShortName("test");
    Assert.isTrue(!optional.isPresent(), "DataSet must not exist already.");
    
    String json = "{\"shortName\": \"test\", \"displayName\": \"This is a test\", \"source\": \"internal\"}";
    CreateCommandParameters parameters = new CreateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.isTrue(exception == null, "Exception must be null");

    optional = dataSetRepository.findByShortName("test");
    Assert.isTrue(optional.isPresent());
    DataSet dataSet = optional.get();
    Assert.isTrue("test".equals(dataSet.getShortName()));
    Assert.isTrue("This is a test".equals(dataSet.getDisplayName()));
    Assert.isTrue("internal".equals(dataSet.getSource()));
    Assert.isTrue(dataSet.getDescription() == null);
    
  }

  @Test
  public void duplicateDataSetTest(){

    Optional<DataSet> optional = dataSetRepository.findByShortName("DataSetA");
    Assert.isTrue(optional.isPresent(), "DataSet must exist already.");

    String json = "{\"shortName\": \"DataSetA\", \"displayName\": \"This is a test\", \"source\": \"internal\"}";
    CreateCommandParameters parameters = new CreateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.notNull(exception, "Exception must not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, but was " 
        + exception.getClass().getSimpleName());

    optional = dataSetRepository.findByShortName("DataSetA");
    Assert.isTrue(optional.isPresent());
    DataSet dataSet = optional.get();
    Assert.isTrue("DataSetA".equals(dataSet.getShortName()));
    
  }

  @Test
  public void invalidModelTest(){

    String json = "{\"shortName\": \"test\", \"displayName\": \"This is a test\", \"source\": \"internal\"}";
    CreateCommandParameters parameters = new CreateCommandParameters();
    parameters.setModel("invalid");
    parameters.setData(json);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.notNull(exception, "Exception must not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, but was "
        + exception.getClass().getSimpleName());

  }

  @Test
  public void badJsonTest(){

    String json = "{\"shortName\" \"test\", \"displayName\" \"This is a test\", \"source\" \"internal\"}";
    CreateCommandParameters parameters = new CreateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.notNull(exception, "Exception must not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, but was "
        + exception.getClass().getSimpleName());

  }

  @Test
  public void invalidAttributeTest(){

    String json = "{\"shortName\" \"test\", \"displayName\" \"This is a test\", \"invalid\" \"internal\"}";
    CreateCommandParameters parameters = new CreateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.notNull(exception, "Exception must not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, but was "
        + exception.getClass().getSimpleName());

  }
  
}
