package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.config.Profiles;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author woemler
 */
@Configuration
@Profile({ Profiles.API_DOCUMENTATION_ENABLED_PROFILE })
@EnableSwagger2
@Import({
    //SpringDataRestConfiguration.class,
    BeanValidatorPluginsConfiguration.class
})
public class SwaggerConfig {

  @Autowired private Environment env;

  @Bean
  public Docket api(){
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(apiPaths())
        .build()
        .apiInfo(apiInfo())
        .enableUrlTemplating(true);
  }

  private ApiInfo apiInfo(){
    return new ApiInfo(
        env.getRequiredProperty("centromere.api.name"),
        env.getRequiredProperty("centromere.api.description"),
        env.getRequiredProperty("centromere.api.version"),
        env.getRequiredProperty("centromere.api.tos"),
        new Contact(
            env.getRequiredProperty("centromere.api.contact-name"),
            env.getRequiredProperty("centromere.api.contact-url"),
            env.getRequiredProperty("centromere.api.contact-email")
        ),
        env.getRequiredProperty("centromere.api.license"),
        env.getRequiredProperty("centromere.api.license-url")
    );
  }

  private Predicate<String> apiPaths(){
    return PathSelectors.regex(env.getRequiredProperty("centromere.api.regex-url"));
  }

}
