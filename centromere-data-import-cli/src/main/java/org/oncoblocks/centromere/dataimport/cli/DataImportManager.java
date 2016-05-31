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

package org.oncoblocks.centromere.dataimport.cli;

import org.oncoblocks.centromere.core.dataimport.DataTypes;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.model.support.DataSetMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages mappings of data sets and data types used in the import process.  Links string labels to
 *   registered {@link RecordProcessor} beans, and to new or existing {@link DataSetMetadata}
 *   records.
 * 
 * @author woemler
 */
public class DataImportManager {
	
	private ApplicationContext applicationContext;
	
	private Map<String, RecordProcessor> dataTypeMap = new HashMap<>(); 
	
	private static final Logger logger = LoggerFactory.getLogger(DataImportManager.class);
	
	public DataImportManager(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
		dataTypeMap = initializeDataTypeMap();
	}
	
	/* Data Type Management */

	/**
	 * Builds the {@code dataTypeMap} by inspecting registered {@link RecordProcessor} beans and their
	 *   {@link DataTypes} annotations.
	 */
	private Map<String, RecordProcessor> initializeDataTypeMap(){
		logger.debug("[CENTROMERE] Initializing DataImportManager data type mappings.");
		Map<String, RecordProcessor> map = new HashMap<>();
		for (Map.Entry entry: applicationContext.getBeansWithAnnotation(DataTypes.class).entrySet()){
			Object obj = entry.getValue();
			if (obj instanceof RecordProcessor){
				RecordProcessor p = (RecordProcessor) obj;
				DataTypes dataTypes = p.getClass().getAnnotation(DataTypes.class);
				for (String t: dataTypes.value()){
					map.put(t, p);
				}
			}
		}
		logger.debug(String.format("[CENTROMERE] Data type map initialized: %s", map.toString()));
		return map;
	}

	/**
	 * Adds a data type mapping, using the name of a {@link RecordProcessor} class or instance name to
	 *   locate a usable bean.
	 * 
	 * @param label
	 * @param beanReference
	 */
	public void addDataTypeMapping(String label, String beanReference){
		RecordProcessor processor = null;
		try {
			Class<? extends RecordProcessor> processorClass
					= (Class<? extends RecordProcessor>) Class.forName(beanReference);
			processor = applicationContext.getBean(processorClass);
		} catch (ClassNotFoundException e){
			try {
				processor = (RecordProcessor) applicationContext.getBean(beanReference);
			} catch (NoSuchBeanDefinitionException ex){
				throw new CommandLineRunnerException(String.format("No processor bean found: %s", beanReference));
			}
		}
		addDataTypeMapping(label, processor);
	}

	/**
	 * Adds a data type mapping, using the data type name and {@link RecordProcessor} bean reference.
	 *
	 * @param label
	 */
	public void addDataTypeMapping(String label, RecordProcessor processor){
		dataTypeMap.put(label, processor);
	}

	/**
	 * Returns reference to a {@link RecordProcessor} bean class, if one of that type has been mapped
	 *   to a data set.  Returns null if no mapping exists.
	 *
	 * @param label
	 * @return
	 */
	public RecordProcessor getDataTypeProcessor(String label){
		if (!dataTypeMap.containsKey(label)) return null;
		return dataTypeMap.get(label);
	}

	/**
	 * Tests whether a data type mapping has been registered.
	 *
	 * @param label
	 * @return
	 */
	public boolean isSupportedDataType(String label){
		return dataTypeMap.containsKey(label) && dataTypeMap.get(label) != null;
	}

	public Map<String, RecordProcessor> getDataTypeMap() {
		return dataTypeMap;
	}

	public void setDataTypeMap(
			Map<String, RecordProcessor> dataTypeMap) {
		this.dataTypeMap = dataTypeMap;
	}
	
}
