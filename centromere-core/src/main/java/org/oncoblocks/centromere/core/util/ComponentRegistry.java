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

package org.oncoblocks.centromere.core.util;

import java.util.Map;

/**
 * API for a bean object that allows registering and looking-up beans with String aliases.  
 *   Registered objects are stored in a {@link Map}.
 * 
 * @author woemler
 * @since 0.4.1
 */
public interface ComponentRegistry<T> {

	/**
	 * Returns the mapped object, given a keyword alias.
	 * 
	 * @param keyword {@link String} value of the alias.
	 * @return mapped object.
	 */
	T find(String keyword);

	/**
	 * Tests to see if a mapping exists for the given keyword.
	 * 
	 * @param keyword {@link String} keyword.
	 * @return boolean result of test.
	 */
	boolean exists(String keyword);

	/**
	 * Tests to see if a given object is mapped in the registry.
	 *
	 * @param object mappable object.
	 * @return boolean result of test.
	 */
	boolean exists(T object);

	/**
	 * Adds a new mapping in the registry for the given object.  Presumes some logive will be supplied 
	 *   for a default keyword mapping scheme. 
	 * 
	 * @param object The object to be registered.
	 */
	void add(T object);

	/**
	 * Adds a new mapping in theregistry, with the given keyword and object value.
	 * 
	 * @param keyword String alias for the object.
	 * @param object The object to be registered.
	 */
	void add(String keyword, T object);

	/**
	 * Returns the entire {@link Map} of registered items.
	 * 
	 * @return map of registry.
	 */
	Map<String, T> getRegistry();

	/**
	 * Returns only the registered objects, without their keyword aliases.
	 * 
	 * @return A collection of registered objects.
	 */
	Iterable<T> getRegisteredComponents();

	/**
	 * Sets the registry mappings, given a supplied {@link Map} of keyword-object mappings.
	 * 
	 * @param registry map of aliases and objects.
	 */
	void setRegistry(Map<String, T> registry);
}
