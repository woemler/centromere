/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.ws.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Performs automatic configuration of core components of Centromere. This annotation is not 
 *   required for running Centromere web service applications, but it does make configuration much
 *   easier. This is intended to be used in conjunction with the {@link com.blueprint.centromere.ws.CentromereWebInitializer}
 *   Spring Boot initializer implementation.
 *
 * @author woemler
 * @since 0.4.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Configuration
@Import({
    WebApplicationConfig.DefaultWebApplicationConfig.class,
    WebSecurityConfig.class,
    ApiDocumentationConfig.class,
    ActuatorConfig.class
})
public @interface AutoConfigureCentromere {

    /**
     * If true, web API access will require authentication.
     *
     * @return
     */
    String webSecurity() default WebSecurityConfig.NO_SECURITY_PROFILE;

    /**
     * If true, web services will include automatic API documentation.
     *
     * @return
     */
    String apiDocumentation() default ApiDocumentationConfig.NO_DOCUMENTATION_PROFILE;

}
