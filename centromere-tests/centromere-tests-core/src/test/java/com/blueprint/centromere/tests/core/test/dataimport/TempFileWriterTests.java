package com.blueprint.centromere.tests.core.test.dataimport;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.DataSet;
import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.repositories.DataFileRepository;
import com.blueprint.centromere.core.commons.repositories.DataSetRepository;
import com.blueprint.centromere.core.commons.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.core.dataimport.DelimtedTextFileWriter;
import com.blueprint.centromere.tests.core.config.MongoDataSourceConfig;
import com.blueprint.centromere.tests.core.model.DataFileGenerator;
import com.blueprint.centromere.tests.core.model.DataSetGenerator;
import com.blueprint.centromere.tests.core.model.EntrezGeneDataGenerator;
import com.blueprint.centromere.tests.core.model.ExpressionDataGenerator;
import com.blueprint.centromere.tests.core.model.SampleDataGenerator;
import com.blueprint.centromere.tests.core.model.SubjectDataGenerator;
import java.util.List;
import org.junit.Before;
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
public class TempFileWriterTests {

  @Autowired private SampleRepository sampleRepository;
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private Environment environment;
  private boolean isConfigured = false;

  @Before
  public void setup() throws Exception {
    if (!isConfigured) {
      geneExpressionRepository.deleteAll();
      sampleRepository.deleteAll();
      subjectRepository.deleteAll();
      dataFileRepository.deleteAll();
      dataSetRepository.deleteAll();
      geneRepository.deleteAll();

      List<DataSet> dataSets = DataSetGenerator.generateData();
      dataSetRepository.save(dataSets);
      List<DataFile> dataFiles = DataFileGenerator.generateData(dataSets);
      dataFileRepository.save(dataFiles);
      List<Subject> subjects = SubjectDataGenerator.generateData();
      subjectRepository.save(subjects);
      List<Sample> samples = SampleDataGenerator.generateData(subjects, dataSets.get(0));
      sampleRepository.save(samples);
      List<Gene> genes = EntrezGeneDataGenerator.generateData();
      geneRepository.save(genes);
      List<GeneExpression> data = ExpressionDataGenerator.generateData(samples, genes, dataFiles);
      geneExpressionRepository.save(data);
      isConfigured = true;
    }
  }
  
  @Test
  public void delimitedTextWriterTest() throws Exception {
    DelimtedTextFileWriter<GeneExpression> writer = new DelimtedTextFileWriter<>(GeneExpression.class);
    writer.setEnvironment(environment);
    String path = writer.getTempFilePath("/path/to/fake/file.txt");
    System.out.println(String.format("Writing temp file: %s", path));
    writer.doBefore(path);
    for (GeneExpression data: geneExpressionRepository.findAll()){
      writer.writeRecord(data);
    }
    writer.doAfter();
  }
  
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
