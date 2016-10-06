/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Extends {@link SpringBootServletInitializer} and wraps the constructor for {@link SpringApplication}
 *   to allow for quick Spring Boot application initialization, when combined with the
 *   {@link AutoConfigureCentromere} annotation.
 * 
 * @author woemler
 * @since 0.4.3
 */

public abstract class CentromereInitializer extends SpringBootServletInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(CentromereInitializer.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(this);
	}

	/**
	 * Creates a Spring Boot web application instance and loads configuration classes determined by 
	 *   the profiles defined by {@link AutoConfigureCentromere}.
	 * 
	 * @param applicationClass main application class.
	 * @param args command line arguments.
	 */
	public static void run(Class<?> applicationClass, String[] args) {
		String[] profiles;
		if (applicationClass.isAnnotationPresent(AutoConfigureCentromere.class)){
			AutoConfigureCentromere cfg = applicationClass.getAnnotation(AutoConfigureCentromere.class);
			logger.debug(String.format("Processing AutoConfigureCentromere annotation with params: db=%s  schema=%s",
					cfg.database(), cfg.schema()));
			profiles = Profiles.getApplicationProfiles(cfg.database(), cfg.schema());
		} else {
			profiles = Profiles.getApplicationProfiles(Database.CUSTOM, Schema.CUSTOM);
		}
		logger.info(String.format("[CENTROMERE] Using application profiles: %s", profiles));
		SpringApplication springApplication = new SpringApplication(applicationClass);
		springApplication.setAdditionalProfiles(profiles);
		springApplication.run(args);
	}
	
}
