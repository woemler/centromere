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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.mongodb.MongoConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Performs configuration of core components of Centromere.
 * 
 * @author woemler
 * @since 0.4.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Configuration
@Import({ CoreConfiguration.class, MongoConfiguration.class})
public @interface AutoConfigureCentromere {

  /**
   * If true, the default repository classes will not be instantiated, with the assumption that a
   *   custom data model and repository classes will be defined.
   * 
   * @return
   */
  boolean useCustomSchema() default false;

  /**
   * If true, web API access will require authentication.
   *
   * @return
   */
  boolean enableWebSecurity() default false;

  /**
   * If true, web services will include automatic API documentation.
   *
   * @return
   */
  boolean enableApiDocumentation() default false;
	
}
