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
	T find(String keyword);
	boolean exists(String keyword);
	boolean exists(T object);
	void add(T object);
	void add(String keyword, T object);
	Map<String, T> getRegistry();
	Iterable<T> getRegisteredComponents();
	void setRegistry(Map<String, T> registry);
}
