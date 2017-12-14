package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.mongodb.model.MongoDataFile;
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
  public void dataFileTest() throws Exception {
    DataFile dataFile = new MongoDataFile();
    dataFile.setFilePath("/path/to/file.txt");
    dataFile.setDataSetId("dataset");
    dataFile.setModel(GeneExpression.class);
    dataFile.setDataFileId(DataFile.generateFileId(dataFile));
    Assert.notNull(dataFile.getId());
    Assert.notNull(dataFile.getDataFileId());
    String fileId1 = (String) dataFile.getId();
    dataFile.setDataSetId("another-dataset");
    dataFile.setDataFileId(DataFile.generateFileId(dataFile));
    Assert.notNull(dataFile.getId());
    Assert.notNull(dataFile.getDataFileId());
    String fileId2 = (String) dataFile.getId();
    Assert.isTrue(!fileId1.equals(fileId2), "DataFileIds should be different");
    dataFile.setDataSetId("dataset");
    dataFile.setDataFileId(DataFile.generateFileId(dataFile));
    Assert.notNull(dataFile.getId());
    Assert.notNull(dataFile.getDataFileId());
    String fileId3 = (String) dataFile.getId();
    Assert.isTrue(fileId1.equals(fileId3), "FileIds should be the same");
  }
  
}
