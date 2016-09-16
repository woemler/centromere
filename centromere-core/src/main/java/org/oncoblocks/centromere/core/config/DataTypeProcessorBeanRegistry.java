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

package org.oncoblocks.centromere.core.config;

import org.oncoblocks.centromere.core.dataimport.DataTypes;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * Implementation of {@link ModelBeanRegistry} for mapping input file data types to their appropriate
 *   {@link RecordProcessor} beans.  Uses the {@link DataTypes} annotation of existing processor
 *   classes to identify valid beans.
 * 
 * @author woemler
 * @since 0.4.1
 */
public class DataTypeProcessorBeanRegistry extends AbstractModelBeanRegistry<RecordProcessor> {
	
	private Map<String, RecordProcessor> dataTypeMap = new HashMap<>();
	private Map<Class<? extends Model>, List<RecordProcessor>> modelProcessorMap = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(DataTypeProcessorBeanRegistry.class);

	public DataTypeProcessorBeanRegistry(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	/**
	 * Checks to see which of the registered {@link RecordProcessor} beans can be considered the 
	 *   default for the submitted {@link Model} class.  Throws an exception if the default cannot be 
	 *   determined, or null if no processors are registered.
	 * 
	 * @param model model class to search
	 * @return default processor for supplied model
	 */
	@Override 
	public RecordProcessor get(Class<? extends Model> model) {
		if (modelProcessorMap.containsKey(model)){
			List<RecordProcessor> foundProcessors = new ArrayList<>();
			for (RecordProcessor processor: modelProcessorMap.get(model)){
				if (processor.getClass().isAnnotationPresent(DataTypes.class)){
					DataTypes dataTypes = processor.getClass().getAnnotation(DataTypes.class);
					if (dataTypes.defaultForModel()){
						foundProcessors.add(processor);
					}
				}
			}
			if (foundProcessors.size() == 1){
				return foundProcessors.get(0);
			} else if (foundProcessors.size() > 1){
				throw new ModelRegistryException(String.format("More than one default processor registered " 
						+ "for model %s", model.getName()));
			}
			foundProcessors = new ArrayList<>();
			for (RecordProcessor processor: modelProcessorMap.get(model)){
				foundProcessors.add(processor);
			}
			if (foundProcessors.size() == 1){
				return foundProcessors.get(0);
			} else if (foundProcessors.size() > 1){
				throw new ModelRegistryException(String.format("More than one non-default processor registered "
						+ "for model %s, cannot determine preferred bean.", model.getName()));
			}
		}
		return null;
	}

	/**
	 * Fetches {@link RecordProcessor} bean instance that is mapped to the supplied data type.
	 * 
	 * @param dataType data type identifier.
	 * @return processor bean associated with data type.
	 */
	public RecordProcessor getByDataType(String dataType){
		return dataTypeMap.containsKey(dataType) ? dataTypeMap.get(dataType) : null;
	}

	@Override 
	public void registerBean(Class<? extends Model> model, RecordProcessor component) {
		List<RecordProcessor> processors = new ArrayList<>();
		if (modelProcessorMap.containsKey(model)){
			processors = modelProcessorMap.get(model);
		}
		if (!processors.contains(component)) {
			processors.add(component);
			modelProcessorMap.put(model, processors);
			if (component.getClass().isAnnotationPresent(DataTypes.class)) {
				DataTypes dataTypes = component.getClass().getAnnotation(DataTypes.class);
				if (dataTypes.value().length > 0) {
					for (String dataType : dataTypes.value()) {
						dataTypeMap.put(dataType, component);
						logger.info(String.format("Registering %s bean %s for data type %s for model %s",
								this.getBeanClass().getName(), component.getClass().getName(), dataType,
								model.getName()));
					}
				} else {
					logger.warn(String.format("RecordProcessor %s DataTypes annotation is empty.",
							component.getClass().getName()));
				}
			} else {
				logger.warn(String.format("RecordProcessor %s does not have DataTypes annotation.",
						component.getClass().getName()));
			}
		} else {
			logger.warn(String.format("RecordProcessor already registered: %s", component.getClass().getName()));
		}
	}

	public boolean isSupportedDataType(String dataType){
		return dataTypeMap.containsKey(dataType);
	}

	@Override 
	public boolean isRegistered(Class<? extends Model> model) {
		return modelProcessorMap.containsKey(model);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isRegistered(RecordProcessor bean) {
		for (Map.Entry entry: modelProcessorMap.entrySet()){
			if (((List<RecordProcessor>) entry.getValue()).contains(bean)){
				return true;
			}
		}
		return false;
	}

	@Override 
	protected Class<RecordProcessor> getBeanClass() {
		return RecordProcessor.class;
	}

	@Override 
	protected String getCreatedBeanNameSuffix() {
		return "Processor";
	}

	@Override 
	public List<RecordProcessor> getRegisteredBeans() {
		Set<RecordProcessor> processors = new HashSet<>();
		for (List<RecordProcessor> processor: modelProcessorMap.values()){
			processors.addAll(processor);
		}
		return new ArrayList<>(processors);
	}

	@Override 
	public List<Class<? extends Model>> getRegisteredBeanModels() {
		return new ArrayList<>(modelProcessorMap.keySet());
	}

}
