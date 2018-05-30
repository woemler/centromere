package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.MongoConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.dataimport.processor.impl.MafMutationProcessor;
import com.blueprint.centromere.core.dataimport.reader.impl.MafSourceReader;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Mutation;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.MutationRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class MutationTests extends AbstractRepositoryTests {
  
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private DataSourceRepository dataSourceRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private MutationRepository mutationRepository;
  
  private static final Resource mafFile = new ClassPathResource("samples/mutations.maf");

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    mutationRepository.deleteAll();
  }

  @Test
  public void mafReaderTest() throws Exception {
    
    List<Mutation> mutations = new ArrayList<>();
    
    DataSet dataSet = dataSetRepository.findByDataSetId("DataSetA").get();
    Assert.notNull(dataSet);
    DataSource dataSource = new DataSource();
    dataSource.setDataSetId((String) dataSet.getId());
    dataSource.setSource(mafFile.getFile().getAbsolutePath());
    dataSource.setModel(Mutation.class);
    dataSourceRepository.insert(dataSource);
    
    DataImportProperties properties = new DataImportProperties();
    properties.setSkipInvalidSamples(true);
    properties.setSkipInvalidGenes(true);
    
    MafSourceReader reader = new MafSourceReader(geneRepository, sampleRepository, properties);
    reader.setDataSource(dataSource);
    reader.setDataSet(dataSet);
    reader.doBefore();
    
    Mutation mutation = reader.readRecord();
    System.out.println(mutation != null ? mutation.toString() : "null");
    while (mutation != null){
      System.out.println(mutation.toString());
      mutations.add(mutation);
      mutation = reader.readRecord();
    }
    
    reader.doAfter();
    
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
    
    Assert.notNull(reader.getSamples());
    Assert.notEmpty(reader.getSamples());
    Assert.isTrue(reader.getSamples().size() == 5);
    
  }

  @Test
  public void mafProcessorTest() throws Exception {
    
    Assert.isTrue(mutationRepository.count() == 0);

    DataSet dataSet = dataSetRepository.findByDataSetId("DataSetA").get();
    Assert.notNull(dataSet);
    DataSource dataSource = new DataSource();
    dataSource.setDataSetId(dataSet.getId());
    dataSource.setSource(mafFile.getFile().getAbsolutePath());
    dataSource.setModel(Mutation.class);
    dataSourceRepository.insert(dataSource);

    DataImportProperties properties = new DataImportProperties();
    properties.setSkipInvalidSamples(true);
    properties.setSkipInvalidGenes(true);

    MafMutationProcessor processor = new MafMutationProcessor(geneRepository, mutationRepository, sampleRepository, properties);
    processor.setDataImportProperties(properties);
    processor.setDataSet(dataSet);
    processor.setDataSource(dataSource);
    processor.setDataSourceRepository(dataSourceRepository);
    processor.setDataSetRepository(dataSetRepository);
    processor.doBefore();
    processor.run();
    processor.doAfter();
    
    Assert.isTrue(!processor.isInFailedState());
    Assert.isTrue(processor.isSupportedDataType("maf_mutation"));

    Assert.isTrue(mutationRepository.count() > 0);
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

}
