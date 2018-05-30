/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.repository.MongoModelRepository;
import com.blueprint.centromere.core.repository.MongoModelRepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan;
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
      basePackages = {"com.blueprint.centromere.core.repository.impl"},
      repositoryBaseClass = MongoModelRepository.class,
      repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
  public static class MongoRepositoryConfiguration { }

  @Profile({ Profiles.CLI_PROFILE })
  @Configuration
  @Import({ DataImportProperties.class })
  @SuppressWarnings("SpringJavaAutowiringInspection")
  @ComponentScan(basePackages = { "com.blueprint.centromere.core.dataimport.processor.impl" })
  public static class MongoCommandLineComponentConfiguration {
    
  }

}
