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

package com.blueprint.centromere.cli;

import com.blueprint.centromere.core.config.Profiles;

import com.blueprint.centromere.core.config.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author woemler
 */
@Configuration
@PropertySources({
		@PropertySource({"classpath:centromere-defaults.properties"}),
		@PropertySource(value = {"classpath:centromere.properties"},ignoreResourceNotFound = true)
})
@Profile({ Profiles.CLI_PROFILE })
public class CommandLineInputConfiguration {
	
	@Bean
	public ModelProcessorBeanRegistry modelProcessorBeanRegistry(){
		return new ModelProcessorBeanRegistry();
	}
	
	@Bean
	public CommandLineInputExecutor commandLineInputExecutor(){
		return new CommandLineInputExecutor();
	}
	
	@Bean
	public FileImportExecutor fileImportExecutor(){
		return new FileImportExecutor();
	}
	
	@Bean
	public ManifestImportExecutor manifestImportExecutor(){
		return new ManifestImportExecutor();
	}

	@Profile({Schema.DEFAULT_PROFILE})
  @ComponentScan(basePackages = {
      "com.blueprince.centromere.core.commons.processors",
      "com.blueprince.centromere.core.commons.readers"
  })
	public static class DefaultSchemaProcessorConfig {
  }
	
}
