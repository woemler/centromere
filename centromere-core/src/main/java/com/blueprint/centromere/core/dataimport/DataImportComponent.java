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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;

import javax.annotation.PostConstruct;

/**
 * Ensures that data import component classes have flexible setup and teardown methods that run
 *   before and after the main import methods.
 * 
 * @author woemler
 * @since 0.4.1
 */
public interface DataImportComponent extends ImportOptionsAware, InitializingBean {

	/**
	 * To be executed before the main component method is first called.  Can be configured to handle 
	 *   a variety of tasks using flexible input parameters.
	 *
	 * @param args an array of objects of any type.
	 * @throws DataImportException
	 */
	default void doBefore(Object... args) throws DataImportException { }

	/**
	 * To be executed after the main component method is called for the last time.  Can be configured 
	 *   to handle a variety of tasks using flexible input parameters.
	 *
	 * @param args an array of objects of any type.
	 * @throws DataImportException
	 */
	default void doAfter(Object... args) throws DataImportException { }

	/**
	 * Empty default implementation.  The purpose of extending {@link InitializingBean} is to trigger
	 *   bean post-processing by a {@link org.springframework.beans.factory.config.BeanPostProcessor}.
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	default void afterPropertiesSet() throws Exception { }
}
