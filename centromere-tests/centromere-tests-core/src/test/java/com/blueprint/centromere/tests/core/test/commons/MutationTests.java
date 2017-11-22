package com.blueprint.centromere.tests.core.test.commons;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.processor.MafMutationProcessor;
import com.blueprint.centromere.core.commons.reader.MafFileReader;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.repository.MutationRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.DataImportProperties;
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
    CoreConfiguration.DefaultModelConfiguration.class
})
public class MutationTests extends AbstractRepositoryTests {
  
  @Autowired private SampleRepository sampleRepository;
  @Autowired private GeneRepository geneRepository;
  @Autowired private DataFileRepository dataFileRepository;
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
    
    DataSet dataSet = dataSetRepository.findBySlug("DataSetA").get();
    Assert.notNull(dataSet);
    DataFile dataFile = new DataFile();
    dataFile.setDataSetId(dataSet.getId());
    dataFile.setFilePath(mafFile.getFile().getAbsolutePath());
    dataFile.setModel(Mutation.class);
    dataFileRepository.insert(dataFile);
    
    DataImportProperties properties = new DataImportProperties();
    properties.setSkipInvalidSamples(true);
    properties.setSkipInvalidGenes(true);
    
    MafFileReader reader = new MafFileReader(geneRepository, sampleRepository, properties);
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
    Optional<Sample> sampleOptional = sampleRepository.findById(record.getSampleId());
    Assert.isTrue(sampleOptional.isPresent());
    Assert.isTrue("SampleB".equals(sampleOptional.get().getName()));
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
    Assert.isTrue("SampleD".equals(sampleOptional.get().getName()));
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
    
    Assert.notNull(reader.getSamples());
    Assert.notEmpty(reader.getSamples());
    Assert.isTrue(reader.getSamples().size() == 5);
    
  }

  @Test
  public void mafProcessorTest() throws Exception {
    
    Assert.isTrue(mutationRepository.count() == 0);

    DataSet dataSet = dataSetRepository.findBySlug("DataSetA").get();
    Assert.notNull(dataSet);
    DataFile dataFile = new DataFile();
    dataFile.setDataSetId(dataSet.getId());
    dataFile.setFilePath(mafFile.getFile().getAbsolutePath());
    dataFile.setModel(Mutation.class);
    dataFileRepository.insert(dataFile);

    DataImportProperties properties = new DataImportProperties();
    properties.setSkipInvalidSamples(true);
    properties.setSkipInvalidGenes(true);

    MafMutationProcessor processor = new MafMutationProcessor(geneRepository, mutationRepository, sampleRepository, properties);
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
    List<Mutation> mutations = (List<Mutation>) mutationRepository.findAll();
    Assert.notEmpty(mutations);
    Assert.isTrue(mutations.size() == 8);

    Mutation record = mutations.get(0);
    Assert.notNull(record.getSampleId());
    Optional<Sample> sampleOptional = sampleRepository.findById(record.getSampleId());
    Assert.isTrue(sampleOptional.isPresent());
    Assert.isTrue("SampleB".equals(sampleOptional.get().getName()));
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
    Assert.isTrue("SampleD".equals(sampleOptional.get().getName()));
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

}
