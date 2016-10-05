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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * @author woemler
 * @since 0.4.3
 */

public abstract class CentromereInitializer extends SpringBootServletInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(CentromereInitializer.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		String[] profiles;
		if (this.getClass().isAnnotationPresent(AutoConfigureCentromere.class)){
			AutoConfigureCentromere cfg = this.getClass().getAnnotation(AutoConfigureCentromere.class);
			profiles = Profiles.getApplicationProfiles(cfg.database(), cfg.schema());
		} else {
			profiles = Profiles.getApplicationProfiles(Database.CUSTOM, Schema.CUSTOM);
		}
		logger.info(String.format("[CENTROMERE] Using application profiles: %s", profiles));
		return application.sources(this).profiles(profiles);
	}
	
}
