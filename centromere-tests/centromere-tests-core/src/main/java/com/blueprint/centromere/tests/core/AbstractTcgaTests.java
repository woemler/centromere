package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.commons.reader.TcgaSubjectReader;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import java.io.File;
import java.util.Date;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
public class AbstractTcgaTests extends AbstractEntrezGeneTests {

  private static final ClassPathResource SUBJECTS_FILE = new ClassPathResource("samples/tcga_sample_subjects.txt");

  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private Environment environment;

  @Override
  @Before
  public void setup() {
    super.setup();
    subjectRepository.deleteAll();
    TcgaSubjectReader reader = new TcgaSubjectReader();
    try {
      DataSet dataSet = getDataSet();
      DataFile dataFile = getDataFile(dataSet, SUBJECTS_FILE.getFile());
      reader.setDataSet(dataSet);
      reader.setDataFile(dataFile);
      reader.setImportOptions(new ImportOptionsImpl(environment));
      reader.doBefore();
      Subject subject = reader.readRecord();
      while (subject != null) {
        subjectRepository.insert(subject);
        subject = reader.readRecord();
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      reader.doAfter();
    }
    Assert.isTrue(subjectRepository.count() > 0);
  }

  protected DataSet getDataSet(){
    DataSet dataSet = new DataSet();
    dataSet.setShortName("TCGA-Test");
    dataSet.setDisplayName("TCGA Test Data Set");
    dataSet.setSource("TCGA");
    dataSet.setVersion("1.0");
    dataSet = dataSetRepository.insert(dataSet);
    return dataSet;
  }

  protected DataFile getDataFile(DataSet dataSet, File file){
    DataFile dataFile = new DataFile();
    dataFile.setDataSetId(dataSet.getId());
    dataFile.setDataType("RNA-Seq Gene Expression");
    dataFile.setDateCreated(new Date());
    dataFile.setDateUpdated(new Date());
    dataFile.setModel(GeneExpression.class);
    dataFile.setFilePath(file.getAbsolutePath());
    dataFile = dataFileRepository.insert(dataFile);
    return dataFile;
  }

  public DataFileRepository getDataFileRepository() {
    return dataFileRepository;
  }

  public DataSetRepository getDataSetRepository() {
    return dataSetRepository;
  }

  public SampleRepository getSampleRepository() {
    return sampleRepository;
  }

  public SubjectRepository getSubjectRepository() {
    return subjectRepository;
  }
}
