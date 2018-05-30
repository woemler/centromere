package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DataSetTests {

  @Test
  // TODO: Update this test
  public void dataFileTest() throws Exception {
    DataSource dataSource = new DataSource();
    dataSource.setSource("/path/to/file.txt");
    dataSource.setDataSetId("dataset");
    dataSource.setModel(GeneExpression.class);
    dataSource.setDataSourceId("file-a");
    Assert.notNull(dataSource.getId());
    Assert.notNull(dataSource.getDataSourceId());
    String fileId1 = (String) dataSource.getId();
    dataSource.setDataSetId("another-dataset");
    dataSource.setDataSourceId("file-b");
    Assert.notNull(dataSource.getId());
    Assert.notNull(dataSource.getDataSourceId());
    String fileId2 = (String) dataSource.getId();
    Assert.isTrue(!fileId1.equals(fileId2), "DataSourceIds should be different");
    dataSource.setDataSetId("dataset");
    dataSource.setDataSourceId("file-a");
    Assert.notNull(dataSource.getId());
    Assert.notNull(dataSource.getDataSourceId());
    String fileId3 = (String) dataSource.getId();
    Assert.isTrue(fileId1.equals(fileId3), "FileIds should be the same");
  }
  
}
