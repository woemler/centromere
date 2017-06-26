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

package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.ws.controller.ModelCrudController;
import com.blueprint.centromere.ws.controller.ModelResourceAssembler;
import com.blueprint.centromere.ws.controller.UserAuthenticationController;
import com.blueprint.centromere.ws.exception.RestExceptionHandler;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author woemler
 */

@Profile({Profiles.WEB_PROFILE})
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableSpringDataWebSupport
@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL })
@EnableEntityLinks
@PropertySources({
		@PropertySource({"classpath:centromere-defaults.properties"}),
		@PropertySource(value = {"classpath:centromere.properties"},ignoreResourceNotFound = true)
})
@Import({
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

  @Bean
  public FilteringJackson2HttpMessageConverter filteringJackson2HttpMessageConverter(){
    FilteringJackson2HttpMessageConverter jsonConverter
        = new FilteringJackson2HttpMessageConverter();
    jsonConverter.setSupportedMediaTypes(ApiMediaTypes.getJsonMediaTypes());
    return jsonConverter;
  }

//  @Bean
//  public MarshallingHttpMessageConverter marshallingHttpMessageConverter(){
//    MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
//    xmlConverter.setSupportedMediaTypes(ApiMediaTypes.getXmlMediaTypes());
//    XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
//    xmlConverter.setMarshaller(xStreamMarshaller);
//    xmlConverter.setUnmarshaller(xStreamMarshaller);
//    return xmlConverter;
//  }

  @Bean
  public FilteringTextMessageConverter filteringTextMessageConverter(){
    FilteringTextMessageConverter filteringTextMessageConverter =
        new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
    filteringTextMessageConverter.setDelimiter("\t");
    return filteringTextMessageConverter;
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer){
    configurer
        //.favorPathExtension(false)
        //.favorParameter(true)
        .defaultContentType(MediaType.APPLICATION_JSON);
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

  //TODO resolve this dependency
//  @Bean
//  public EmbeddedServletContainerCustomizer servletContainerCustomizer(){
//    return configurableEmbeddedServletContainer ->
//        ((TomcatEmbeddedServletContainerFactory) configurableEmbeddedServletContainer).addConnectorCustomizers(
//            new TomcatConnectorCustomizer() {
//              @Override
//              public void customize(Connector connector) {
//                AbstractHttp11Protocol httpProtocol = (AbstractHttp11Protocol) connector.getProtocolHandler();
//                httpProtocol.setCompression("on");
//                httpProtocol.setCompressionMinSize(256);
//                String mimeTypes = httpProtocol.getCompressableMimeType();
//                String mimeTypesWithJson = mimeTypes + "," + MediaType.APPLICATION_JSON_VALUE;
//                httpProtocol.setCompressableMimeType(mimeTypesWithJson);
//              }
//            }
//        );
//  }

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
	
}
