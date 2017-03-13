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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.commons.support.TcgaSupport;
import com.blueprint.centromere.core.repository.MongoModelRepository;
import com.blueprint.centromere.core.repository.MongoModelRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author woemler
 */
public class ProfileConfiguration {
	
	@Profile({ "default", Schema.CUSTOM_PROFILE })
	@Configuration
	public static class DefaultModelConfiguration {
	}

	@Profile({ Schema.DEFAULT_PROFILE })
	@Configuration
	public static class DefaultSchemaConfiguration {

		@Bean
    public TcgaSupport tcgaSupport(){
		  return new TcgaSupport();
    }
		
		@Profile({ Database.MONGODB_PROFILE, Database.EMBEDDED_MONGODB_PROFILE })
		@Configuration
		@EnableMongoRepositories(basePackages = { "com.blueprint.centromere.core.commons.repositories" },
				repositoryBaseClass = MongoModelRepository.class, 
				repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
		public static class DefaultMongoSchemaConfiguration { }

		@Profile({ Profiles.CLI_PROFILE })
		@Configuration
		@ComponentScan(basePackages = { 
				"com.blueprint.centromere.core.commons.readers", 
				"com.blueprint.centromere.core.commons.processors" 
		})
		public static class CommandLineComponentConfiguration { }
		
	}
	
	
}
