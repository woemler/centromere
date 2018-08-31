package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.ws.documentation.ModelApiListingPlugin;
import com.blueprint.centromere.ws.documentation.ModelParameterBuilderPlugin;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
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
 * @author woemler
 */
public class ApiDocumentationConfig {
  
  public static final String SWAGGER_PROFILE = "api_doc_swagger";
  public static final String NO_DOCUMENTATION_PROFILE = "api_doc_none";

  @Configuration
  @Profile({ SWAGGER_PROFILE })
  @EnableSwagger2
  @Import({ BeanValidatorPluginsConfiguration.class })
  public static class SwaggerConfig {

    @Autowired
    private WebProperties webProperties;
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
          //.additionalModels(typeResolver.resolve(User.class), getModelTypes())
          .enableUrlTemplating(true);
    }

    private ApiInfo apiInfo() {
      WebProperties.Api api = webProperties.getApi();
      return new ApiInfo(
          api.getName(),
          api.getDescription(),
          api.getVersion(),
          api.getTermsOfService(),
          new Contact(
              api.getContactName(),
              api.getContactUrl(),
              api.getContactEmail()
          ),
          api.getLicense(),
          api.getLicenseUrl()
      );
    }

    private Predicate<String> apiPaths() {
      return PathSelectors.regex(webProperties.getApi().getRegexUrl());
    }

    private ResolvedType[] getModelTypes() {
      List<Class<?>> models = new ArrayList<>(registry.getRegisteredModels());
      ResolvedType[] types = new ResolvedType[models.size()];
      for (int i = 0; i < models.size(); i++) {
        types[i] = typeResolver.resolve(models.get(i));
      }
      return types;
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    public ModelParameterBuilderPlugin modelParameterBuilderPlugin() {
      return new ModelParameterBuilderPlugin();
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    public ModelApiListingPlugin mappedModelApiListingPlugin() {
      return new ModelApiListingPlugin();
    }

  }

}
