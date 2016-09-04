/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.core.test.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.config.DataTypeProcessorBeanRegistry;
import org.oncoblocks.centromere.core.config.ModelRegistry;
import org.oncoblocks.centromere.core.config.ModelRepositoryBeanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConfigurationTestConfig.class })
public class ComponentConfigurationTests {
	
	@Autowired private ModelRegistry modelRegistry;
	@Autowired private ApplicationContext context;
	@Autowired private ModelRepositoryBeanRegistry repositoryBeanRegistry;
	@Autowired private DataTypeProcessorBeanRegistry processorBeanRegistry;
	
	@Test
	public void modelRegistryTest() throws Exception {
		Assert.notNull(modelRegistry);
		Assert.notNull(modelRegistry.getModels());
		Assert.isTrue(ExampleModel.class.equals(modelRegistry.getModels().get(0)));
		Assert.isTrue(modelRegistry.isSupported(ExampleModel.class));
	}
	
	@Test
	public void repositoryRegistryTest() throws Exception {
		Assert.notNull(repositoryBeanRegistry);
		Assert.isTrue(repositoryBeanRegistry.isRegistered(ExampleModel.class));
		Assert.notNull(repositoryBeanRegistry.get(ExampleModel.class));
		Assert.isTrue(repositoryBeanRegistry.get(ExampleModel.class) instanceof ExampleRepository);
	}
	
	@Test
	public void processorRegistryTest() throws Exception {
		Assert.notNull(processorBeanRegistry);
		Assert.isTrue(processorBeanRegistry.isRegistered(ExampleModel.class));
		Assert.isTrue(processorBeanRegistry.isSupportedDataType("example_data"));
		Assert.notNull(processorBeanRegistry.get(ExampleModel.class));
		Assert.isTrue(processorBeanRegistry.get(ExampleModel.class) instanceof ExampleProcessor);
		Assert.notNull(processorBeanRegistry.getByDataType("example_data"));
		Assert.isTrue(processorBeanRegistry.getByDataType("example_data") instanceof ExampleProcessor);
	}
	
}
