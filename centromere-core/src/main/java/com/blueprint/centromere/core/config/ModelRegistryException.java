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

/** 
 * Generic exception to be thrown when problems arise in {@link ModelRegistry} or {@link ModelBeanRegistry}
 *   initialization or processing.
 * 
 * @author woemler
 * @since 0.4.3
 */
@Deprecated
public class ModelRegistryException extends RuntimeException {

	public ModelRegistryException() {
	}

	public ModelRegistryException(String message) {
		super(message);
	}

	public ModelRegistryException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModelRegistryException(Throwable cause) {
		super(cause);
	}

	public ModelRegistryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
