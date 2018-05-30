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

package com.blueprint.centromere.tests.core.test;

import com.blueprint.centromere.core.config.CoreConfiguration;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.config.MongoConfiguration;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
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
public class ConfigurationTests {

  @Autowired(required = false) private ModelRepositoryRegistry repositoryRegistry;
  @Autowired(required = false) private ModelResourceRegistry resourceRegistry;

  @Test
  public void modelResourceTest() throws Exception {
    Assert.notNull(resourceRegistry, "ResourceRegistrymust not be null");
    Assert.notNull(repositoryRegistry, "RepositoryRegistry must not be null");
    System.out.println(resourceRegistry.getRegisteredModels().toString());
    System.out.println(repositoryRegistry.getRegisteredModelRepositories().toString());
    Assert.isTrue(resourceRegistry.isRegisteredResource("gene"));
    Assert.isTrue(resourceRegistry.isRegisteredModel(Gene.class));
    ModelRepository repository = repositoryRegistry.getRepositoryByModel(Gene.class);
    Assert.notNull(repository, "GeneRepository is not registered.");
    Assert.isTrue(repository instanceof GeneRepository, "Repository does not implement GeneRepository");
  }

}
