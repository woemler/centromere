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

package org.oncoblocks.centromere.mongodb.test.registry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.oncoblocks.centromere.mongodb.CentromereMongoRepositoryComponentFactory;
import org.oncoblocks.centromere.mongodb.commons.models.MongoGene;
import org.oncoblocks.centromere.mongodb.test.commons.MongoCommonsDataSourceTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoCommonsDataSourceTestConfig.class })
public class MongoRepositoryRegistryTests {
	
	@Autowired private MongoTemplate mongoTemplate;
	
	@Test
	public void setupTest() throws Exception {
		Assert.notNull(mongoTemplate);
		CentromereMongoRepositoryComponentFactory factory = new CentromereMongoRepositoryComponentFactory();
		factory.setMongoTemplate(mongoTemplate);
		Assert.notNull(mongoTemplate);
		Assert.notNull(factory.getMongoTemplate());
		RepositoryOperations repository = factory.getComponent(MongoGene.class);
		Assert.notNull(repository);
		Assert.notNull(repository.getModel());
		Assert.isTrue(MongoGene.class.equals(repository.getModel()));
	}
	
}
