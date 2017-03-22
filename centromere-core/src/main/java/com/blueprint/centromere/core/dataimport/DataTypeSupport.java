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

package com.blueprint.centromere.core.dataimport;

/**
 * Implementing classes are expected to support specific data types, which can be defined in the
 *   class declaration with a {@link DataTypes} annotation, or at runtime with the defined methods.
 * 
 * @author woemler
 * @since 0.4.1
 * TODO: Do we still need this???
 */
public interface DataTypeSupport {

	/**
	 * Returns true if the implementing class supports the input data type identifier.
	 * 
	 * @param dataType
	 * @return
	 */
	boolean isSupportedDataType(String dataType);

	/**
	 * Sets all of the supported data type identifiers.
	 * 
	 * @param dataTypes
	 */
	void setSupportedDataTypes(Iterable<String> dataTypes);

	/**
	 * Returns all of the supported data type identifiers.
	 * 
	 * @return
	 */
	Iterable<String> getSupportedDataTypes();
}
