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
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.model.ModelAttributes;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Keeps class of classpath {@link Model} classes and their instantiated beans, and handles requests
 *   from components mapping model functions.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class ModelRegistry implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {
	
	private List<Class<? extends Model>> models = new ArrayList<>();
	private Map<String, Class<? extends Model>> uriMap = new HashMap<>();
	private ModelBeanRegistry<RepositoryOperations> repositoryRegistry;
	private ModelBeanRegistry<RecordProcessor> processorRegistry;
	
	private static final Logger logger = LoggerFactory.getLogger(ModelRegistry.class);
	
	@PostConstruct
	public void afterPropertiesSet(){
		Assert.notNull(repositoryRegistry, "RepositoryOperations registry must not be null.");
		Assert.notNull(processorRegistry, "RecordProcessor registry must not be null.");
	}
	
	/* Adding models */
	
	public void addModel(Class<? extends Model> model){
		if (!models.contains(model)){
			models.add(model);
			String uri = model.getSimpleName().toLowerCase();
			if (model.isAnnotationPresent(ModelAttributes.class)){
				ModelAttributes modelAttributes = model.getAnnotation(ModelAttributes.class);
				if (!modelAttributes.uri().equals("")){
					uri = modelAttributes.uri();
				}
			}
			uriMap.put(uri, model);
			logger.info(String.format("Registering model %s with URI %s", model.getName(), uri));
		}
	}
	
	public void addModels(Collection<Class<? extends Model>> models){
		for (Class<? extends Model> model: models){
			addModel(model);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addClassPathModels(String classPath){
		ClassPathScanningCandidateComponentProvider provider
				= new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AssignableTypeFilter(Model.class));
		for (BeanDefinition beanDef : provider.findCandidateComponents(classPath)) {
			try {
				Class<?> clazz = Class.forName(beanDef.getBeanClassName());
				if (Model.class.isAssignableFrom(clazz)){
					this.addModel((Class<? extends Model>) clazz);
				}
			} catch (ClassNotFoundException e){
				e.printStackTrace();
				logger.warn(String.format("Cannot find target class: %s", beanDef.getBeanClassName()));
			}
		}
	}
	
	/* Finding models */
	
	public Class<? extends Model> getModel(String name){
		for (Class<? extends Model> model: models){
			if (model.getName().equals(name) || model.getName().toLowerCase().equals(name) 
					|| model.getSimpleName().equals(name) || model.getSimpleName().toLowerCase().equals(name)){
				return model;
			}
		}
		return null;
	}


	public List<Class<? extends Model>> getModels() {
		return models;
	}

	public boolean isSupported(Class<? extends Model> model){
		return models.contains(model);
	}
	
	public boolean isSupported(String name){
		return getModel(name) != null;
	}
	
	/* Controller mapping */
	
	public boolean isSupportedUri(String uri){
		return uriMap.containsKey(uri);
	}
	
	public Class<? extends Model> getModelFromUri(String uri){
		return uriMap.containsKey(uri) ? uriMap.get(uri) : null;
	}
	
	public String getModelUri(Class<? extends Model> model){
		String uri = null;
		for (Map.Entry entry: uriMap.entrySet()){
			if (entry.getValue().equals(model)){
				uri = (String) entry.getKey();
			}
		}
		return uri;
	}
	
	/* Repositories */
	
	public RepositoryOperations getModelRepository(Class<? extends Model> model){
		return repositoryRegistry.isRegistered(model) ? repositoryRegistry.get(model) : null;
	}

	public ModelBeanRegistry<RepositoryOperations> getRepositoryRegistry() {
		return repositoryRegistry;
	}

	public void setRepositoryRegistry(ModelBeanRegistry<RepositoryOperations> repositoryRegistry) {
		this.repositoryRegistry = repositoryRegistry;
	}

	public ModelBeanRegistry<RecordProcessor> getProcessorRegistry() {
		return processorRegistry;
	}

	public void setProcessorRegistry(
			ModelBeanRegistry<RecordProcessor> processorRegistry) {
		this.processorRegistry = processorRegistry;
	}
	
	/* Listener */

	@Override 
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("Initializing post-refresh model bean registration.");
		repositoryRegistry.addModelBeansFromContext();
		processorRegistry.addModelBeansFromContext();
		if (!repositoryRegistry.getRegisteredBeanModels().isEmpty()){
			addModels(repositoryRegistry.getRegisteredBeanModels());
		}
		if (!processorRegistry.getRegisteredBeanModels().isEmpty()){
			addModels(processorRegistry.getRegisteredBeanModels());
		}
		if (models != null && !models.isEmpty()) {
			repositoryRegistry.addModelBeans(models);
			processorRegistry.addModelBeans(models);
		}
	}
	
	/* Static constructors */

	@SuppressWarnings("unchecked")
	public static ModelRegistry fromComponentScan(ComponentScan componentScan){
		ModelRegistry registry = new ModelRegistry();
		if (componentScan.value().length > 0){
			for (String path: componentScan.value()){
				registry.addClassPathModels(path);
			}
		} else if (componentScan.basePackages().length > 0){
			for (String path: componentScan.basePackages()){
				registry.addClassPathModels(path);
			}
		}
		if (componentScan.basePackageClasses().length > 0){
			for (Class<?> model: componentScan.basePackageClasses()){
				if (Model.class.isAssignableFrom(model) 
						&& !Modifier.isAbstract(model.getModifiers())
						&& !Modifier.isInterface(model.getModifiers())) {
					registry.addModel((Class<? extends Model>) model);
				} else {
					logger.warn(String.format("Cannot register non-model class: %s", model.getName()));
				}
			}
		}
		return registry;
	}

	@SuppressWarnings("unchecked")
	public static ModelRegistry fromModelScan(ModelScan modelScan){
		ModelRegistry registry = new ModelRegistry();
		if (modelScan.value().length > 0){
			for (String path: modelScan.value()){
				registry.addClassPathModels(path);
			}
		} else if (modelScan.basePackages().length > 0){
			for (String path: modelScan.basePackages()){
				registry.addClassPathModels(path);
			}
		}
		if (modelScan.basePackageClasses().length > 0){
			for (Class<?> model: modelScan.basePackageClasses()){
				if (Model.class.isAssignableFrom(model) 
						&& !Modifier.isAbstract(model.getModifiers()) 
						&& !Modifier.isInterface(model.getModifiers())) {
					registry.addModel((Class<? extends Model>) model);
				} else {
					logger.warn(String.format("Cannot register non-model class: %s", model.getName()));
				}
			}
		}
		return registry;
	}
	
}
