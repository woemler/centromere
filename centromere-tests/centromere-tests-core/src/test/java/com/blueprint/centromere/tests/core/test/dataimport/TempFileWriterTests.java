package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.writer.DelimtedTextFileWriter;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    MongoDataSourceConfig.class,
    CoreConfiguration.DefaultModelConfiguration.class,
    CoreConfiguration.CommonConfiguration.class
})
public class TempFileWriterTests extends AbstractRepositoryTests {

  @Autowired private SampleRepository sampleRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  private boolean isConfigured = false;
  
  @Test
  public void delimitedTextWriterTest() throws Exception {
    DelimtedTextFileWriter<GeneExpression> writer = 
        new DelimtedTextFileWriter<>(new DataImportProperties(), GeneExpression.class);
    String path = writer.getTempFilePath("/path/to/fake/file.txt");
    DataSet dataSet = new DataSet();
    dataSet.setSlug("test");
    dataSet.setName("Test");
    dataSet.setId("test");
    DataFile dataFile = new DataFile();
    dataFile.setFilePath(path);
    dataFile.setDataSetId(dataSet.getId());
    dataFile.setId("test");
    writer.setDataFile(dataFile);
    writer.setDataSet(dataSet);
    System.out.println(String.format("Writing temp file: %s", path));
    writer.doBefore();
    for (GeneExpression data: geneExpressionRepository.findAll()){
      writer.writeRecord(data);
    }
    writer.doAfter();
  }
  
}
