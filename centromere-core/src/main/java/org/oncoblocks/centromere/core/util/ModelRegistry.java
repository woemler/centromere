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

import org.oncoblocks.centromere.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link ComponentRegistry} implementation for tracking all instances of {@link Model} classes
 *   in a given project.
 * 
 * @author woemler
 * @since 0.4.1
 */
public class ModelRegistry extends AbstractComponentRegistry<Class<? extends Model>> {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelRegistry.class);
	
	public void addClasspathModels(String classpath) {
		logger.debug("Scanning classpath for Model classes: " + classpath);
		ClassPathScanningCandidateComponentProvider provider
				= new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AssignableTypeFilter(Model.class));
		for (BeanDefinition beanDef : provider.findCandidateComponents(classpath)) {
			try {
				Class<?> clazz = Class.forName(beanDef.getBeanClassName());
				if (Model.class.isAssignableFrom(clazz)){
					this.add((Class<? extends Model>) clazz);
				}
			} catch (ClassNotFoundException e){
				e.printStackTrace();
				logger.warn(String.format("Cannot find target class: %s", beanDef.getBeanClassName()));
			}
		}
	}
	
	@Override
	public void add(Class<? extends Model> model){
		logger.debug("Adding Model class to registry: " + model.getName());
		this.add(model.getSimpleName() , model);
	}

	@Override 
	public Iterable<Class<? extends Model>> getRegisteredComponents() {
		Set<Class<? extends Model>> models = new HashSet<>();
		for (Class<? extends Model> model: this.getRegistry().values()){
			models.add(model);
		}
		return models;
	}
	
	public static ModelRegistry fromModelScan(ModelScan modelScan) throws ClassNotFoundException {
		ModelRegistry registry = new ModelRegistry();
		if (modelScan.value().length > 0){
			for (String path: modelScan.value()){
				registry.addClasspathModels(path);
			}
		} else if (modelScan.basePackages().length > 0){
			for (String path: modelScan.basePackages()){
				registry.addClasspathModels(path);
			}
		}
		if (modelScan.basePackageClasses().length > 0){
			for (Class<?> model: modelScan.basePackageClasses()){
				if (Model.class.isAssignableFrom(model)) {
					registry.add((Class<? extends Model>) model);
				} else {
					logger.warn(String.format("Cannot register non-model class: %s", model.getName()));
				}
			}
		}
		return registry;
	}
	
}
