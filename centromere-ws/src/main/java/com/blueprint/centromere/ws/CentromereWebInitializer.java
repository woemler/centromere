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

import com.blueprint.centromere.ws.config.ApiDocumentationConfig;
import com.blueprint.centromere.ws.config.AutoConfigureCentromere;
import com.blueprint.centromere.ws.config.WebApplicationConfig;
import com.blueprint.centromere.ws.config.WebSecurityConfig;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Convenience class for initializing a Spring Boot-based Centromere instance.  This class should be
 * subclassed and annotated with {@link AutoConfigureCentromere} for the simplest configuration.
 *
 * @author woemler
 */
@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class,
    DataSourceAutoConfiguration.class
})
public class CentromereWebInitializer extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CentromereWebInitializer.class);

    public static void run(Class<?> source, String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
        builder.child(WebApplicationConfig.class, WebSecurityConfig.class,
            ApiDocumentationConfig.class);
        builder.profiles(getActiveProfiles(source));
        builder.web(WebApplicationType.SERVLET);
        builder.run(args);
    }

    /**
     * Checks the {@link AutoConfigureCentromere} annotation, if present, and determines what
     * profiles the application should run with, and therefore what configuration classes to
     * initialize.
     */
    private static String[] getActiveProfiles(Class<?> source) {
        String[] profiles;
        if (source.isAnnotationPresent(AutoConfigureCentromere.class)) {
            AutoConfigureCentromere annotation = source
                .getAnnotation(AutoConfigureCentromere.class);
            String securityProfile = annotation.webSecurity();
            String apiDocumentationProfile = annotation.apiDocumentation();
            profiles = new String[]{securityProfile, apiDocumentationProfile};
            LOGGER.info(
                String.format("Running Centromere with profiles: %s", Arrays.asList(profiles)));
        } else {
            LOGGER.info("Running Centromere with default profiles.");
            profiles = new String[]{};
        }
        return profiles;
    }

}
