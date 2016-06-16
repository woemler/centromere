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

import org.oncoblocks.centromere.core.dataimport.DataTypeSupport;
import org.oncoblocks.centromere.core.dataimport.DataTypes;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Implementation of {@link ComponentRegistry} for mapping input file data types to their appropriate
 *   {@link RecordProcessor} beans.  Uses the {@link DataTypes} annotation of existing processor
 *   classes to identify valid beans.
 * 
 * @author woemler
 * @since 0.4.1
 */
public class DataTypeProcessorRegistry extends AbstractComponentRegistry<RecordProcessor> 
		implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	private static final Logger logger = LoggerFactory.getLogger(DataTypeProcessorRegistry.class);

	public DataTypeProcessorRegistry() {
	}

	public DataTypeProcessorRegistry(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Configures the registry object and builds the object mappings.
	 */
	@PostConstruct
	public void configure(){
		Assert.notNull(applicationContext, "ApplicationContext must not be null.");
		for (Map.Entry entry: applicationContext.getBeansOfType(RecordProcessor.class).entrySet()){
			this.add((RecordProcessor) entry.getValue());
		}
	}

	/**
	 * Adds a new data type processor mapping, if the submitted processor has a {@link DataTypes} 
	 *   annotation or implements the {@link DataTypeSupport} interface.
	 * 
	 * @param processor
	 */
	@Override 
	public void add(RecordProcessor processor) {
		if (processor instanceof DataTypeSupport){
			for (String dataType: ((DataTypeSupport) processor).getSupportedDataTypes()){
				this.add(dataType, processor);
			}
		}
		else if (processor.getClass().isAnnotationPresent(DataTypes.class)){
			DataTypes dataTypes = processor.getClass().getAnnotation(DataTypes.class);
			for (String dataType: dataTypes.value()){
				this.add(dataType, processor);
			}
		} else {
			logger.warn(String.format("RecordProcessor does not have DataTypes annotation.  No data type " 
					+ "mapping will be registered: %s", processor.getClass().getName()));
		}
	}

	/**
	 * Searches the {@link ApplicationContext} for {@link RecordProcessor} beans and attempts to 
	 *   register them.
	 * 
	 * @param type
	 */
	public void add(Class<? extends RecordProcessor> type){
		Map<String, ? extends RecordProcessor> map = applicationContext.getBeansOfType(type);
		for (Map.Entry entry: map.entrySet()){
			this.add((RecordProcessor) entry.getValue());
		}
	}

	@Autowired 
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
