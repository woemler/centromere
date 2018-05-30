package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.dataimport.reader.impl.EntrezGeneInfoReader;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.DataSource.SourceTypes;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
public abstract class AbstractEntrezGeneTests {

  private static final ClassPathResource GENE_INFO_FILE = new ClassPathResource(
      "samples/Homo_sapiens.gene_info");

  @Autowired private GeneRepository geneRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataSourceRepository dataSourceRepository;

  @Before
  public void setup() throws Exception{
    dataSourceRepository.deleteAll();
    dataSetRepository.deleteAll();
    geneRepository.deleteAll();
    EntrezGeneInfoReader reader = new EntrezGeneInfoReader();
    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("metadata");
    dataSet.setName("Metadata");
    dataSetRepository.insert(dataSet);
    DataSource dataSource = new DataSource();
    dataSource.setDataSetId(dataSet.getDataSetId());
    dataSource.setSource(GENE_INFO_FILE.getPath());
    dataSource.setSourceType(SourceTypes.FILE.toString());
    dataSourceRepository.insert(dataSource);
    reader.setDataSet(dataSet);
    reader.setDataSource(dataSource);
    
    try {
      reader.doBefore();
      Gene gene = reader.readRecord();
      while (gene != null){
        geneRepository.insert(gene);
        gene = reader.readRecord();
      }
    } finally {
      reader.doAfter();
    }
    Assert.isTrue(geneRepository.count() == 5);
  }

  public GeneRepository getGeneRepository() {
    return geneRepository;
  }
}
