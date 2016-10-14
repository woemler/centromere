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

package org.oncoblocks.centromere.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

/**
 * @author woemler
 */
public class ProfileConfiguration {
	
	@Profile({ "default", Profiles.SCHEMA_CUSTOM })
	@Configuration
	public static class DefaultModelConfiguration extends ModelComponentRegistrationConfigurer {
	}
	
	@Profile({ Profiles.SCHEMA_MONGODB_DEFAULT})
	@Configuration
	@ComponentScan(basePackages = { "org.oncoblocks.centromere.mongodb" },
			includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, 
					pattern =  ".+?DefaultMongoRepositoryConfig.DefaultSpringDataConfig.*"), useDefaultFilters = false)
	@ModelScan(basePackages = { "org.oncoblocks.centromere.mongodb.commons.models" })
	public static class DefaultMongoDbConfiguration extends ModelComponentRegistrationConfigurer {
		
	}
	
}
