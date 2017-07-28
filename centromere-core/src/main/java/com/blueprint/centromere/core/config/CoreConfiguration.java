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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.repository.MongoModelRepository;
import com.blueprint.centromere.core.repository.MongoModelRepositoryFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configures required components, such as repositories and data import beans.
 *
 * @author woemler
 */
public class CoreConfiguration {

	@Configuration
	public static class DefaultModelConfiguration {

	  @Profile({ Profiles.SCHEMA_DEFAULT })
		@Configuration
		@EnableMongoRepositories(
		    basePackages = {"com.blueprint.centromere.core.commons.repository"},
				repositoryBaseClass = MongoModelRepository.class,
				repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
		public static class DefaultMongoSchemaConfiguration { }

		
    @Profile({ Profiles.CLI_PROFILE })
		@Configuration
    @Import({ DataImportProperties.class })
		@ComponentScan(basePackages = {
				"com.blueprint.centromere.core.commons.reader",
				"com.blueprint.centromere.core.commons.processor",
        "com.blueprint.centromere.core.commons.support"
		})
		public static class CommandLineComponentConfiguration { }
		
		@Profile({ Profiles.WEB_PROFILE })
    @Configuration
    @Import({ WebProperties.class })
    public static class WebComponentConfiguration { }

	}
  
  @Configuration
  @Import({ DatabaseProperties.class })
  public static class CommonConfiguration {

    @Bean
    public ModelRepositoryRegistry modelRepositoryRegistry(ApplicationContext applicationContext){
      return new ModelRepositoryRegistry(applicationContext);
    }

  }

}
