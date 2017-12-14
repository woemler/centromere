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
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.mongodb.model.MongoGene;
import com.blueprint.centromere.core.mongodb.model.MongoSample;
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
public class CommandLineExecutorTests {
	
	@Autowired private ModelProcessorBeanRegistry registry;
	@Autowired private JCommanderInputExecutor executor;
	@Autowired private GeneRepository geneRepository;
	@Autowired private GeneExpressionRepository geneExpressionRepository;
	@Autowired private DataFileRepository dataFileRepository;
	
	@Before
	public void setup(){
	  dataFileRepository.deleteAll();
		geneExpressionRepository.deleteAll();
		geneRepository.deleteAll();
	}
	
	@Test
	public void modelProcessorRegistryTest(){
		Assert.notNull(registry);
		Assert.isTrue(registry.isSupportedDataType("entrez_gene"));
		Assert.isTrue(registry.isSupportedModel(MongoGene.class));
		Assert.isTrue(!registry.isSupportedDataType("samples"));
    Assert.isTrue(registry.isSupportedDataType("generic_samples"));
		Assert.isTrue(registry.isSupportedModel(MongoSample.class));
	}

}
