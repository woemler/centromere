package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.dataimport.writer.DelimtedTextFileWriter;
import com.blueprint.centromere.core.mongodb.MongoConfiguration;
import com.blueprint.centromere.core.mongodb.model.MongoDataFile;
import com.blueprint.centromere.core.mongodb.model.MongoDataSet;
import com.blueprint.centromere.core.mongodb.model.MongoGeneExpression;
import com.blueprint.centromere.core.mongodb.repository.MongoDataFileRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoDataSetRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneExpressionRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoSampleRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
    MongoDataSourceConfig.class,
    CoreConfiguration.CommonConfiguration.class,
    MongoConfiguration.MongoRepositoryConfiguration.class
})
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT })
public class TempFileWriterTests extends AbstractRepositoryTests {

  @Autowired private MongoSampleRepository sampleRepository;
  @Autowired private MongoDataFileRepository dataFileRepository;
  @Autowired private MongoDataSetRepository dataSetRepository;
  @Autowired private MongoGeneRepository geneRepository;
  @Autowired private MongoGeneExpressionRepository geneExpressionRepository;
  private boolean isConfigured = false;
  
  @Test
  public void delimitedTextWriterTest() throws Exception {
    DelimtedTextFileWriter<MongoGeneExpression> writer = 
        new DelimtedTextFileWriter<>(new DataImportProperties(), MongoGeneExpression.class);
    String path = writer.getTempFilePath("/path/to/fake/file.txt");
    DataSet dataSet = new MongoDataSet();
    dataSet.setDataSetId("test");
    dataSet.setName("Test");
    dataSet.setId("test");
    DataFile dataFile = new MongoDataFile();
    dataFile.setFilePath(path);
    dataFile.setDataSetId((String) dataSet.getId());
    dataFile.setId("test");
    writer.setDataFile(dataFile);
    writer.setDataSet(dataSet);
    System.out.println(String.format("Writing temp file: %s", path));
    writer.doBefore();
    for (MongoGeneExpression data: geneExpressionRepository.findAll()){
      writer.writeRecord(data);
    }
    writer.doAfter();
  }
  
}
