package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.MongoConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.dataimport.reader.impl.GctGeneExpressionSourceReader;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneExpressionRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
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
    MongoConfiguration.MongoRepositoryConfiguration.class
})
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT })
public class GeneExpressionTests extends AbstractRepositoryTests {

  private static final Resource gctGeneExpressionFile = new ClassPathResource("samples/gene_expression.gct");

  @Autowired(required = false) private GeneExpressionRepository geneExpressionRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataSourceRepository dataSourceRepository;

  @Before
  @Override
  public void setup() throws Exception {
    super.setup();
    dataSetRepository.deleteAll();
    dataSourceRepository.deleteAll();
    geneExpressionRepository.deleteAll();
  }

  @Test
  public void configTest(){
    Assert.notNull(geneExpressionRepository, "Repository must not be null");
  }

  @Test
  public void gctExpressionReaderTest() throws Exception {

    geneExpressionRepository.deleteAll();
    Assert.isTrue(geneExpressionRepository.count() == 0);

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add((String) sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());
    
    DataSource dataFile = new DataSource();
    dataFile.setSource(gctGeneExpressionFile.getFile().getAbsolutePath());
    dataFile.setModel(GeneExpression.class);
    dataFile.setDataSetId((String) dataSet.getId());
    dataFile.setDataType("gct_expression");
    dataSourceRepository.insert(dataFile);

    DataImportProperties dataImportProperties = new DataImportProperties();
    dataImportProperties.setSkipInvalidGenes(true);
    dataImportProperties.setSkipInvalidSamples(true);

    GctGeneExpressionSourceReader reader = 
        new GctGeneExpressionSourceReader(geneRepository, sampleRepository, dataImportProperties);
    reader.setDataSet(dataSet);
    reader.setDataSource(dataFile);
    reader.doBefore();
    
    List<GeneExpression> expressionList = new ArrayList<>();
    
    GeneExpression geneExpression = reader.readRecord();
    while (geneExpression != null){
      expressionList.add(geneExpression);
      geneExpression = reader.readRecord();
    }
    
    reader.doAfter();

    Assert.notEmpty(expressionList);

  }

}
