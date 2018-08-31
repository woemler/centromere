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

package com.blueprint.centromere.tests.cli.test;

import com.blueprint.centromere.cli.JCommanderInputExecutor;
import com.blueprint.centromere.cli.ModelProcessorBeanRegistry;
import com.blueprint.centromere.cli.dataimport.processor.impl.EntrezGeneInfoProcessor;
import com.blueprint.centromere.cli.dataimport.processor.impl.GenericSampleProcessor;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.GeneExpressionRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.tests.cli.CommandLineTestInitializer;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommandLineTestInitializer.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({ Profiles.SCHEMA_DEFAULT, Profiles.CLI_PROFILE, CommandLineTestInitializer.SINGLE_COMMAND_PROFILE })
@FixMethodOrder
public class CommandLineConfigTests {
	
	@Autowired(required = false) private ModelProcessorBeanRegistry registry;
	@Autowired(required = false) private JCommanderInputExecutor executor;
	@Autowired(required = false) private GeneRepository geneRepository;
	@Autowired(required = false) private GeneExpressionRepository geneExpressionRepository;
	@Autowired(required = false) private DataSourceRepository dataSourceRepository;
	@Autowired(required = false) private GenericSampleProcessor sampleProcessor;
  @Autowired(required = false) private EntrezGeneInfoProcessor geneInfoProcessor;
	
	@Before
	public void setup(){
	  dataSourceRepository.deleteAll();
		geneExpressionRepository.deleteAll();
		geneRepository.deleteAll();
	}
	
	@Test
	public void modelProcessorRegistryTest(){
		Assert.notNull(registry);
		Assert.isTrue(registry.isSupportedDataType("entrez_gene"));
		Assert.isTrue(registry.isSupportedModel(Gene.class));
		Assert.isTrue(!registry.isSupportedDataType("samples"));
    Assert.isTrue(registry.isSupportedDataType("generic_samples"));
		Assert.isTrue(registry.isSupportedModel(Sample.class));
	}
	
	@Test
  public void repositoryTest(){
	  Assert.notNull(geneRepository);
	  Assert.notNull(geneExpressionRepository);
	  Assert.notNull(dataSourceRepository);
  }
  
  @Test
  public void executorTest(){
	  Assert.notNull(executor);
  }
  
  @Test
  public void processorTest(){
	  Assert.notNull(sampleProcessor);
	  Assert.notNull(geneInfoProcessor);
  }

}
