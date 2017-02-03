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

import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.model.AbstractModel;
import com.blueprint.centromere.core.ws.controller.ModelController;
import com.blueprint.centromere.core.ws.controller.UserAuthenticationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author woemler
 */

@Configuration
@PropertySources({
		@PropertySource({"classpath:centromere-defaults.properties"}),
		@PropertySource(value = {"classpath:centromere.properties"},ignoreResourceNotFound = true)
})
@Import({ RepositoryRestMvcConfiguration.class, WebSecurityConfig.class })
@ComponentScan(basePackageClasses = { UserAuthenticationController.class, ModelController.class })
@Profile({ Profiles.WEB_PROFILE })
public class WebApplicationConfig {
	
	@Bean
	public RepositoryRestConfigurer springDataRestCustomConfig(){
		return new RepositoryRestConfigurerAdapter(){

			@Autowired private Environment env;
			
			@Override
			public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
				config.setBasePath(env.getRequiredProperty("centromere.api.root-url"));
				config.setReturnBodyForPutAndPost(true);
				config.exposeIdsFor(AbstractModel.class);
			}

			@Override
			public void configureHttpMessageConverters(List<HttpMessageConverter<?>> converters) {
				converters.addAll(httpMessageConverters());
			}
			
		};
	}
	
	@Bean
	public WebMvcConfigurer springMvcCustomConfig(){
		return new WebMvcConfigurerAdapter() {
			@Override 
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				converters.addAll(httpMessageConverters());
			}
		};
	}
	
	static List<HttpMessageConverter<?>> httpMessageConverters(){
		
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		
		FilteringJackson2HttpMessageConverter jsonConverter
				= new FilteringJackson2HttpMessageConverter();
		jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON_UTF8, RestMediaTypes.ALPS_JSON, RestMediaTypes.HAL_JSON,
				RestMediaTypes.JSON_PATCH_JSON, RestMediaTypes.MERGE_PATCH_JSON,
				RestMediaTypes.SPRING_DATA_COMPACT_JSON, RestMediaTypes.SPRING_DATA_VERBOSE_JSON));
		converters.add(jsonConverter);

		FilteringTextMessageConverter filteringTextMessageConverter =
				new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
		filteringTextMessageConverter.setSupportedMediaTypes(Arrays.asList(
				new MediaType("text", "plain", Charset.forName("utf-8")), MediaType.TEXT_PLAIN));
		filteringTextMessageConverter.setDelimiter("\t");
		converters.add(filteringTextMessageConverter);
		
		return converters;
		
	}
	
}
