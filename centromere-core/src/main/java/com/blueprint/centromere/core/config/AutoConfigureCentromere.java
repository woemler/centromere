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
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@Import({ ProfileConfiguration.class })
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
	 *   model creation and repository instantiation will be handled by the user.
	 * 
	 * @return
	 */
	Schema schema() default Schema.CUSTOM;

	/**
	 * Sets the level of security to be automaticaly configured within the web services context.
	 *   Defaults to NONE, which is no security.
	 *
	 * @return
	 */
	Security webSecurity() default Security.NONE;
	
}
