package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.MongoConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.dataimport.writer.DelimtedTextFileWriter;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.DataSource.SourceTypes;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneExpressionRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
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

  @Autowired private SampleRepository sampleRepository;
  @Autowired private DataSourceRepository dataSourceRepository;
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
    dataSet.setDataSetId("test");
    dataSet.setName("Test");
    dataSet.setId("test");
    DataSource dataSource = new DataSource();
    dataSource.setSource(path);
    dataSource.setSourceType(SourceTypes.FILE.toString());
    dataSource.setDataSetId((String) dataSet.getId());
    dataSource.setId("test");
    writer.setDataSource(dataSource);
    writer.setDataSet(dataSet);
    System.out.println(String.format("Writing temp file: %s", path));
    writer.doBefore();
    for (GeneExpression data: geneExpressionRepository.findAll()){
      writer.writeRecord(data);
    }
    writer.doAfter();
  }
  
}
