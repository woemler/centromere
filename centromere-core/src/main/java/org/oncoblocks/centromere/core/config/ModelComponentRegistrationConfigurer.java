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
import org.springframework.core.annotation.Order;

/**
 * @author woemler
 * @since 0.4.3
 */
@Order
public abstract class ModelComponentRegistrationConfigurer {
	
	@Autowired private ApplicationContext applicationContext;
	private static final Logger logger = LoggerFactory.getLogger(ModelComponentRegistrationConfigurer.class);
	
	@Bean
	public ModelRegistry modelRegistry(){
		ModelRegistry modelRegistry;
		if (this.getClass().isAnnotationPresent(ModelScan.class)) {
			ModelScan modelScan = this.getClass().getAnnotation(ModelScan.class);
			modelRegistry = ModelRegistry.fromModelScan(modelScan);
		} else if (this.getClass().isAnnotationPresent(ComponentScan.class)) {
			ComponentScan componentScan = this.getClass().getAnnotation(ComponentScan.class);
			modelRegistry = ModelRegistry.fromComponentScan(componentScan);
		} else if (this.getClass().getSuperclass().isAnnotationPresent(ModelScan.class)) {
			ModelScan modelScan = this.getClass().getSuperclass().getAnnotation(ModelScan.class);
			modelRegistry = ModelRegistry.fromModelScan(modelScan);
		} else if (this.getClass().getSuperclass().isAnnotationPresent(ComponentScan.class)) {
			ComponentScan componentScan = this.getClass().getSuperclass().getAnnotation(ComponentScan.class);
			modelRegistry = ModelRegistry.fromComponentScan(componentScan);
		} else {
			modelRegistry = new ModelRegistry();
		}
		modelRegistry.setRepositoryRegistry(modelRepositoryBeanRegistry());
		modelRegistry.setProcessorRegistry(modelProcessorBeanRegistry());
		//modelRegistry.afterPropertiesSet();
		return modelRegistry;
	}

	@Bean
	public ModelBeanRegistry<RepositoryOperations> modelRepositoryBeanRegistry(){
		return configureModelRepositoryBeanRegistry();
	}
	
	protected ModelBeanRegistry<RepositoryOperations> configureModelRepositoryBeanRegistry(){
		return new ModelRepositoryBeanRegistry(applicationContext);
	}
	
	@Bean
	public ModelBeanRegistry<RecordProcessor> modelProcessorBeanRegistry(){
		return configureModelProcessorBeanRegistry();
	}
	
	protected ModelBeanRegistry<RecordProcessor> configureModelProcessorBeanRegistry(){
		return new DataTypeProcessorBeanRegistry(applicationContext);
	}
	
}
