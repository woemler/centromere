package com.blueprint.centromere.tests.cli.test.executor;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.JCommanderInputExecutor;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.GeneExpression;
import com.blueprint.centromere.core.model.impl.Mutation;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneExpressionRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.MutationRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
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
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT, Profiles.CLI_PROFILE, CentromereCommandLineInitializer.SINGLE_COMMAND_PROFILE })
@FixMethodOrder
public class ImportArgExecutionTests extends AbstractRepositoryTests {
  
  private static final Resource geneInfoFile = new ClassPathResource("samples/Homo_sapiens.gene_info");
  private static final Resource gctGeneExpressionFile = new ClassPathResource("samples/gene_expression.gct");
  private static final Resource mafFile = new ClassPathResource("samples/mutations.maf");

  @Autowired private JCommanderInputExecutor executor;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataSourceRepository dataSourceRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private GeneExpressionRepository geneExpressionRepository;
  @Autowired private MutationRepository mutationRepository;
  
  @Before
  @Override
  public void setup() throws Exception {
    super.setup();
    mutationRepository.deleteAll();
  }
  
  @Test
  public void helpTest(){
    String[] args = { "-h" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;
    try {
      executor.run(arguments);
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
    long dfCount = dataSourceRepository.count();
    Assert.isTrue(!dataSourceRepository.findBySource(geneInfoFile.getFile().getAbsolutePath()).isPresent());
    
    String[] args = { "import", "-t", "entrez_gene", "-f", geneInfoFile.getFile().getAbsolutePath(), 
        "-d", "DataSetA", "--skip-invalid-samples", "--skip-invalid-genes", "-Dsource=Entrez Gene" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;
    
    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }
    
    Assert.isTrue(exception == null, "Exception should be null");
    Assert.isTrue(geneRepository.count() > 0, "GeneRepository should not be empty");
    Assert.isTrue(dataSourceRepository.count() == dfCount+1, "DaatFile record count should have iterated by one");

    Optional<DataSource> dataFileOptional = dataSourceRepository
        .findBySource(geneInfoFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataSource dataSource = dataFileOptional.get();
    Assert.isTrue(geneInfoFile.getFile().getAbsolutePath().equals(dataSource.getSource()));
    Assert.isTrue(Gene.class.isAssignableFrom(dataSource.getModelType()));
    Assert.isTrue("entrez_gene".equals(dataSource.getDataType()));
    Assert.isTrue(!dataSource.getAttributes().isEmpty());
    Assert.isTrue(dataSource.getAttributes().containsKey("source"));
    Assert.isTrue("Entrez Gene".equals(dataSource.getAttribute("source")));

    Optional<DataSet> dataSetOptional = dataSetRepository.findById(dataSource.getDataSetId());
    Assert.isTrue(dataSetOptional.isPresent());
    DataSet dataSet = dataSetOptional.get();
    Assert.isTrue("DataSetA".equals(dataSet.getDataSetId()));
    Assert.isTrue(dataSet.getDataSourceIds().contains(dataSource.getId()));
    
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
    for (Sample sample: (List<Sample>) sampleRepository.findAll()){
      sampleIds.add(sample.getSampleId());
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

    String[] args = { "import", "-t", "gct_gene_expression", "-f", gctGeneExpressionFile.getFile().getAbsolutePath(),
        "-d", "example", "--skip-invalid-samples", "--skip-invalid-genes" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;

    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "Exception should be null");
    
    Optional<DataSource> dataFileOptional = dataSourceRepository
        .findBySource(gctGeneExpressionFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataSource dataSource = dataFileOptional.get();
    Assert.isTrue(GeneExpression.class.isAssignableFrom(dataSource.getModelType()));
    Assert.isTrue("gct_gene_expression".equals(dataSource.getDataType()));

    Assert.isTrue(geneExpressionRepository.count() == 25, 
        String.format("Expected 25, was %d", geneExpressionRepository.count()));
    List<GeneExpression> data = (List<GeneExpression>) geneExpressionRepository.findAll();
    GeneExpression geneExpression = data.get(0);
    Assert.notNull(geneExpression.getValue());
    Assert.isTrue(geneExpression.getValue() > 0);
    Assert.isTrue(dataSource.getId().equals(geneExpression.getDataSourceId()));
    Assert.isTrue(dataSet.getId().equals(geneExpression.getDataSetId()));
    
    System.out.println(geneExpression.toString());

  }

  @Test
  public void mafMutationImportTest() throws Exception {

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: (List<Sample>) sampleRepository.findAll()){
      sampleIds.add(sample.getSampleId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());
    
    Assert.isTrue(!dataSourceRepository.findBySource(mafFile.getFile().getAbsolutePath()).isPresent());

    String[] args = { "import", "-t", "maf_mutation", "-f", mafFile.getFile().getAbsolutePath(),
        "-d", "example", "--skip-invalid-samples", "--skip-invalid-genes" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;

    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.isTrue(exception == null, "Exception should be null");

    Optional<DataSource> dataFileOptional = dataSourceRepository
        .findBySource(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(dataFileOptional.isPresent());
    DataSource dataSource = dataFileOptional.get();
    Assert.isTrue(Mutation.class.isAssignableFrom(dataSource.getModelType()));
    Assert.isTrue("maf_mutation".equals(dataSource.getDataType()));

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
    Assert.isTrue("GeneA".equals(geneOptional.get().getSymbol()));
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
    Assert.isTrue("GeneE".equals(geneOptional.get().getSymbol()));
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

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: (List<Sample>) sampleRepository.findAll()){
      sampleIds.add(sample.getSampleId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    String[] args = { "import", "-t", "maf_mutation", "-f", mafFile.getFile().getAbsolutePath(),
        "-d", "example", "--skip-invalid-genes" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;

    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was " 
        + exception.getClass().getSimpleName());
    Assert.isTrue(exception.getCause().getCause() instanceof InvalidSampleException);

    Optional<DataSource> dataFileOptional = dataSourceRepository
        .findBySource(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());
    
    Assert.isTrue(mutationRepository.count() == 0);

  }
  
  // Test file overwrite with changes
  //TODO
  
  // test file overwrite with no changes
  //TODO
  
  // test import of non-existent file
  // TODO: What about when you aren't importing files?
//  @Test
//  public void nonexistentFileImportTest() throws Exception {
//
//    DataSet dataSet = new MongoDataSet();
//    dataSet.setDataSetId("example");
//    dataSet.setName("Example data set");
//    List<String> sampleIds = new ArrayList<>();
//    for (Sample sample: (List<Sample>) sampleRepository.findAll()){
//      sampleIds.add(sample.getSampleId());
//    }
//    dataSet.setSampleIds(sampleIds);
//    dataSetRepository.insert(dataSet);
//    Assert.notNull(dataSet.getId());
//    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());
//
//    String[] args = { "import", "-t", "maf_mutation", "-f", "/path/to/no/file",
//        "-d", "example", "--skip-invalid-samples", "--skip-invalid-genes" };
//    ApplicationArguments arguments = new DefaultApplicationArguments(args);
//    Exception exception = null;
//
//    try {
//      executor.run(arguments);
//    } catch (Exception e){
//      e.printStackTrace();
//      exception = e;
//    }
//
//    Assert.notNull(exception, "Exception should not be null");
//    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was "
//        + exception.getClass().getSimpleName());
//    Assert.isTrue(exception.getCause().getCause() instanceof FileNotFoundException);
//
//    Optional<DataFile> dataFileOptional = dataFileRepository
//        .findByFilePath(mafFile.getFile().getAbsolutePath());
//    Assert.isTrue(!dataFileOptional.isPresent());
//
//    Assert.isTrue(mutationRepository.count() == 0);
//
//  }
  
  // test import of bad data type
  @Test
  public void invalidDataTypeTest() throws Exception {

    DataSet dataSet = new DataSet();
    dataSet.setDataSetId("example");
    dataSet.setName("Example data set");
    List<String> sampleIds = new ArrayList<>();
    for (Sample sample: (List<Sample>) sampleRepository.findAll()){
      sampleIds.add(sample.getSampleId());
    }
    dataSet.setSampleIds(sampleIds);
    dataSetRepository.insert(dataSet);
    Assert.notNull(dataSet.getId());
    Assert.isTrue(dataSetRepository.findByDataSetId("example").isPresent());

    String[] args = { "import", "-t", "bad_type", "-f", mafFile.getFile().getAbsolutePath(),
        "-d", "example", "--skip-invalid-samples", "--skip-invalid-genes" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;

    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was "
        + exception.getClass().getSimpleName());

    Optional<DataSource> dataFileOptional = dataSourceRepository
        .findBySource(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());

    Assert.isTrue(mutationRepository.count() == 0);

  }
  
  // test import of non-existing data set
  @Test
  public void invalidDataSetTest() throws Exception {

    ImportCommandParameters parameters = new ImportCommandParameters();
    parameters.setDataType("maf_mutation");
    parameters.setFilePath(mafFile.getFile().getAbsolutePath());
    parameters.setDataSetId("bad-data-set");
    parameters.setSkipInvalidGenes(true);
    parameters.setSkipInvalidSamples(true);


    String[] args = { "import", "-t", "maf_mutation", "-f", mafFile.getFile().getAbsolutePath(),
        "-d", "bad-data-set", "--skip-invalid-samples", "--skip-invalid-genes" };
    ApplicationArguments arguments = new DefaultApplicationArguments(args);
    Exception exception = null;

    try {
      executor.run(arguments);
    } catch (Exception e){
      e.printStackTrace();
      exception = e;
    }

    Assert.notNull(exception, "Exception should not be null");
    Assert.isTrue(exception instanceof CommandLineRunnerException, "Expected CommandLineRunnerException, was "
        + exception.getClass().getSimpleName());

    Optional<DataSource> dataFileOptional = dataSourceRepository
        .findBySource(mafFile.getFile().getAbsolutePath());
    Assert.isTrue(!dataFileOptional.isPresent());

    Assert.isTrue(mutationRepository.count() == 0);

  }
  
}
