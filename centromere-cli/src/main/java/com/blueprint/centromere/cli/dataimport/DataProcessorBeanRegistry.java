/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.cli.dataimport;

import com.blueprint.centromere.core.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Registry bean for mapping input file data types to their appropriate
 *   {@link DataProcessor} beans.  Uses the {@link DataTypes} annotation of existing processor
 *   classes to identify valid beans.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class DataProcessorBeanRegistry implements BeanPostProcessor, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	private Map<String, DataProcessor> dataTypeMap = new HashMap<>();
	private Map<String, String> dataTypeDescriptionMap = new HashMap<>();
	private Map<Class<? extends Model>, List<DataProcessor>> modelProcessorMap = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(DataProcessorBeanRegistry.class);

	/**
	 * Fetches {@link DataProcessor} bean instance that is mapped to the supplied data type.
	 * 
	 * @param dataType data type identifier.
	 * @return processor bean associated with data type.
	 */
	public DataProcessor getByDataType(String dataType){
		return dataTypeMap.getOrDefault(dataType, null);
	}

	/**
	 * Adds a mapping for the submitted {@link DataProcessor} bean, associating it with any annotated
	 *   data types (found in {@link DataTypes} annotations), and it's target {@link Model} type.
	 * 
	 * @param component
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(DataProcessor component) {
		List<DataProcessor> processors = new ArrayList<>();
		Class<? extends Model> model = component.getModel();
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
						dataTypeDescriptionMap.put(dataType, dataTypes.description().equals("") 
                ? "No description given." : dataTypes.description());
						logger.info(String.format("Registering DataProcessor bean %s for data type %s for model %s",
								component.getClass().getName(), dataType, model.getName()));
					}
				} else {
					logger.warn(String.format("DataProcessor %s DataTypes annotation is empty.",
							component.getClass().getName()));
				}
			} else {
				logger.warn(String.format("DataProcessor %s does not have DataTypes annotation.",
						component.getClass().getName()));
			}
		} else {
			logger.warn(String.format("DataProcessor already registered: %s", component.getClass().getName()));
		}
	}

	/**
	 * Returns the submitted bean with no other operation performed.
	 * 
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	/**
	 * For each bean in the context, adds it to the registry if it is a {@link DataProcessor} instance.
	 * 
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		logger.debug(String.format("Checking bean: %s", beanName));
		if (DataProcessor.class.isInstance(bean)){
			this.registerBean((DataProcessor) bean);
		}
		return bean;
	}

	/**
	 * Scans the {@link ApplicationContext} for instances of {@link DataProcessor} beans and registers
	 *   them.
	 */
	@SuppressWarnings("unchecked")
	public void addProcessorBeans() {
		List<DataProcessor> foundBeans = new ArrayList<>();
		for (Map.Entry entry: applicationContext.getBeansOfType(DataProcessor.class, false, false).entrySet()){
			DataProcessor bean = (DataProcessor) entry.getValue();
			if (bean != null && bean.getModel() != null){
				foundBeans.add(bean);
			} else {
				logger.warn(String.format("Found bean is null or has no set model: %s", entry.getKey()));
			}
		}
		for (DataProcessor bean: foundBeans) {
			registerBean(bean);
			logger.info(String.format("Registered DataProcessor bean %s for model %s",
					bean.getClass().getName(), bean.getModel().getName()));
		}
	}

	public boolean isSupportedDataType(String dataType){
		return dataTypeMap.containsKey(dataType);
	}

	/**
	 * Tests to see if the submitted {@link Model} has an associated {@link DataProcessor} registered.
	 * 
	 * @param model
	 * @return
	 */
	public boolean isSupportedModel(Class<? extends Model> model) {
		return modelProcessorMap.containsKey(model);
	}

	/**
	 * Tests to see if the submitted {@link DataProcessor} bean has been registered.
	 * 
	 * @param bean
	 * @return
	 */
	public boolean isRegistered(DataProcessor bean) {
		return dataTypeMap.containsValue(bean);
	}

	/**
	 * Returns a list of unique registered {@link DataProcessor} beans.
	 * 
	 * @return list of processor.
	 */
	public List<DataProcessor> getRegisteredProcessors() {
		Set<DataProcessor> processors = new HashSet<>();
		for (List<DataProcessor> processor: modelProcessorMap.values()){
			processors.addAll(processor);
		}
		return new ArrayList<>(processors);
	}

  /**
   * Returns a list of registered data types.
   *
   * @return
   */
	public List<String> getRegisteredDataTypes(){
	  return new ArrayList<>(dataTypeMap.keySet());
  }

	/**
	 * Returns a list of {@link Model} types that have associated {@link DataProcessor} beans registered.
	 * 
	 * @return list of models.
	 */
	public List<Class<? extends Model>> getRegisteredModels() {
		return new ArrayList<>(modelProcessorMap.keySet());
	}

  public Map<String, String> getDataTypeDescriptionMap() {
    return dataTypeDescriptionMap;
  }

  @Override 
	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
