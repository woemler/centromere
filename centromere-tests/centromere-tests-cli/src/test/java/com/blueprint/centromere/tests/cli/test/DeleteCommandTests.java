package com.blueprint.centromere.tests.cli.test;

import com.blueprint.centromere.cli.CommandLineInputExecutor;
import com.blueprint.centromere.cli.ModelProcessorBeanRegistry;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.util.List;
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
@ActiveProfiles({ Profiles.CLI_PROFILE })
public class DeleteCommandTests extends AbstractRepositoryTests {

  @Autowired private ModelProcessorBeanRegistry registry;
  @Autowired private CommandLineInputExecutor executor;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private SampleRepository sampleRepository;
  
  @Test
  public void test() throws Exception {
    for (DataSet dataSet: dataSetRepository.findAll()){
      System.out.println(dataSet.toString());
    }
    for (DataFile dataFile: dataFileRepository.findAll()){
      System.out.println(dataFile.toString());
    }
  }
  
  @Test
  public void deleteFileTest() throws Exception {
    
    String path = "/path/to/fileA";
    Optional<DataFile> dataFileOptional = dataFileRepository.findByFilePath(path);
    DataFile dataFile = dataFileOptional.get();
    String dataFileId = dataFile.getId();
    String dataSetId = dataFile.getDataSetId();
    
    Assert.isTrue(geneExpressionRepository.findByDataFileId(dataFileId).size() > 0);
    Assert.isTrue(dataFileOptional.isPresent(), "DataFile must be present");
    
    DataSet dataSet = dataSetRepository.findOne(dataSetId);
    Assert.notNull(dataSet);
    Assert.isTrue(dataSet.getDataFileIds().contains(dataFileId));
    
    
    String[] args = { "delete", "datafile", path};
    Exception exception = null;
    try {
      executor.run(args);
    } catch (Exception e){
      exception = e;
      e.printStackTrace();
    }
    
    Assert.isNull(exception, "Exception must be null");
    
    dataFileOptional = dataFileRepository.findByFilePath(path);
    Assert.isTrue(!dataFileOptional.isPresent(), "DataFile must not be present");
    Assert.isTrue(geneExpressionRepository.findByDataFileId(dataFileId).size() == 0);

    dataSet = dataSetRepository.findOne(dataSetId);
    Assert.notNull(dataSet);
    Assert.isTrue(!dataSet.getDataFileIds().contains(dataFileId));
    
  }
  
  @Test
  public void deleteDataSetTest() throws Exception {
    
    String dataSetName = "DataSetA";
    Optional<DataSet> dataSetOptional = dataSetRepository.findByShortName(dataSetName);
    Assert.isTrue(dataSetOptional.isPresent());
    DataSet dataSet = dataSetOptional.get();
    String dataSetId = dataSet.getId();
    
    List<String> dataFileIds = dataSet.getDataFileIds();
    for (String dataFileId: dataFileIds){
      Assert.notNull(dataFileRepository.findOne(dataFileId));
    }
    
    List<String> sampleIds = dataSet.getSampleIds();
    for (String sampleId: sampleIds){
      Assert.notNull(sampleRepository.findOne(sampleId));
    }

    Assert.isTrue(geneExpressionRepository.findByDataSetId(dataSetId).size() > 0);

    String[] args = { "delete", "dataset", dataSetName};
    Exception exception = null;
    try {
      executor.run(args);
    } catch (Exception e){
      exception = e;
      e.printStackTrace();
    }

    Assert.isNull(exception, "Exception must be null");
    
    Assert.isNull(dataSetRepository.findOne(dataSetId));
    for (String dataFileId: dataFileIds){
      Assert.isNull(dataFileRepository.findOne(dataFileId));
    }
    for (String sampleId: sampleIds){
      Assert.isNull(sampleRepository.findOne(sampleId));
    }
    Assert.isTrue(geneExpressionRepository.findByDataSetId(dataSetId).size() == 0);
    
    
  }
  
}
