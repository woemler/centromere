package com.blueprint.centromere.tests.cli.test.commands;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.commands.UpdateCommandExecutor;
import com.blueprint.centromere.cli.parameters.UpdateCommandParameters;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT, Profiles.CLI_PROFILE, CentromereCommandLineInitializer.SINGLE_COMMAND_PROFILE })
public class UpdateCommandTests extends AbstractRepositoryTests {

  @Autowired private UpdateCommandExecutor executor;
  @Autowired private DataSetRepository dataSetRepository;
  
  @Test
  public void helpTest(){
    UpdateCommandParameters parameters = new UpdateCommandParameters();
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
  public void mergeTest() throws Exception {
    
    DataSet dataSet1 = new DataSet();
    dataSet1.setDataSetId("test");
    dataSet1.setName("This is a test");
    dataSet1.setSampleIds(Arrays.asList("12345"));
    
    DataSet dataSet2 = new DataSet();
    dataSet2.setDataSetId("new-name");
    dataSet2.setSource("internal");
    Map<String, String> map = new HashMap<>();
    map.put("key", "value");
    dataSet2.setAttributes(map);

    BeanUtils.copyProperties(dataSet2, dataSet1, getNullProperties(dataSet2));
    
    System.out.println(dataSet1.toString());
    
    Assert.notNull(dataSet1.getId());
    Assert.notNull(dataSet1.getDataSetId());
    Assert.isTrue("new-name".equals(dataSet1.getDataSetId()));
    Assert.notNull(dataSet1.getName());
    Assert.isTrue("This is a test".equals(dataSet1.getName()));
    Assert.notNull(dataSet1.getSampleIds());
    Assert.notEmpty(dataSet1.getSampleIds());
    Assert.isTrue(dataSet1.getSampleIds().contains("12345"));
    Assert.notNull(dataSet1.getAttributes());
    Assert.notEmpty(dataSet1.getAttributes());
    Assert.isTrue(dataSet1.getAttributes().containsKey("key"));
    
  }

  public static String[] getNullProperties (Object source) {
    BeanWrapper wrapper = new BeanWrapperImpl(source);
    Set<String> properties = new HashSet<>();
    for(PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
      Object value = wrapper.getPropertyValue(descriptor.getName());
      if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
        if (((Collection) value).isEmpty()) {
          properties.add(descriptor.getName());
        }
      } else if (value == null) {
        properties.add(descriptor.getName());
      }
    }
    String[] result = new String[properties.size()];
    return properties.toArray(result);
  }
  

  @Test
  public void mergeUpdateDataSetTest(){

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("DataSetA");
    Assert.isTrue(optional.isPresent(), "DataSet must exist already.");
    String id = (String) optional.get().getId();

    String json = "{\"dataSetId\": \"DataSetA\", \"name\": \"test\", \"sampleIds\": [\"ABC123\"], \"attributes\": {\"key\": \"value\" } }";
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    parameters.setId(id);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.isTrue(exception == null, "Exception must be null");

    optional = dataSetRepository.findByName("DataSetA");
    Assert.isTrue(!optional.isPresent());
    optional = dataSetRepository.findByName("test");
    Assert.isTrue(optional.isPresent());
    DataSet dataSet = optional.get();
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSet.getId().equals(id));
    Assert.isTrue("test".equals(dataSet.getName()));
    Assert.notEmpty(dataSet.getSampleIds());
    Assert.isTrue(dataSet.getSampleIds().contains("ABC123"));
    Assert.notEmpty(dataSet.getAttributes());
    Assert.isTrue(dataSet.getAttributes().containsKey("key"));
    Assert.isTrue("Internal".equals(dataSet.getSource()));

  }

  @Test
  public void replaceUpdateDataSetTest(){

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("DataSetA");
    Assert.isTrue(optional.isPresent(), "DataSet must exist already.");
    String id = (String) optional.get().getId();

    String json = "{\"dataSetId\": \"DataSetA\", \"name\": \"test\", \"sampleIds\": [\"ABC123\"], \"attributes\": {\"key\": \"value\" } }";
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    parameters.setId(id);
    parameters.setReplace(true);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.isTrue(exception == null, "Exception must be null");

    optional = dataSetRepository.findByName("DataSetA");
    Assert.isTrue(!optional.isPresent());
    optional = dataSetRepository.findByName("test");
    Assert.isTrue(optional.isPresent());
    DataSet dataSet = optional.get();
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSet.getId().equals(id));
    Assert.isTrue("test".equals(dataSet.getName()));
    Assert.notEmpty(dataSet.getSampleIds());
    Assert.isTrue(dataSet.getSampleIds().contains("ABC123"));
    Assert.notEmpty(dataSet.getAttributes());
    Assert.isTrue(dataSet.getAttributes().containsKey("key"));
    Assert.isTrue(dataSet.getSource() == null, String.format("Expected null, found %s", dataSet.getSource()));

  }

  @Test
  public void invalidModelTest(){

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("DataSetA");
    Assert.isTrue(optional.isPresent(), "DataSet must exist already.");
    String id = (String) optional.get().getId();

    String json = "{\"dataSetId\": \"test\", \"name\": \"This is a test\", \"source\": \"internal\"}";
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    parameters.setModel("invalid");
    parameters.setData(json);
    parameters.setId(id);
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

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("DataSetA");
    Assert.isTrue(optional.isPresent(), "DataSet must exist already.");
    String id = (String) optional.get().getId();

    String json = "{\"dataSetId\" \"test\", \"name\" \"This is a test\", \"source\" \"internal\"}";
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    parameters.setId(id);
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

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("DataSetA");
    Assert.isTrue(optional.isPresent(), "DataSet must exist already.");
    String id = (String) optional.get().getId();

    String json = "{\"dataSetId\" \"test\", \"name\" \"This is a test\", \"invalid\" \"internal\"}";
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    parameters.setId(id);
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
  public void invaliIdTest(){

    String json = "{\"dataSetId\" \"test\", \"name\" \"This is a test\", \"invalid\" \"internal\"}";
    UpdateCommandParameters parameters = new UpdateCommandParameters();
    parameters.setModel("dataset");
    parameters.setData(json);
    parameters.setId("123");
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
