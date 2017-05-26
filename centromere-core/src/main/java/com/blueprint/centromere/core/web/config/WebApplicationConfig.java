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

package com.blueprint.centromere.core.web.config;

import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.web.controller.ModelResourceAssembler;
import com.blueprint.centromere.core.web.controller.ModelCrudController;
import com.blueprint.centromere.core.web.controller.UserAuthenticationController;
import com.blueprint.centromere.core.web.exception.RestExceptionHandler;
import com.sun.jdi.connect.Connector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author woemler
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
@EnableEntityLinks
@PropertySources({
		@PropertySource({"classpath:centromere-defaults.properties"}),
		@PropertySource(value = {"classpath:centromere.properties"},ignoreResourceNotFound = true)
})
@Import({
		RepositoryRestMvcConfiguration.class,
		WebSecurityConfig.class,
		SwaggerConfig.class
})
@ComponentScan(basePackageClasses = {
    UserAuthenticationController.class,
    ModelCrudController.class,
    RestExceptionHandler.class
})
public class WebApplicationConfig extends WebMvcConfigurerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfig.class);

  @Autowired private Environment env;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public ModelResourceAssembler mappingModelResourceAssembler(
      ModelRepositoryRegistry modelRepositoryRegistry){
    return new ModelResourceAssembler(modelRepositoryRegistry);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    FilteringJackson2HttpMessageConverter jsonConverter
        = new FilteringJackson2HttpMessageConverter();
    jsonConverter.setSupportedMediaTypes(ApiMediaTypes.getJsonMediaTypes());
    converters.add(jsonConverter);

    MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
    xmlConverter.setSupportedMediaTypes(ApiMediaTypes.getXmlMediaTypes());
    XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
    xmlConverter.setMarshaller(xStreamMarshaller);
    xmlConverter.setUnmarshaller(xStreamMarshaller);
    converters.add(xmlConverter);

    FilteringTextMessageConverter filteringTextMessageConverter =
        new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
    filteringTextMessageConverter.setDelimiter("\t");
    converters.add(filteringTextMessageConverter);

  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer){
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Bean
  public CorsFilter corsFilter(){
    return new CorsFilter();
  }

//  @Override
//  public void addFormatters(FormatterRegistry registry) {
//    registry.addConverter(new StringToAttributeConverter());
//    registry.addConverter(new StringToSourcedAliasConverter());
//    super.addFormatters(registry);
//  }

  @Bean
  public EmbeddedServletContainerCustomizer servletContainerCustomizer(){
    return configurableEmbeddedServletContainer ->
        ((TomcatEmbeddedServletContainerFactory) configurableEmbeddedServletContainer).addConnectorCustomizers(
            new TomcatConnectorCustomizer() {
              @Override
              public void customize(Connector connector) {
                AbstractHttp11Protocol httpProtocol = (AbstractHttp11Protocol) connector.getProtocolHandler();
                httpProtocol.setCompression("on");
                httpProtocol.setCompressionMinSize(256);
                String mimeTypes = httpProtocol.getCompressableMimeType();
                String mimeTypesWithJson = mimeTypes + "," + MediaType.APPLICATION_JSON_VALUE;
                httpProtocol.setCompressableMimeType(mimeTypesWithJson);
              }
            }
        );
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    if (!registry.hasMappingForPattern("/webjars/**")){
      registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    } else {
      logger.info("[CENTROMERE] WebJar location already configured.");
    }
    if ("true".equals(env.getRequiredProperty("centromere.web.enable-static-content").toLowerCase())){
      if (!registry.hasMappingForPattern("/static/**")){
        registry.addResourceHandler("/static/**").addResourceLocations(
            env.getRequiredProperty("centromere.web.static-content-location"));
        if (env.getRequiredProperty("centromere.web.home-page") != null
            && !"".equals(env.getRequiredProperty("centromere.web.home-page"))
            && env.getRequiredProperty("centromere.web.home-page-location") != null
            && !"".equals(env.getRequiredProperty("centromere.web.home-page"))){
          registry.addResourceHandler(env.getRequiredProperty("centromere.web.home-page"))
              .addResourceLocations(env.getRequiredProperty("centromere.web.home-page-location"));
          logger.info(String.format("[CENTROMERE] Static home page configured at URL: /%s",
              env.getRequiredProperty("centromere.web.home-page")));
        } else {
          logger.warn("[CENTROMERE] Static home page location not properly configured.");
        }
      } else {
        logger.info("[CENTROMERE] Static content already configured.");
      }
    }
  }
  
//  @Configuration
//	public static class SpringDataRestCustomConfig extends RepositoryRestConfigurerAdapter{
//
//    @Autowired private Environment env;
//
//    @Override
//    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//      config.setBasePath(env.getRequiredProperty("centromere.api.root-url"));
//      config.setReturnBodyForPutAndPost(true);
//      config.exposeIdsFor(AbstractModel.class);
//    }
//
//    @Override
//    public void configureHttpMessageConverters(List<HttpMessageConverter<?>> converters) {
//      converters.addAll(0, httpMessageConverters());
//    }
//
//    @Override
//    public void configureConversionService(ConfigurableConversionService conversionService) {
//      super.configureConversionService(conversionService);
//      conversionService.addConverter(new StringToMapParameterConverter());
//    }
//
//    @Bean
//		public LinkedResourceProcessor linkedResourceProcessor(){
//    	return new LinkedResourceProcessor();
//		}
//
//		@Bean
//    public SearchResourceProcessor searchResourceProcessor(){
//		  return new SearchResourceProcessor();
//    }
//
//	}
//
//	@Configuration
//	public static class SpringMvcCustomConfig extends WebMvcConfigurerAdapter {
//
//	  private static final Logger logger = LoggerFactory.getLogger(SpringMvcCustomConfig.class);
//
//    @Autowired private Environment env;
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//      converters.addAll(0, httpMessageConverters());
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//      registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//      registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//      if (env.getRequiredProperty("centromere.web.enable-static-content", Boolean.class)){
//        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
//        if (env.getRequiredProperty("centromere.web.home-page") != null
//            && !"".equals(env.getRequiredProperty("centromere.web.home-page"))
//            && env.getRequiredProperty("centromere.web.home-page-location") != null
//            && !"".equals(env.getRequiredProperty("centromere.web.home-page"))){
//          registry.addResourceHandler(env.getRequiredProperty("centromere.web.home-page"))
//              .addResourceLocations(env.getRequiredProperty("centromere.web.home-page-location"));
//          logger.info(String.format("Static home page configured at URL: /%s",
//              env.getRequiredProperty("centromere.web.home-page")));
//        } else {
//          logger.warn("Static home page location not properly configured.");
//        }
//      }
//    }
//
//	}
	
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
