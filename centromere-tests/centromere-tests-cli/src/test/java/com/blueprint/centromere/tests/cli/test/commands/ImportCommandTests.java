package com.blueprint.centromere.tests.cli.test.commands;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.commands.ImportCommandExecutor;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.GeneExpression;
import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.MutationRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.io.FileNotFoundException;
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
  
  private static final Resource geneInfoFile = new ClassPathResource("samples/Homo_sapiens.gene_info");
  private static final Resource gctGeneExpressionFile = new ClassPathResource("samples/gene_expression.gct");
  private static final Resource mafFile = new ClassPathResource("samples/mutations.maf");

  @Autowired private ImportCommandExecutor executor;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataFileRepository dataFileRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private MutationRepository mutationRepository;
  
  @Test
  public void helpTest(){
    ImportCommandParameters parameters = new ImportCommandParameters();
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
    long dfCount = dataFileRepository.count();
    Assert.isTrue(!dataFileRepository.findByFilePath(geneInfoFile.getFile().getAbsolutePath()).isPresent());
    
    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("entrez_gene");
    parameters.setFilePath(geneInfoFile.getFile().getAbsolutePath());
    parameters.setDataSetId("DataSetA");
    Exception exception = null;
    
    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    
    Assert.isTrue(exception == null, "Exception should be null");
    Assert.isTrue(geneRepository.count() > 0, "GeneRepository should not be empty");
    Assert.isTrue(dataFileRepository.count() == dfCount+1, "DaatFile record count should have iterated by one");

    Optional<DataFile> dataFileOptional = dataFileRepository.findByFilePath(geneInfoFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataFile dataFile = dataFileOptional.get();
    Assert.isTrue(geneInfoFile.getFile().getAbsolutePath().equals(dataFile.getFilePath()));
    Assert.isTrue(Gene.class.equals(dataFile.getModelType()));
    Assert.isTrue("entrez_gene".equals(dataFile.getDataType()));

    Optional<DataSet> dataSetOptional = dataSetRepository.findById(dataFile.getDataSetId());
    Assert.isTrue(dataSetOptional.isPresent());
    DataSet dataSet = dataSetOptional.get();
    Assert.isTrue("DataSetA".equals(dataSet.getDataSetId()));
    Assert.isTrue(dataSet.getDataFileIds().contains(dataFile.getId()));
    
  }
  
  // Test normal data file import
  @Test
  public void geneExpressionImportTest() throws Exception {

    geneExpressionRepository.deleteAll();
    Assert.isTrue(geneExpressionRepository.count() == 0);
    
    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add(sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("gct_gene_expression");
    parameters.setFilePath(gctGeneExpressionFile.getFile().getAbsolutePath());
    parameters.setDataSetId("example");
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

  @Test
  public void mafMutationImportTest() throws Exception {

    mutationRepository.deleteAll();
    Assert.isTrue(mutationRepository.count() == 0);

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add(sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("maf_mutation");
    parameters.setFilePath(mafFile.getFile().getAbsolutePath());
    parameters.setDataSetId("example");
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
        .findByFilePath(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataFile dataFile = dataFileOptional.get();
    Assert.isTrue(Mutation.class.equals(dataFile.getModelType()));
    Assert.isTrue("maf_mutation".equals(dataFile.getDataType()));

    Assert.isTrue(mutationRepository.count() == 8,
        String.format("Expected 8, was %d", mutationRepository.count()));
    List<Mutation> mutations = (List<Mutation>) mutationRepository.findAll();
    Assert.notEmpty(mutations);
    Assert.isTrue(mutations.size() == 8);

    Mutation record = mutations.get(0);
    Assert.notNull(record.getSampleId());
    Optional<Sample> sampleOptional = sampleRepository.findById(record.getSampleId());
    Assert.isTrue(sampleOptional.isPresent());
    Assert.isTrue("SampleB".equals(sampleOptional.get().getSampleId()));
    Assert.notNull(record.getGeneId());
    Optional<Gene> geneOptional = geneRepository.findById(record.getGeneId());
    Assert.isTrue(geneOptional.isPresent());
    Assert.isTrue("GeneA".equals(geneOptional.get().getPrimaryGeneSymbol()));
    Assert.isTrue("Missense_Mutation".equals(record.getVariantClassification()));
    Assert.isTrue("SNP".equals(record.getVariantType()));
    Assert.isTrue(140994602 == record.getDnaStartPosition());
    Assert.isTrue(140994602 == record.getDnaStopPosition());
    Assert.isTrue("+".equals(record.getStrand()));
    Assert.isTrue("A".equals(record.getReferenceAllele()));
    Assert.isTrue("A/T".equals(record.getAlternateAllele()));
    Assert.isTrue("X".equals(record.getChromosome()));
    Assert.isTrue("37".equals(record.getAttribute("NCBI_Build")));

    record = mutations.get(7);
    Assert.notNull(record.getSampleId());
    sampleOptional = sampleRepository.findById(record.getSampleId());
    Assert.isTrue(sampleOptional.isPresent());
    Assert.isTrue("SampleD".equals(sampleOptional.get().getSampleId()));
    Assert.notNull(record.getGeneId());
    geneOptional = geneRepository.findById(record.getGeneId());
    Assert.isTrue(geneOptional.isPresent());
    Assert.isTrue("GeneE".equals(geneOptional.get().getPrimaryGeneSymbol()));
    Assert.isTrue("Nonsense_Mutation".equals(record.getVariantClassification()));
    Assert.isTrue("SNP".equals(record.getVariantType()));
    Assert.isTrue(22157034 == record.getDnaStartPosition());
    Assert.isTrue(22157034 == record.getDnaStopPosition());
    Assert.isTrue("+".equals(record.getStrand()));
    Assert.isTrue("G".equals(record.getReferenceAllele()));
    Assert.isTrue("G/A".equals(record.getAlternateAllele()));
    Assert.isTrue("19".equals(record.getChromosome()));
    Assert.isTrue("37".equals(record.getAttribute("NCBI_Build")));

  }
  
  // test import of file w/ sample specified
  //TODO
  
  // test changing dataImportProperties
  @Test
  public void invalidSampleTest() throws Exception {

    mutationRepository.deleteAll();
    Assert.isTrue(mutationRepository.count() == 0);

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add(sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("maf_mutation");
    parameters.setFilePath(mafFile.getFile().getAbsolutePath());
    parameters.setDataSetId("example");
    parameters.setSkipInvalidGenes(true);
    Exception exception = null;

    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was " 
        + exception.getClass().getSimpleName());
    Assert.isTrue(exception.getCause() instanceof InvalidSampleException);

    Optional<DataFile> dataFileOptional = dataFileRepository
        .findByFilePath(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());
    
    Assert.isTrue(mutationRepository.count() == 0);

  }
  
  // Test file overwrite with changes
  //TODO
  
  // test file overwrite with no changes
  //TODO
  
  // test import of non-existent file
  @Test
  public void nonexistentFileImportTest() throws Exception {

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add(sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("maf_mutation");
    parameters.setFilePath("/path/to/no/file");
    parameters.setDataSetId("example");
    parameters.setSkipInvalidGenes(true);
    parameters.setSkipInvalidSamples(true);
    Exception exception = null;

    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was "
        + exception.getClass().getSimpleName());
    Assert.isTrue(exception.getCause() instanceof FileNotFoundException);

    Optional<DataFile> dataFileOptional = dataFileRepository
        .findByFilePath(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());

    Assert.isTrue(mutationRepository.count() == 0);

  }
  
  // test import of bad data type
  @Test
  public void invalidDataTypeTest() throws Exception {

    mutationRepository.deleteAll();
    Assert.isTrue(mutationRepository.count() == 0);

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: sampleRepository.findAll()){
      sampleIds.add(sample.getId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("bad_type");
    parameters.setFilePath(mafFile.getFile().getAbsolutePath());
    parameters.setDataSetId("example");
    parameters.setSkipInvalidGenes(true);
    parameters.setSkipInvalidSamples(true);
    Exception exception = null;

    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was "
        + exception.getClass().getSimpleName());

    Optional<DataFile> dataFileOptional = dataFileRepository
        .findByFilePath(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());

    Assert.isTrue(mutationRepository.count() == 0);

  }
  
  // test import of non-existing data set
  @Test
  public void invalidDataSetTest() throws Exception {

    mutationRepository.deleteAll();
    Assert.isTrue(mutationRepository.count() == 0);

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("maf_mutation");
    parameters.setFilePath(mafFile.getFile().getAbsolutePath());
    parameters.setDataSetId("bad-data-set");
    parameters.setSkipInvalidGenes(true);
    parameters.setSkipInvalidSamples(true);
    Exception exception = null;

    try {
      executor.run(parameters);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was "
        + exception.getClass().getSimpleName());

    Optional<DataFile> dataFileOptional = dataFileRepository
        .findByFilePath(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());

    Assert.isTrue(mutationRepository.count() == 0);

  }
  
}
