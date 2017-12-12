package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.reader.TcgaSampleReader;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import java.io.File;
import java.util.Date;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    sampleRepository.deleteAll();
    TcgaSampleReader reader = new TcgaSampleReader();
    try {
      DataSet dataSet = getDataSet();
      DataFile dataFile = getDataFile(dataSet, SUBJECTS_FILE.getFile());
      reader.setDataSet(dataSet);
      reader.setDataFile(dataFile);
      reader.doBefore();
      Sample sample = reader.readRecord();
      while (sample != null) {
        sampleRepository.insert(sample);
        sample = reader.readRecord();
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      reader.doAfter();
    }
    Assert.isTrue(sampleRepository.count() > 0);
  }

  protected DataSet getDataSet(){
    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("TCGA-Test");
    dataSet.setName("TCGA Test Data Set");
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
}
