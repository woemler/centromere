package com.blueprint.centromere.tests.cli.test.commands;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.commands.FileImportExecutor;
import com.blueprint.centromere.cli.parameters.ImportFileCommandParameters;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandLineTestInitializer.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({ Profiles.CLI_PROFILE, CentromereCommandLineInitializer.SINGLE_COMMAND_PROFILE })
public class ImportCommandTests extends AbstractRepositoryTests {
  
  public static final Resource geneInfoFile = new ClassPathResource("samples/Homo_sapiens.gene_info");
  public static final Resource gctGeneExpressionFile = new ClassPathResource("samples/gene_expression.gct");

  @Autowired private FileImportExecutor executor;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private DataImportProperties dataImportProperties;
  
  @Test
  public void helpTest(){
    ImportFileCommandParameters parameters = new ImportFileCommandParameters();
    parameters.setHelp(true);
    Exception exception = null;
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    Assert.isTrue(exception == null, "Exception must be null");
  }

  // Test metadata file import, w/o data set
  @Test
  public void geneInfoImportTest() throws Exception {
    
    geneRepository.deleteAll();
    Assert.isTrue(geneRepository.count() == 0);
    long dsCount = dataSetRepository.count();
    long dfCount = dataFileRepository.count();
    Assert.isTrue(!dataFileRepository.findByFilePath(geneInfoFile.getFile().getAbsolutePath()).isPresent());
    
    ImportFileCommandParameters parameters = new ImportFileCommandParameters();
    parameters.setDataType("entrez_gene");
    parameters.setFilePath(geneInfoFile.getFile().getAbsolutePath());
    Exception exception = null;
    
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    
    Assert.isTrue(exception == null, "Exception should be null");
    Assert.isTrue(geneRepository.count() > 0, "GeneRepository should not be empty");
    Assert.isTrue(dataSetRepository.count() == dsCount+1, "DataSet record count should have iterated by one");
    Assert.isTrue(dataFileRepository.count() == dfCount+1, "DaatFile record count should have iterated by one");
    
    Optional<DataSet> dataSetOptional = dataSetRepository.findByShortName(dataImportProperties.getDataSet().getShortName());
    Assert.isTrue(dataSetOptional.isPresent());
    DataSet dataSet = dataSetOptional.get();
    Assert.isTrue(dataImportProperties.getDataSet().getDisplayName().equals(dataSet.getDisplayName()));

    Optional<DataFile> dataFileOptional = dataFileRepository.findByFilePath(geneInfoFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataFile dataFile = dataFileOptional.get();
    Assert.isTrue(geneInfoFile.getFile().getAbsolutePath().equals(dataFile.getFilePath()));
    Assert.isTrue(Gene.class.equals(dataFile.getModelType()));
    Assert.isTrue("entrez_gene".equals(dataFile.getDataType()));
    Assert.isTrue(dataSet.getId().equals(dataFile.getDataSetId()));
    
  }
  
  // Test normal data file import
  @Test
  public void geneExpressionImportTest() throws Exception {

    geneExpressionRepository.deleteAll();
    Assert.isTrue(geneExpressionRepository.count() == 0);
    
    DataSet dataSet = new DataSet();
    dataSet.setShortName("example");
    dataSet.setDisplayName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add(sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByShortName("example").isPresent());

    ImportFileCommandParameters parameters = new ImportFileCommandParameters();
    parameters.setDataType("gct_gene_expression");
    parameters.setFilePath(gctGeneExpressionFile.getFile().getAbsolutePath());
    parameters.setDataSetKey("example");
    parameters.setSkipInvalidGenes(true);
    parameters.setSkipInvalidSamples(true);
    Exception exception = null;

    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "Exception should be null");
    
    Optional<DataFile> dataFileOptional = dataFileRepository
        .findByFilePath(gctGeneExpressionFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataFile dataFile = dataFileOptional.get();
    Assert.isTrue(GeneExpression.class.equals(dataFile.getModelType()));
    Assert.isTrue("gct_gene_expression".equals(dataFile.getDataType()));

    Assert.isTrue(geneExpressionRepository.count() == 25, 
        String.format("Expected 25, was %d", geneExpressionRepository.count()));
    List<GeneExpression> data = (List<GeneExpression>) geneExpressionRepository.findAll();
    GeneExpression geneExpression = data.get(0);
    Assert.notNull(geneExpression.getValue());
    Assert.isTrue(geneExpression.getValue() > 0);
    Assert.isTrue(dataFile.getId().equals(geneExpression.getDataFileId()));
    Assert.isTrue(dataSet.getId().equals(geneExpression.getDataSetId()));
    
    System.out.println(geneExpression.toString());

  }
  
  // test import of file w/ sample specified
  // test changing dataImportProperties
  // Test file overwrite with changes
  // test file overwrite with no changes
  // test import of non-existent file
  // test import of bad data type
  // test import of non-existing data set
  // test import of sample data with non-existent sample
  // Test import with each flag set to make sure exceptions are triggered
  
}
