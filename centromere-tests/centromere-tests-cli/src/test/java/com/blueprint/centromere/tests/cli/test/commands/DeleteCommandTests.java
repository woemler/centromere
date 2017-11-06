package com.blueprint.centromere.tests.cli.test.commands;

import com.blueprint.centromere.cli.CommandLineInputExecutor;
import com.blueprint.centromere.cli.ModelProcessorBeanRegistry;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
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
public class DeleteCommandTests extends AbstractRepositoryTests {

  @Autowired private ModelProcessorBeanRegistry registry;
  @Autowired private CommandLineInputExecutor executor;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private SampleRepository sampleRepository;
  
  @Test
  public void deleteFileTest() throws Exception {
    
//    String path = "/path/to/fileA";
//    Optional<DataFile> dataFileOptional = dataFileRepository.findByFilePath(path);
//    DataFile dataFile = dataFileOptional.get();
//    String dataFileId = dataFile.getId();
//    String dataSetId = dataFile.getDataSetId();
//    
//    Assert.isTrue(geneExpressionRepository.findByDataFileId(dataFileId).size() > 0);
//    Assert.isTrue(dataFileOptional.isPresent(), "DataFile must be present");
//    
//    DataSet dataSet = dataSetRepository.findOne(dataSetId);
//    Assert.notNull(dataSet);
//    Assert.isTrue(dataSet.getDataFileIds().contains(dataFileId));
//    
//    
//    String[] args = { "delete", "datafile", path};
//    Exception exception = null;
//    try {
//      executor.run(new DefaultApplicationArguments(args));
//    } catch (Exception e){
//      exception = e;
//      e.printStackTrace();
//    }
//    
//    Assert.isNull(exception, "Exception must be null");
//    
//    dataFileOptional = dataFileRepository.findByFilePath(path);
//    Assert.isTrue(!dataFileOptional.isPresent(), "DataFile must not be present");
//    Assert.isTrue(geneExpressionRepository.findByDataFileId(dataFileId).size() == 0);
//
//    dataSet = dataSetRepository.findOne(dataSetId);
//    Assert.notNull(dataSet);
//    Assert.isTrue(!dataSet.getDataFileIds().contains(dataFileId));
//    
  }
  
}
