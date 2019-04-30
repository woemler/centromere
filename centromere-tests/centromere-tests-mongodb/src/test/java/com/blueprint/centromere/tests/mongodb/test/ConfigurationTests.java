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

package com.blueprint.centromere.tests.mongodb.test;

import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;
import com.blueprint.centromere.tests.core.repositories.GeneRepository;
import com.blueprint.centromere.tests.mongodb.EmbeddedMongoDataSourceConfig;
import com.blueprint.centromere.tests.mongodb.MongoRepositoryConfig;
import com.blueprint.centromere.tests.mongodb.models.MongoGene;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
    EmbeddedMongoDataSourceConfig.class,
    MongoRepositoryConfig.class
})
public class ConfigurationTests {

    @Autowired(required = false)
    private ModelRepositoryRegistry repositoryRegistry;

    @Test
    public void modelResourceTest() throws Exception {
        Assert.assertNotNull(repositoryRegistry);
        System.out.println(repositoryRegistry.getRegisteredModelRepositories().toString());
        ModelRepository repository = repositoryRegistry.getRepositoryByModel(MongoGene.class);
        Assert.assertNotNull(repository);
        Assert.assertTrue(repository instanceof GeneRepository);
    }

}
