package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.dataimport.impl.repositories.DataFileRepository;
import com.blueprint.centromere.core.dataimport.impl.repositories.DataSetRepository;
import com.blueprint.centromere.core.dataimport.impl.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.dataimport.impl.repositories.GeneRepository;
import com.blueprint.centromere.core.dataimport.impl.repositories.SampleRepository;
import com.blueprint.centromere.core.dataimport.impl.repositories.SubjectRepository;
import com.blueprint.centromere.core.dataimport.impl.writers.DelimtedTextFileWriter;
import com.blueprint.centromere.core.model.impl.GeneExpression;
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
@SpringBootTest(classes = { MongoDataSourceConfig.class })
public class TempFileWriterTests extends AbstractRepositoryTests{

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
        new DelimtedTextFileWriter<>(GeneExpression.class, environment);
    writer.setEnvironment(environment);
    String path = writer.getTempFilePath("/path/to/fake/file.txt");
    System.out.println(String.format("Writing temp file: %s", path));
    writer.doBefore(path);
    for (GeneExpression data: geneExpressionRepository.findAll()){
      writer.writeRecord(data);
    }
    writer.doAfter();
  }
  
  //TODO
//  @Test
//  public void mysqlImportTempWriterTest() throws Exception {
//    MySQLImportTempFileWriter<GeneExpression> writer = new MySQLImportTempFileWriter<>(GeneExpression.class);
//    writer.setEnvironment(environment);
//    String path = writer.getTempFilePath("/path/to/fake/file.txt");
//    System.out.println(String.format("Writing temp file: %s", path));
//    writer.doBefore(path);
//    for (GeneExpression data: geneExpressionRepository.findAll()){
//      writer.writeRecord(data);
//    }
//    writer.doAfter();
//  }
//
//  @Test
//  public void mysqlImportTest() throws Exception {
//    Assert.isTrue(geneExpressionRepository.count() == 5);
//    
//    MySQLImportTempFileWriter<GeneExpression> writer = new MySQLImportTempFileWriter<>(GeneExpression.class);
//    writer.setEnvironment(environment);
//    String path = writer.getTempFilePath("/path/to/fake/file.txt");
//    System.out.println(String.format("Writing temp file: %s", path));
//    writer.doBefore(path);
//    for (GeneExpression data: geneExpressionRepository.findAll()){
//      writer.writeRecord(data);
//    }
//    writer.doAfter();
//
//    MySqlImportFileImporter importer = new MySqlImportFileImporter("localhost", "", "", "");
//    
//  }

}
