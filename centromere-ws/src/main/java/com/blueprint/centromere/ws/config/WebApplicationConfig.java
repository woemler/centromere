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

package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.model.AbstractModel;
import com.blueprint.centromere.core.util.StringToMapParameterConverter;
import com.blueprint.centromere.ws.controller.ModelController;
import com.blueprint.centromere.ws.controller.UserAuthenticationController;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author woemler
 */

@Configuration
@PropertySources({
		@PropertySource({"classpath:centromere-defaults.properties"}),
		@PropertySource(value = {"classpath:centromere.properties"},ignoreResourceNotFound = true)
})
@Import({
		RepositoryRestMvcConfiguration.class,
		WebSecurityConfig.class,
		SwaggerConfig.class
})
@ComponentScan(basePackageClasses = { UserAuthenticationController.class, ModelController.class })
public class WebApplicationConfig {
  
  @Configuration
	public static class SpringDataRestCustomConfig extends RepositoryRestConfigurerAdapter{

    @Autowired private Environment env;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
      config.setBasePath(env.getRequiredProperty("centromere.api.root-url"));
      config.setReturnBodyForPutAndPost(true);
      config.exposeIdsFor(AbstractModel.class);
    }

    @Override
    public void configureHttpMessageConverters(List<HttpMessageConverter<?>> converters) {
      converters.addAll(0, httpMessageConverters());
    }

    @Override
    public void configureConversionService(ConfigurableConversionService conversionService) {
      super.configureConversionService(conversionService);
      conversionService.addConverter(new StringToMapParameterConverter());
    }
    
    @Bean
		public LinkedResourceProcessor linkedResourceProcessor(){
    	return new LinkedResourceProcessor();
		}

		@Bean
    public SearchResourceProcessor searchResourceProcessor(){
		  return new SearchResourceProcessor();
    }
		
	}
	
	@Configuration
	public static class SpringMvcCustomConfig extends WebMvcConfigurerAdapter {

	  private static final Logger logger = LoggerFactory.getLogger(SpringMvcCustomConfig.class);

    @Autowired private Environment env;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
      converters.addAll(0, httpMessageConverters());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
      registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
      if (env.getRequiredProperty("centromere.web.enable-static-content", Boolean.class)){
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
        if (env.getRequiredProperty("centromere.web.home-page") != null
            && !"".equals(env.getRequiredProperty("centromere.web.home-page"))
            && env.getRequiredProperty("centromere.web.home-page-location") != null
            && !"".equals(env.getRequiredProperty("centromere.web.home-page"))){
          registry.addResourceHandler(env.getRequiredProperty("centromere.web.home-page"))
              .addResourceLocations(env.getRequiredProperty("centromere.web.home-page-location"));
          logger.info(String.format("Static home page configured at URL: /%s",
              env.getRequiredProperty("centromere.web.home-page")));
        } else {
          logger.warn("Static home page location not properly configured.");
        }
      }
    }
		
	}
	
	static List<HttpMessageConverter<?>> httpMessageConverters(){
		
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		
		FilteringJackson2HttpMessageConverter jsonConverter
				= new FilteringJackson2HttpMessageConverter();
		jsonConverter.setSupportedMediaTypes(Arrays.asList(
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON_UTF8, 
				RestMediaTypes.ALPS_JSON, 
				RestMediaTypes.HAL_JSON,
				RestMediaTypes.JSON_PATCH_JSON, 
				RestMediaTypes.MERGE_PATCH_JSON,
				RestMediaTypes.SPRING_DATA_COMPACT_JSON, 
				RestMediaTypes.SPRING_DATA_VERBOSE_JSON
		));
		converters.add(jsonConverter);

		FilteringTextMessageConverter filteringTextMessageConverter =
				new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
		filteringTextMessageConverter.setSupportedMediaTypes(Arrays.asList(
				new MediaType("text", "plain", Charset.forName("utf-8")), 
				MediaType.TEXT_PLAIN
		));
		filteringTextMessageConverter.setDelimiter("\t");
		converters.add(filteringTextMessageConverter);
		
		return converters;
		
	}
	
}
