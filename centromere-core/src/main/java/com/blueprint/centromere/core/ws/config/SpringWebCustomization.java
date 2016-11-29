/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.ws.config;

import com.blueprint.centromere.core.model.AbstractModel;
import com.blueprint.centromere.core.ws.controller.ModelController;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author woemler
 */
public class SpringWebCustomization {
	
	@Configuration
	@ComponentScan(basePackageClasses = { ModelController.class })
	@PropertySources({
			@PropertySource({"classpath:centromere-defaults.properties"}),
			@PropertySource(value = {"classpath:centromere.properties"},ignoreResourceNotFound = true)
	})
	public static class WebServicesConfig {

		@Bean
		public SpringDataRestCustomConfig springDataRestCustomConfig(){
			return new SpringDataRestCustomConfig();
		}

	}

	public static class SpringDataRestCustomConfig extends RepositoryRestConfigurerAdapter {
		
		@Autowired private Environment env;
		private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SpringDataRestCustomConfig.class);

		@Override 
		public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
			config.setBasePath(env.getRequiredProperty("centromere.api.root-url"));
			config.setReturnBodyForPutAndPost(true);
			config.exposeIdsFor(AbstractModel.class);
		}

		@Override
		public void configureHttpMessageConverters(List<HttpMessageConverter<?>> converters) {
			
			FilteringJackson2HttpMessageConverter jsonConverter
					= new FilteringJackson2HttpMessageConverter();
			jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_JSON_UTF8, RestMediaTypes.ALPS_JSON, RestMediaTypes.HAL_JSON, 
					RestMediaTypes.JSON_PATCH_JSON, RestMediaTypes.MERGE_PATCH_JSON, 
					RestMediaTypes.SPRING_DATA_COMPACT_JSON, RestMediaTypes.SPRING_DATA_VERBOSE_JSON));
			converters.add(jsonConverter);
			
			FilteringTextMessageConverter filteringTextMessageConverter =
					new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
			filteringTextMessageConverter.setDelimiter("\t");
			converters.add(filteringTextMessageConverter);

			super.configureHttpMessageConverters(converters);
			
		}
	}
	
}
