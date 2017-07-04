package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.tests.core.AbstractTcgaTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
    MongoDataSourceConfig.class,
    CoreConfiguration.CommonConfiguration.class,
    CoreConfiguration.DefaultModelConfiguration.class
})
public class GeneExpressionTests extends AbstractTcgaTests {

  @Autowired(required = false) private GeneExpressionRepository geneExpressionRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private SubjectRepository subjectRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataFileRepository dataFileRepository;

  private final ClassPathResource exampleFile = new ClassPathResource("samples/sample_tcga_rna_seq_gene_expression.txt");

  @Override
  public void setup() {
    super.setup();
    dataSetRepository.deleteAll();
    dataFileRepository.deleteAll();
    geneExpressionRepository.deleteAll();
  }

  @Test
  public void configTest(){
    Assert.notNull(geneExpressionRepository, "Repository must not be null");
  }

  @Test
  public void tcgaGeneExpressionFileReaderTest() throws Exception {
//
//    DataSet dataSet = getDataSet();
//    DataFile dataFile = getDataFile(dataSet, exampleFile.getFile());
//    reader.doBefore(exampleFile.getFile(), dataFile, dataSet);
//    List<GeneExpression> records = new ArrayList<>();
//    GeneExpression record = reader.readRecord();
//    while (record != null){
//      records.add(record);
//      record = reader.readRecord();
//    }
//    reader.doAfter();
//    Assert.notNull(records, "List must not be null");
//    Assert.notEmpty(records, "List must not be empty");
//    Assert.isTrue(records.size() == 5,
//        String.format("Expected 5 records, found ", records.size()));
  }



}
