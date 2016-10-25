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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.model.Model;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Performs full, automatic configuration for model schema selection, database integration, and 
 *   web services creation.  
 * 
 * @author woemler
 * @since 0.4.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Configuration
@ComponentScan(basePackages = { "org.oncoblocks.centromere.web.config" })
@Import({ 
		ProfileConfiguration.class
})
@ModelScan
public @interface AutoConfigureCentromere {

	/**
	 * Allows selection of a default database configuration.  Defaults to {@code CUSTOM}, which assumes
	 *   a user-supplied configuration class is present.
	 * 
	 * @return database profile
	 */
	Database database() default Database.CUSTOM;

	/**
	 * Allows selection fo a default set of {@link Model} classes,
	 *   which will be registered in the application.  Defaults to {@code CUSTOM}, which assumes that
	 *   model registration will be handled by the user, or with the {@link #basePackages()} or 
	 *   {@link #modelClasses()} methods.
	 * 
	 * @return
	 */
	Schema schema() default Schema.CUSTOM;
	
	@AliasFor(annotation = ModelScan.class, attribute = "basePackages")
	String[] basePackages() default {};
	
	@AliasFor(annotation = ModelScan.class, attribute = "modelClasses")
	Class<? extends Model<?>>[] modelClasses() default {};
	
}
