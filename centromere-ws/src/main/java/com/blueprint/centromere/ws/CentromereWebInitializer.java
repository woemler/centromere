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

package com.blueprint.centromere.ws;

import com.blueprint.centromere.core.config.AutoConfigureCentromere;
import com.blueprint.centromere.core.config.Profiles;
import com.blueprint.centromere.ws.config.SwaggerConfig;
import com.blueprint.centromere.ws.config.WebApplicationConfig;
import com.blueprint.centromere.ws.config.WebSecurityConfig;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author woemler
 */
@SpringBootApplication
public class CentromereWebInitializer extends SpringBootServletInitializer {

  private static final Logger logger = LoggerFactory.getLogger(CentromereWebInitializer.class);

  public static void run(Class<?> source, String[] args){
    SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
    builder.child(WebApplicationConfig.class, WebSecurityConfig.class, SwaggerConfig.class);
    builder.profiles(getActiveProfiles(source));
    builder.web(WebApplicationType.SERVLET);
    builder.run(args);
  }
  
  private static String[] getActiveProfiles(Class<?> source){
    String[] profiles;
    if (source.isAnnotationPresent(AutoConfigureCentromere.class)){
      AutoConfigureCentromere annotation = source.getAnnotation(AutoConfigureCentromere.class);
      String securityProfile = annotation.enableWebSecurity()
          ? Profiles.SECURE_READ_WRITE_PROFILE : Profiles.NO_SECURITY;
      String apiDocumentationProfile = annotation.enableApiDocumentation()
          ? Profiles.API_DOCUMENTATION_ENABLED_PROFILE : Profiles.API_DOCUMENTATION_DISABLED_PROFILE;
      profiles = annotation.useCustomSchema() ? 
          new String[] { Profiles.WEB_PROFILE, securityProfile, apiDocumentationProfile, Profiles.SCHEMA_CUSTOM } : 
          new String[] { Profiles.WEB_PROFILE, securityProfile, apiDocumentationProfile, Profiles.SCHEMA_DEFAULT };
      logger.info(String.format("Running Centromere with profiles: %s", Arrays.asList(profiles)));
    } else {
      logger.info("Running Centromere with default profiles.");
      profiles = new String[]{};
    }
    return profiles;
  }

}
