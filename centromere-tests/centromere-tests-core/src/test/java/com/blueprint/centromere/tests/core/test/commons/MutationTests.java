package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.processor.MafMutationProcessor;
import com.blueprint.centromere.core.commons.reader.MafFileReader;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.mongodb.MongoConfiguration;
import com.blueprint.centromere.core.mongodb.model.MongoDataFile;
import com.blueprint.centromere.core.mongodb.model.MongoDataSet;
import com.blueprint.centromere.core.mongodb.model.MongoGene;
import com.blueprint.centromere.core.mongodb.model.MongoMutation;
import com.blueprint.centromere.core.mongodb.model.MongoSample;
import com.blueprint.centromere.core.mongodb.repository.MongoDataFileRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoDataSetRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoMutationRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoSampleRepository;
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
  
  @Autowired private MongoSampleRepository sampleRepository;
  @Autowired private MongoGeneRepository geneRepository;
  @Autowired private MongoDataFileRepository dataFileRepository;
  @Autowired private MongoDataSetRepository dataSetRepository;
  @Autowired private MongoMutationRepository mutationRepository;
  
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
    
    MongoDataSet dataSet = dataSetRepository.findByDataSetId("DataSetA").get();
    Assert.notNull(dataSet);
    MongoDataFile dataFile = new MongoDataFile();
    dataFile.setDataSetId((String) dataSet.getId());
    dataFile.setFilePath(mafFile.getFile().getAbsolutePath());
    dataFile.setModel(Mutation.class);
    dataFileRepository.insert(dataFile);
    
    DataImportProperties properties = new DataImportProperties();
    properties.setSkipInvalidSamples(true);
    properties.setSkipInvalidGenes(true);
    
    MafFileReader<MongoMutation> reader = new MafFileReader<>(MongoMutation.class, geneRepository, sampleRepository, properties);
    reader.setDataFile(dataFile);
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
    Optional<MongoSample> sampleOptional = sampleRepository.findById(record.getSampleId());
    Assert.isTrue(sampleOptional.isPresent());
    Assert.isTrue("SampleB".equals(sampleOptional.get().getSampleId()));
    Assert.notNull(record.getGeneId());
    Optional<MongoGene> geneOptional = geneRepository.findById(record.getGeneId());
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

    MongoDataSet dataSet = dataSetRepository.findByDataSetId("DataSetA").get();
    Assert.notNull(dataSet);
    MongoDataFile dataFile = new MongoDataFile();
    dataFile.setDataSetId(dataSet.getId());
    dataFile.setFilePath(mafFile.getFile().getAbsolutePath());
    dataFile.setModel(Mutation.class);
    dataFileRepository.insert(dataFile);

    DataImportProperties properties = new DataImportProperties();
    properties.setSkipInvalidSamples(true);
    properties.setSkipInvalidGenes(true);

    MafMutationProcessor<MongoMutation, String> processor = new MafMutationProcessor<>(MongoMutation.class, geneRepository, mutationRepository, sampleRepository, properties);
    processor.setDataImportProperties(properties);
    processor.setDataSet(dataSet);
    processor.setDataFile(dataFile);
    processor.setDataFileRepository(dataFileRepository);
    processor.setDataSetRepository(dataSetRepository);
    processor.doBefore();
    processor.run();
    processor.doAfter();
    
    Assert.isTrue(!processor.isInFailedState());
    Assert.isTrue(processor.isSupportedDataType("maf_mutation"));

    Assert.isTrue(mutationRepository.count() > 0);
    List<MongoMutation> mutations = (List<MongoMutation>) mutationRepository.findAll();
    Assert.notEmpty(mutations);
    Assert.isTrue(mutations.size() == 8);

    Mutation record = mutations.get(0);
    Assert.notNull(record.getSampleId());
    Optional<MongoSample> sampleOptional = sampleRepository.findById(record.getSampleId());
    Assert.isTrue(sampleOptional.isPresent());
    Assert.isTrue("SampleB".equals(sampleOptional.get().getSampleId()));
    Assert.notNull(record.getGeneId());
    Optional<MongoGene> geneOptional = geneRepository.findById(record.getGeneId());
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
