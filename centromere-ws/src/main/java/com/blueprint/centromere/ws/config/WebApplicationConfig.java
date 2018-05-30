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

import com.blueprint.centromere.core.config.DefaultModelRepositoryRegistry;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.core.config.WebProperties;
import com.blueprint.centromere.ws.controller.ModelCrudController;
import com.blueprint.centromere.ws.controller.ModelResourceAssembler;
import com.blueprint.centromere.ws.controller.UserAuthenticationController;
import com.blueprint.centromere.ws.exception.RestExceptionHandler;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author woemler
 */

@Profile({Profiles.WEB_PROFILE})
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableSpringDataWebSupport
//@EnableHypermediaSupport(type = { EnableHypermediaSupport.HypermediaType.HAL }) // Not needed in Spring boot 2
//@EnableEntityLinks
@Import({
		WebSecurityConfig.class,
		SwaggerConfig.class
})
@ComponentScan(basePackageClasses = {
    UserAuthenticationController.class,
    ModelCrudController.class,
    RestExceptionHandler.class
})
public class WebApplicationConfig {

  private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfig.class);

  @Autowired private WebProperties webProperties;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public ModelResourceAssembler mappingModelResourceAssembler(
      DefaultModelRepositoryRegistry modelRepositoryRegistry){
    return new ModelResourceAssembler(modelRepositoryRegistry);
  }
  
  @Bean
  public HttpMessageConverters customConverters(){
    
    // JSON
    FilteringJackson2HttpMessageConverter jsonConverter
        = new FilteringJackson2HttpMessageConverter();
    jsonConverter.setSupportedMediaTypes(ApiMediaTypes.getJsonMediaTypes());

    // XML
    MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
    xmlConverter.setSupportedMediaTypes(ApiMediaTypes.getXmlMediaTypes());
    XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
    xmlConverter.setMarshaller(xStreamMarshaller);
    xmlConverter.setUnmarshaller(xStreamMarshaller);
    
    // Text
    FilteringTextMessageConverter textMessageConverter =
        new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
    textMessageConverter.setDelimiter("\t");
    
    return new HttpMessageConverters(jsonConverter, xmlConverter, textMessageConverter);
    
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer(){
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**");
      }
    };
  }
  
//
//  @Bean
//  public FilteringJackson2HttpMessageConverter filteringJackson2HttpMessageConverter(){
//    FilteringJackson2HttpMessageConverter jsonConverter
//        = new FilteringJackson2HttpMessageConverter();
//    jsonConverter.setSupportedMediaTypes(ApiMediaTypes.getJsonMediaTypes());
//    return jsonConverter;
//  }
//
//  @Bean
//  public MarshallingHttpMessageConverter marshallingHttpMessageConverter(){
//    MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
//    xmlConverter.setSupportedMediaTypes(ApiMediaTypes.getXmlMediaTypes());
//    XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
//    xmlConverter.setMarshaller(xStreamMarshaller);
//    xmlConverter.setUnmarshaller(xStreamMarshaller);
//    return xmlConverter;
//  }
//
//  @Bean
//  public FilteringTextMessageConverter filteringTextMessageConverter(){
//    FilteringTextMessageConverter filteringTextMessageConverter =
//        new FilteringTextMessageConverter(new MediaType("text", "plain", Charset.forName("utf-8")));
//    filteringTextMessageConverter.setDelimiter("\t");
//    return filteringTextMessageConverter;
//  }
//
//  @Override
//  public void configureContentNegotiation(ContentNegotiationConfigurer configurer){
//    configurer
//        //.favorPathExtension(false)
//        .useJaf(false)
//        .ignoreAcceptHeader(false)
//        .favorParameter(false)
//        .defaultContentType(MediaType.APPLICATION_JSON_UTF8);
//  }

//  @Bean
//  public CorsFilter corsFilter(){
//    return new CorsFilter();
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

  // TODO static resource handling
//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//    if (!registry.hasMappingForPattern("/webjars/**")){
//      registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//    } else {
//      logger.info("[CENTROMERE] WebJar location already configured.");
//    }
//    if (webProperties.isEnableStaticContent()){
//      if (!registry.hasMappingForPattern("/static/**")){
//        registry.addResourceHandler("/static/**").addResourceLocations(
//            webProperties.getStaticContentLocation());
//        if (webProperties.getHomePage() != null && !"".equals(webProperties.getHomePage())
//            && webProperties.getHomePageLocation() != null && !"".equals(webProperties.getHomePageLocation())){
//          registry.addResourceHandler(webProperties.getHomePage())
//              .addResourceLocations(webProperties.getHomePageLocation());
//          logger.info(String.format("[CENTROMERE] Static home page configured at URL: /%s",
//              webProperties.getHomePage()));
//        } else {
//          logger.warn("[CENTROMERE] Static home page location not properly configured.");
//        }
//      } else {
//        logger.info("[CENTROMERE] Static content already configured.");
//      }
//    }
//  }
	
}
