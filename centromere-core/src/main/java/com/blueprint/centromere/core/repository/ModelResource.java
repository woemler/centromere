/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ModelRepository} instances with this annotation are marked as being eligible for being
 * automatically exposed in REST web services as a {@link com.blueprint.centromere.core.model.Model}
 * resource. The {@link #value()} and {@link #name()} attributes can be used interchangibly, to name
 * the resource in API documentation and generate a URI for accessing the resource. By setting the
 * {@link #ignored()} value to {@code true}, the repository should not be exposed as a public REST
 * resource, but a repository bean should still be created.
 *
 * @author woemler
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelResource {

    /**
     * Resource name and URI to be assigned in web services.  Will be used if {@link #name()} is
     * empty.
     */
    String value() default "";

    /**
     * Resource name and URI to be assigned in web services.
     */
    String name() default "";

    /**
     * When set to {@code true}, the repository should be ignored by web services.
     */
    boolean ignored() default false;

}
