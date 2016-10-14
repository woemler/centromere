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

import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;

/**
 * Basic configuration for {@link ModelRegistry} and required {@link ModelBeanRegistry} instances 
 *   for handling repository mapping, processor mapping, and web service URI mapping. Creates and 
 *   configures the {@link ModelRegistry}.  By default, this registry will look for models
 *   in a couple different places, and will move down this list until it discovers at least a 
 *   single model class:
 *
 *   1. @ModelScan on superclass
 *   2. @ComponentScan on superclass
 *   3. Classpath supplied in property centromere.models.base-package
 *   4. Other beans in context with @ModelScan
 *   5. Other beans in context with @ComponentScan
 *
 *   If no models are found at initialization, the registry will check to see if {@link ModelBeanRegistry}
 *   instances have picked up any component classes and register their respective 
 *   {@link org.oncoblocks.centromere.core.model.Model} classes.
 *   
 *   The ModelBeanRegistry classes will attempt to locate already instantiated {@link ModelComponentFactory}
 *   instances they can use to create beans, if required by the centromere.models.auto-create-components
 *   property.
 * 
 * @author woemler
 * @since 0.4.3
 */
public abstract class ModelComponentRegistrationConfigurer {
	
	@Autowired private ApplicationContext applicationContext;
	@Autowired(required = false) private ModelComponentFactory<RepositoryOperations> modelRepositoryFactory;
	@Autowired(required = false) private ModelComponentFactory<RecordProcessor> recordProcessorFactory;
	@Autowired private Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(ModelComponentRegistrationConfigurer.class);

	@Bean
	public ModelRegistry modelRegistry(){
		
		ModelRegistry modelRegistry = ModelRegistry.fromConfigurationClass(this.getClass());
		
		// If no models have been found, check for property-defined model path
		if (env.containsProperty("centromere.models.base-package") 
				&& env.getProperty("centromere.models.base-package") != null
				&& !env.getProperty("centromere.models.base-package").equals("")){
			modelRegistry.addClassPathModels(env.getRequiredProperty("centromere.models.base-package"));
		}
		
		// If no models have been found, check for config classes with ModelScan and ComponentScan annotations
		if (modelRegistry.getModels().isEmpty()){
			for (Object bean: applicationContext.getBeansWithAnnotation(ModelScan.class).values()){
				ModelScan modelScan = AnnotatedElementUtils.getMergedAnnotation(bean.getClass(), ModelScan.class);
				if (modelScan != null) {
					modelRegistry.addModelScanModels(modelScan);
				}
			}
		}
		if (modelRegistry.getModels().isEmpty()){
			for (Object bean: applicationContext.getBeansWithAnnotation(ComponentScan.class).values()){
				ComponentScan componentScan = AnnotatedElementUtils.getMergedAnnotation(bean.getClass(), ComponentScan.class);
				if (componentScan != null) {
					modelRegistry.addComponentScanModels(componentScan);
				}
			}
		}
		
		logger.info(String.format("ModelRegistry created with registered models: %s", 
				modelRegistry.getModels().toString()));
		
		// Set component registries
		modelRegistry.setRepositoryRegistry(modelRepositoryBeanRegistry());
		modelRegistry.setProcessorRegistry(modelProcessorBeanRegistry());
		
		return modelRegistry;
		
	}

	/**
	 * Creates the {@link ModelBeanRegistry} instance for registering {@link RepositoryOperations}
	 *   implementations.
	 * 
	 * @return
	 */
	@Bean
	public ModelBeanRegistry<RepositoryOperations> modelRepositoryBeanRegistry(){
		return configureModelRepositoryBeanRegistry();
	}
	
	protected ModelBeanRegistry<RepositoryOperations> configureModelRepositoryBeanRegistry(){
		ModelRepositoryBeanRegistry repositoryRegistry 
				= new ModelRepositoryBeanRegistry(applicationContext);
		if (modelRepositoryFactory != null){
			repositoryRegistry.setModelComponentFactory(modelRepositoryFactory);
			if (env.containsProperty("centromere.models.auto-create-components")){
				boolean flag = false;
				try {
					flag = Boolean.parseBoolean(env.getRequiredProperty("centromere.models.auto-create-components"));
				} catch (Exception e){
					logger.warn("Error parsing property for centromere.models.auto-create-components!");
				}
				repositoryRegistry.setCreateIfNull(flag);
			}
		}
		return  repositoryRegistry;
	}
	
	@Bean
	public ModelBeanRegistry<RecordProcessor> modelProcessorBeanRegistry(){
		return configureModelProcessorBeanRegistry();
	}
	
	protected ModelBeanRegistry<RecordProcessor> configureModelProcessorBeanRegistry(){
		DataTypeProcessorBeanRegistry processorRegistry 
				= new DataTypeProcessorBeanRegistry(applicationContext);
		if (recordProcessorFactory != null){
			processorRegistry.setModelComponentFactory(recordProcessorFactory);
			if (env.containsProperty("centromere.models.auto-create-components")) {
				boolean flag = false;
				try {
					flag = Boolean
							.parseBoolean(env.getRequiredProperty("centromere.models.auto-create-components"));
				} catch (Exception e) {
					logger.warn("Error parsing property for centromere.models.auto-create-components!");
				}
				processorRegistry.setCreateIfNull(flag);
			}
		}
		return processorRegistry;
	}

}
