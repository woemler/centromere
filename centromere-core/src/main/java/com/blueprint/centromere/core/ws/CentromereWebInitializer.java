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

package com.blueprint.centromere.core.ws;

import com.blueprint.centromere.core.config.AutoConfigureCentromere;
import com.blueprint.centromere.core.config.Profiles;
import com.google.common.collect.ObjectArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author woemler
 */
@SpringBootApplication
@Configuration
public class CentromereWebInitializer extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CentromereWebInitializer.class);

    public static void run(Class<?> source, String[] args){
        SpringApplication springApplication = new SpringApplication(source);
        if (source.isAnnotationPresent(AutoConfigureCentromere.class)){
            AutoConfigureCentromere annotation = source.getAnnotation(AutoConfigureCentromere.class);
            String[] profiles = ObjectArrays.concat(Profiles.WEB_PROFILE, 
                Profiles.getApplicationProfiles(annotation.database(), annotation.schema(), annotation.webSecurity()));
            springApplication.setAdditionalProfiles(profiles);
            logger.info(String.format("Running Centromere with profiles: %s", Arrays.asList(profiles)));
        } else {
            logger.info("Running Centromere with default profiles.");
        }

        springApplication.run(args);
    }

}
