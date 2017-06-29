package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.blueprint.centromere.core.dataimport.writer.DelimtedTextFileWriter;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
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
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private Environment environment;
  private boolean isConfigured = false;
  
  @Test
  public void delimitedTextWriterTest() throws Exception {
    DelimtedTextFileWriter<GeneExpression> writer = 
        new DelimtedTextFileWriter<>(GeneExpression.class);
    writer.setImportOptions(new ImportOptionsImpl(environment));
    String path = writer.getTempFilePath("/path/to/fake/file.txt");
    System.out.println(String.format("Writing temp file: %s", path));
    writer.doBefore(path);
    for (GeneExpression data: geneExpressionRepository.findAll()){
      writer.writeRecord(data);
    }
    writer.doAfter();
  }
  
}
