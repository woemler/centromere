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

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
		
		@Profile({ Database.GENERIC_JPA_PROFILE, Database.MYSQL_PROFILE })
		@Configuration
		@EnableJpaRepositories(basePackages = { "com.blueprint.centromere.core.commons.repositories" })
		@EnableTransactionManagement
		public static class DefaultJpaSchemaConfiguration { }

		@Profile({ Database.MONGODB_PROFILE })
		@Configuration
		@EnableMongoRepositories(basePackages = { "com.blueprint.centromere.core.commons.repositories" })
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
