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
import org.oncoblocks.centromere.core.config.ModelRegistry;
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
	
	@Autowired private ModelRegistry registry;
	@Autowired private ApplicationContext context;
	
	@Test
	public void registryTest() throws Exception {
		Assert.notNull(registry);
		Assert.notNull(registry.getModels());
		Assert.isTrue(ExampleModel.class.equals(registry.getModels().get(0)));
	}
	
}
