/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.tests.core.test.repository;

import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.MongoConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.core.repository.impl.SampleRepository;
import com.blueprint.centromere.tests.core.AbstractRepositoryTests;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CommonsModelRepositoryTests extends AbstractRepositoryTests {

  @Autowired private GeneRepository geneRepository;
  @Autowired private SampleRepository sampleRepository;
  @Autowired private DataSetRepository dataSetRepository;
  @Autowired private DataSourceRepository dataSourceRepository;
  
  @Test
  public void findUniqueGeneTest(){
    
    Optional<Gene> optional = geneRepository.findByGeneId("1");
    Assert.notNull(optional, "Optional must not be null");
    Assert.isTrue(optional.isPresent(), "Object must be present");
    Gene gene = optional.get();
    Assert.notNull(gene, "Object must not be null");
    Assert.isTrue("1".equals(gene.getGeneId()), "Primary ID must be '1'");
    
    optional = geneRepository.findByGeneId("1000");
    Assert.notNull(optional, "Optional must not be null");
    Assert.isTrue(!optional.isPresent(), "Object must not be present");
    
  }

  @Test
  public void findUniqueDataSetTest(){

    Optional<DataSet> optional = dataSetRepository.findByDataSetId("DataSetA");
    Assert.notNull(optional, "Optional must not be null");
    Assert.isTrue(optional.isPresent(), "Object must be present");
    DataSet dataSet = optional.get();
    Assert.notNull(dataSet, "Object must not be null");
    Assert.isTrue("DataSetA".equals(dataSet.getDataSetId()), "Primary ID must be 'DataSetA'");

    optional = dataSetRepository.findByDataSetId("DataSetZ");
    Assert.notNull(optional, "Optional must not be null");
    Assert.isTrue(!optional.isPresent(), "Object must not be present");

  }

  @Test
  public void findUniqueSubjectTest(){

    Optional<Sample> optional = sampleRepository.findBySampleId("SampleA");
    Assert.notNull(optional, "Optional must not be null");
    Assert.isTrue(optional.isPresent(), "Object must be present");
    Sample sample = optional.get();
    Assert.notNull(sample, "Object must not be null");
    Assert.isTrue("SampleA".equals(sample.getSampleId()), "Primary ID must be 'SampleA'");

    optional = sampleRepository.findBySampleId("SampleZ");
    Assert.notNull(optional, "Optional must not be null");
    Assert.isTrue(!optional.isPresent(), "Object must not be present");

  }

}
