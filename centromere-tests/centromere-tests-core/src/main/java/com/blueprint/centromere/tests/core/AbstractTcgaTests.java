package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.dataimport.reader.impl.TcgaSampleReader;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.DataSource.SourceTypes;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
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

  @Autowired private DataSourceRepository dataSourceRepository;
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
      DataSource dataSource = getDataSource(dataSet, SUBJECTS_FILE.getFile());
      reader.setDataSet(dataSet);
      reader.setDataSource(dataSource);
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

  protected DataSource getDataSource(DataSet dataSet, File file){
    DataSource dataSource = new DataSource();
    dataSource.setDataSetId(dataSet.getDataSetId());
    dataSource.setDataType("RNA-Seq Gene Expression");
    dataSource.setDateCreated(new Date());
    dataSource.setDateUpdated(new Date());
    dataSource.setModel(GeneExpression.class);
    dataSource.setSource(file.getAbsolutePath());
    dataSource.setSourceType(SourceTypes.FILE.toString());
    dataSource = dataSourceRepository.insert(dataSource);
    return dataSource;
  }

  public DataSourceRepository getDataSourceRepository() {
    return dataSourceRepository;
  }

  public DataSetRepository getDataSetRepository() {
    return dataSetRepository;
  }

  public SampleRepository getSampleRepository() {
    return sampleRepository;
  }
}
