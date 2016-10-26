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

package com.blueprint.centromere.core.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows adding or customizing {@link Model} metadata.
 * 
 * @author woemler
 * @since 0.4.3
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModelAttributes {

	/**
	 * Custom URI to be used when mapping web service requests.  If null, defaults to model class name.
	 * 
	 * @return uri value
	 */
	String uri() default "";

	/**
	 * Table or collection to store records in.  If null, defaults to database implementation default.
	 * 
	 * @return table/collection name
	 */
	String table() default "";

	/**
	 * Display name to use for model.  If null, default to class name.
	 * 
	 * @return model display name
	 */
	String name() default "";
	
}
