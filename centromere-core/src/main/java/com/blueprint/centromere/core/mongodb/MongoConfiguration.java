/*
 * Copyright 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.mongodb;

import com.blueprint.centromere.core.commons.processor.CcleGeneCopyNumberProcessor;
import com.blueprint.centromere.core.commons.processor.EntrezGeneInfoProcessor;
import com.blueprint.centromere.core.commons.processor.GctGeneExpressionProcessor;
import com.blueprint.centromere.core.commons.processor.GctTranscriptExpressionProcessor;
import com.blueprint.centromere.core.commons.processor.GenericGeneCopyNumberMatrixProcessor;
import com.blueprint.centromere.core.commons.processor.GenericSampleProcessor;
import com.blueprint.centromere.core.commons.processor.MafMutationProcessor;
import com.blueprint.centromere.core.commons.processor.ManagedTermProcessor;
import com.blueprint.centromere.core.commons.processor.SnpEffVcfProcessor;
import com.blueprint.centromere.core.commons.processor.TcgaGeneExpressionProcessor;
import com.blueprint.centromere.core.commons.processor.TcgaSampleProcessor;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.mongodb.model.MongoGene;
import com.blueprint.centromere.core.mongodb.model.MongoGeneCopyNumber;
import com.blueprint.centromere.core.mongodb.model.MongoGeneExpression;
import com.blueprint.centromere.core.mongodb.model.MongoMutation;
import com.blueprint.centromere.core.mongodb.model.MongoSample;
import com.blueprint.centromere.core.mongodb.model.MongoTerm;
import com.blueprint.centromere.core.mongodb.model.MongoTranscriptExpression;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneCopyNumberRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneExpressionRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoGeneRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoMutationRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoSampleRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoTermRepository;
import com.blueprint.centromere.core.mongodb.repository.MongoTranscriptExpressionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author woemler
 */
public class MongoConfiguration {
  
  @Configuration
  @Profile({ Profiles.SCHEMA_DEFAULT })
  @EnableMongoRepositories(
      basePackages = {"com.blueprint.centromere.core.mongodb.repository"},
      repositoryBaseClass = MongoModelRepository.class,
      repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
  public static class MongoRepositoryConfiguration { }

  @Profile({ Profiles.CLI_PROFILE })
  @Configuration
  @Import({ DataImportProperties.class })
  @SuppressWarnings("SpringJavaAutowiringInspection")
  public static class MongoCommandLineComponentConfiguration { 
    
    @Bean
    public ManagedTermProcessor<MongoTerm, String> managedTermProcessor(
        MongoTermRepository termRepository
    ){
      return new ManagedTermProcessor<>(MongoTerm.class, termRepository);
    }
    
    @Bean
    public EntrezGeneInfoProcessor<MongoGene, String> entrezGeneInfoProcessor(
        GeneRepository<MongoGene, String> geneRepository
    ){
      return new EntrezGeneInfoProcessor<>(MongoGene.class, geneRepository);
    }
    
    @Bean
    public GenericSampleProcessor<MongoSample, String> sampleProcessor(
        MongoSampleRepository sampleRepository, DataImportProperties dataImportProperties
    ){
      return new GenericSampleProcessor<>(MongoSample.class, sampleRepository, dataImportProperties);
    }
    
    @Bean
    public CcleGeneCopyNumberProcessor<MongoGeneCopyNumber, String> ccleGeneCopyNumberProcessor(
        MongoGeneRepository geneRepository,
        MongoSampleRepository sampleRepository,
        MongoGeneCopyNumberRepository geneCopyNumberRepository,
        DataImportProperties dataImportProperties
    ){
      return new CcleGeneCopyNumberProcessor<>(MongoGeneCopyNumber.class, geneRepository, 
          geneCopyNumberRepository, sampleRepository, dataImportProperties);
    }
    
    @Bean
    public GctGeneExpressionProcessor<MongoGeneExpression, String> gctGeneExpressionProcessor(
        MongoGeneRepository geneRepository,
        MongoSampleRepository sampleRepository,
        MongoGeneExpressionRepository geneExpressionRepsitory,
        DataImportProperties dataImportProperties
    ){
      return new GctGeneExpressionProcessor<>(MongoGeneExpression.class, geneRepository, 
          geneExpressionRepsitory, sampleRepository, dataImportProperties);
    }

    @Bean
    public GctTranscriptExpressionProcessor<MongoTranscriptExpression, String> gctTranscriptExpressionProcessor(
        MongoGeneRepository geneRepository,
        MongoSampleRepository sampleRepository,
        MongoTranscriptExpressionRepository transcriptExpressionRepository,
        DataImportProperties dataImportProperties
    ){
      return new GctTranscriptExpressionProcessor<>(MongoTranscriptExpression.class, geneRepository,
          transcriptExpressionRepository, sampleRepository, dataImportProperties);
    }
    
    @Bean
    public GenericGeneCopyNumberMatrixProcessor<MongoGeneCopyNumber, String> genericGeneCopyNumberMatrixProcessor(
        MongoGeneRepository geneRepository,
        MongoSampleRepository sampleRepository,
        MongoGeneCopyNumberRepository geneCopyNumberRepository,
        DataImportProperties dataImportProperties
    ){
      return new GenericGeneCopyNumberMatrixProcessor<>(
          MongoGeneCopyNumber.class, geneRepository, geneCopyNumberRepository, sampleRepository,
          dataImportProperties);
    }
    
    @Bean
    public MafMutationProcessor<MongoMutation, String> mafMutationProcessor(
        MongoGeneRepository geneRepository, 
        MongoSampleRepository sampleRepository,
        MongoMutationRepository mutationRepository,
        DataImportProperties dataImportProperties
    ){
      return new MafMutationProcessor<>(MongoMutation.class, geneRepository, mutationRepository, 
          sampleRepository, dataImportProperties);
    }
    
    @Bean
    public SnpEffVcfProcessor<MongoMutation, String> snpEffVcfProcessor(
        MongoGeneRepository geneRepository,
        MongoSampleRepository sampleRepository,
        MongoMutationRepository mutationRepository,
        DataImportProperties dataImportProperties
    ){
      return new SnpEffVcfProcessor<>(MongoMutation.class, geneRepository, sampleRepository,
          mutationRepository, dataImportProperties);
    }
    
    @Bean
    public TcgaGeneExpressionProcessor<MongoGeneExpression, String> tcgaGeneExpressionProcessor(
        MongoGeneRepository geneRepository,
        MongoSampleRepository sampleRepository,
        MongoGeneExpressionRepository geneExpressionRepsitory,
        DataImportProperties dataImportProperties
    ){
      return new TcgaGeneExpressionProcessor<>(MongoGeneExpression.class, geneRepository, 
          sampleRepository, dataImportProperties, geneExpressionRepsitory);
    }
    
    @Bean 
    public TcgaSampleProcessor<MongoSample, String> tcgaSampleProcessor(
        MongoSampleRepository sampleRepository
    ){
      return new TcgaSampleProcessor<>(MongoSample.class, sampleRepository);
    }
    
  }

}
