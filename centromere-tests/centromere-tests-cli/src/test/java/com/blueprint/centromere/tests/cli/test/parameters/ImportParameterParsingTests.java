package com.blueprint.centromere.tests.cli.test.parameters;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.parameters.BaseParameters;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ImportParameterParsingTests {
  
  private static final Resource exampleFile = new ClassPathResource("samples/Homo_sapiens.gene_info");

  @Test
  public void fileImportParameterTest() throws Exception {
    
    ImportCommandParameters parameters = new ImportCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { "import", "-f", exampleFile.getFile().getAbsolutePath(), "-t", "gene_info", 
        "-d", "1234", "--skip-invalid-genes", "-o", "-X", "--spring.profiles.active=test" };
    
    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand("import", parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    
    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue("import".equals(jc.getParsedCommand()), "Parsed command should be 'import'");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Import parameters cannot be null");
    Assert.notNull(parameters.getFilePath(), "File path must not be null");
    Assert.isTrue(exampleFile.getFile().getAbsolutePath().equals(parameters.getFilePath()), "File path does not match");
    Assert.notNull(parameters.getDataType(), "Data type must not be null");
    Assert.isTrue("gene_info".equals(parameters.getDataType()), "Data type should be 'gene_info'");
    Assert.isTrue(parameters.isOverwrite(), "Overwrite flag should be true");
    Assert.isTrue(parameters.isSkipInvalidGenes(), "Skip invalid genes should be true");
    Assert.isTrue(!parameters.isSkipInvalidFiles(), "Skip invalid files should be false");
    Assert.isTrue(!parameters.isSkipInvalidRecords(), "Skip invalid records should be false");
    Assert.isTrue(!parameters.isSkipInvalidSamples(), "Skip invalid samples should be false");
    
  }

  @Test
  public void fileImportMissingCommandTest() throws Exception {

    ImportCommandParameters parameters = new ImportCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { "-f", exampleFile.getFile().getAbsolutePath(), "-t", "gene_info",
        "--skip-invalid-genes", "-o", "-X", "--spring.profiles.active=test" };

    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand("import", parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue(jc.getParsedCommand() == null, "Parsed command should be null");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Import parameters cannot be null");
    Assert.isTrue(parameters.getFilePath() == null, "File path must be null");
    Assert.isTrue(parameters.getDataType() == null, "Data type must be null");
    Assert.isTrue(!parameters.isOverwrite(), "Overwrite flag should be false");
    Assert.isTrue(!parameters.isSkipInvalidGenes(), "Skip invalid genes should be false");
    Assert.isTrue(!parameters.isSkipInvalidFiles(), "Skip invalid files should be false");
    Assert.isTrue(!parameters.isSkipInvalidRecords(), "Skip invalid records should be false");
    Assert.isTrue(!parameters.isSkipInvalidSamples(), "Skip invalid samples should be false");

  }

  @Test
  public void fileImportInvalidCommandTest() throws Exception {

    ImportCommandParameters parameters = new ImportCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { "invalid", "-f", exampleFile.getFile().getAbsolutePath(), "-t", "gene_info",
        "--skip-invalid-genes", "-o", "-X", "--spring.profiles.active=test" };

    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand("import", parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue(jc.getParsedCommand() == null, "Parsed command should be null");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Import parameters cannot be null");
    Assert.isTrue(parameters.getFilePath() == null, "File path must be null");
    Assert.isTrue(parameters.getDataType() == null, "Data type must be null");
    Assert.isTrue(!parameters.isOverwrite(), "Overwrite flag should be false");
    Assert.isTrue(!parameters.isSkipInvalidGenes(), "Skip invalid genes should be false");
    Assert.isTrue(!parameters.isSkipInvalidFiles(), "Skip invalid files should be false");
    Assert.isTrue(!parameters.isSkipInvalidRecords(), "Skip invalid records should be false");
    Assert.isTrue(!parameters.isSkipInvalidSamples(), "Skip invalid samples should be false");

  }

  @Test
  public void fileImportMetadataArgTest() throws Exception {

    ImportCommandParameters parameters = new ImportCommandParameters();
    BaseParameters baseParameters = new BaseParameters();
    Exception exception = null;
    JCommander jc = null;
    String[] args = { "import", "-f", exampleFile.getFile().getAbsolutePath(), "-t", "gene_info",
       "--data-set", "test-data-set", "-s", "123" };

    try {
      jc = JCommander.newBuilder()
          .acceptUnknownOptions(true)
          .addObject(baseParameters)
          .addCommand("import", parameters)
          .build();
      jc.parse(args);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "No exception should be thrown");
    Assert.isTrue(!baseParameters.isHelp(), "Help should not be triggered.");
    Assert.isTrue("import".equals(jc.getParsedCommand()), "Parsed command should be 'import'");
    Assert.notNull(baseParameters, "BaseParameters cannot be null");
    Assert.notNull(parameters, "Import parameters cannot be null");
    Assert.notNull(parameters.getFilePath(), "File path must not be null");
    Assert.isTrue(exampleFile.getFile().getAbsolutePath().equals(parameters.getFilePath()), "File path does not match");
    Assert.notNull(parameters.getDataType(), "Data type must not be null");
    Assert.isTrue("gene_info".equals(parameters.getDataType()), "Data type should be 'gene_info'");
    Assert.notNull(parameters.getDataSetId(), "DataSet must not be null");
    Assert.isTrue("test-data-set".equals(parameters.getDataSetId()), "value should be 'test-data-set'");
    Assert.notNull(parameters.getSampleId(), "Sample key must not be null");
    Assert.isTrue("123".equals(parameters.getSampleId()), "Sample kye should be '123'");

  }
  
}
