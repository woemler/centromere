package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.ws.documentation.ModelApiListingPlugin;
import com.blueprint.centromere.ws.documentation.ModelParameterBuilderPlugin;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Standardized configurations for REST API documentation tools.
 * 
 * @author woemler
 */
public class ApiDocumentationConfig {
  
  public static final String SWAGGER_PROFILE = "api_doc_swagger";
  public static final String NO_DOCUMENTATION_PROFILE = "api_doc_none";

  /**
   * Configuration for Swagger v2 API documentation, using SpringFox. 
   */
  @Configuration
  @Profile({ SWAGGER_PROFILE })
  @EnableSwagger2
  @Import({ BeanValidatorPluginsConfiguration.class })
  public static class SwaggerConfig {

    @Autowired 
    private Environment env;
    
    @Autowired
    private TypeResolver typeResolver;
    
    @Autowired
    private ModelResourceRegistry registry;

    @Bean
    public Docket api() {
      return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.any())
          .paths(apiPaths())
          .build()
          .apiInfo(apiInfo())
          .enableUrlTemplating(true);
    }

    private ApiInfo apiInfo() {
      //WebProperties.Api api = webProperties.getApi();
      return new ApiInfo(
          env.getRequiredProperty("centromere.web.api.name"),
          env.getRequiredProperty("centromere.web.api.description"),
          env.getRequiredProperty("centromere.web.api.version"),
          env.getRequiredProperty("centromere.web.api.tos"),
          new Contact(
              env.getRequiredProperty("centromere.web.api.contact-name"),
              env.getRequiredProperty("centromere.web.api.contact-url"),
              env.getRequiredProperty("centromere.web.api.contact-email")
          ),
          env.getRequiredProperty("centromere.web.api.license"),
          env.getRequiredProperty("centromere.web.api.license-url")
      );
    }

    private Predicate<String> apiPaths() {
      return PathSelectors.regex(env.getRequiredProperty("centromere.web.api.regex-url"));
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    public ModelParameterBuilderPlugin modelParameterBuilderPlugin() {
      return new ModelParameterBuilderPlugin();
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    public ModelApiListingPlugin mappedModelApiListingPlugin() {
      return new ModelApiListingPlugin(registry, typeResolver);
    }

  }

}
