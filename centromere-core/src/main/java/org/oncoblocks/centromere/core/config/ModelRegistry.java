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
 * Keeps track of {@link Model} classes and their instantiated beans, and handles requests
 *   from components mapping model functions.  Can be instantiated from {@link ComponentScan} or 
 *   {@link ModelScan} annotations on configuration classes, to register specific models and 
 *   components.  Handles lookup of {@link org.oncoblocks.centromere.core.model.ModelSupport}
 *   components when beans are required, based on model identity.
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

	/**
	 * Ensures that required {@link ModelBeanRegistry} beans are present.
	 */
	@PostConstruct
	public void afterPropertiesSet(){
		Assert.notNull(repositoryRegistry, "RepositoryOperations registry must not be null.");
		Assert.notNull(processorRegistry, "RecordProcessor registry must not be null.");
	}
	
	/* Adding models */

	/**
	 * Adds a single {@link Model} to the registry, and maps its web service requeest URI.
	 * 
	 * @param model model to be added.
	 */
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

	/**
	 * Attemptes to register one or more {@link Model} classes at once.
	 * 
	 * @param models collection of models.
	 */
	public void addModels(Collection<Class<? extends Model>> models){
		for (Class<? extends Model> model: models){
			addModel(model);
		}
	}

	/**
	 * Attempts to locate and register {@link Model} classes on the supplied classpath.
	 * 
	 * @param classPath path to scan for models.
	 */
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

	/**
	 * Attempts to register all of the {@link Model} classes supplied in the {@link ComponentScan}
	 *   annotation or its registered classpaths.  Will give priority to paths in the {@code value}
	 *   attribute.
	 * 
	 * @param componentScan annotation instance.
	 */
	@SuppressWarnings("unchecked")
	public void addComponentScanModels(ComponentScan componentScan) {
		if (componentScan.value().length > 0) {
			for (String path : componentScan.value()) {
				this.addClassPathModels(path);
			}
		} else if (componentScan.basePackages().length > 0) {
			for (String path : componentScan.basePackages()) {
				this.addClassPathModels(path);
			}
		}
		if (componentScan.basePackageClasses().length > 0) {
			for (Class<?> model : componentScan.basePackageClasses()) {
				if (Model.class.isAssignableFrom(model)
						&& !Modifier.isAbstract(model.getModifiers())
						&& !Modifier.isInterface(model.getModifiers())) {
					this.addModel((Class<? extends Model>) model);
				} else {
					logger.warn(String.format("Cannot register non-model class: %s", model.getName()));
				}
			}
		}
	}

	/**
	 * Attempts to register all of the {@link Model} classes supplied in the {@link ModelScan}
	 *   annotation or its registered classpaths.  Will give priority to paths in the {@code value}
	 *   attribute.
	 *
	 * @param modelScan annotation instance.
	 */
	@SuppressWarnings("unchecked")
	public void addModelScanModels(ModelScan modelScan){
		if (modelScan.value().length > 0){
			for (String path: modelScan.value()){
				this.addClassPathModels(path);
			}
		} else if (modelScan.basePackages().length > 0){
			for (String path: modelScan.basePackages()){
				this.addClassPathModels(path);
			}
		}
		if (modelScan.basePackageClasses().length > 0){
			for (Class<?> model: modelScan.basePackageClasses()){
				if (Model.class.isAssignableFrom(model)
						&& !Modifier.isAbstract(model.getModifiers())
						&& !Modifier.isInterface(model.getModifiers())) {
					this.addModel((Class<? extends Model>) model);
				} else {
					logger.warn(String.format("Cannot register non-model class: %s", model.getName()));
				}
			}
		}
	}
	
	/* Finding models */

	/**
	 * Will return a registered {@link Model} class instance if the supplied string matches either
	 *   the fully qualified class name or simple class name.
	 * 
	 * @param name class identifier.
	 * @return model class instance.
	 */
	public Class<? extends Model> getModel(String name){
		for (Class<? extends Model> model: models){
			if (model.getName().equals(name) || model.getName().toLowerCase().equals(name) 
					|| model.getSimpleName().equals(name) || model.getSimpleName().toLowerCase().equals(name)){
				return model;
			}
		}
		return null;
	}

	/**
	 * eturns all registered {@link Model} classes.
	 * 
	 * @return
	 */
	public List<Class<? extends Model>> getModels() {
		return models;
	}

	/**
	 * Returns true if the supplied class is a registered {@link Model}.
	 * 
	 * @param model class to check.
	 * @return true if model is registered.
	 */
	public boolean isSupported(Class<? extends Model> model){
		return models.contains(model);
	}

	/**
	 * Returnes true if the supplied string matches against a registered {@link Model}, as determiend 
	 *   by the {@link #getModel(String)} method.
	 * 
	 * @param name class identifier.
	 * @return true if model is registered.
	 */
	public boolean isSupported(String name){
		return getModel(name) != null;
	}
	
	/* Controller mapping */

	/**
	 * Returns true if the supplied string matches the URI of a registered {@link Model} class.
	 * 
	 * @param uri model URI
	 * @return true if URI matches.
	 */
	public boolean isSupportedUri(String uri){
		return uriMap.containsKey(uri);
	}

	/**
	 * Returns the {@link Model} that matches the supplied URI, or null if it is not mapped.
	 * 
	 * @param uri model URI
	 * @return class that maps to the URI
	 */
	public Class<? extends Model> getModelFromUri(String uri){
		return uriMap.containsKey(uri) ? uriMap.get(uri) : null;
	}

	/**
	 * Returns the URI for the supplied {@link Model}, if one has been registered.  Returns null
	 *   otherwise.
	 * 
	 * @param model registered model class.
	 * @return URI mapped to the model.
	 */
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

	/**
	 * Returns a {@link RepositoryOperations} bean for the supplied {@link Model}, if one is registered.
	 *   Returns null otherwise.
	 * 
	 * @param model registered model.
	 * @return repository implementation for the model.
	 */
	public RepositoryOperations getModelRepository(Class<? extends Model> model){
		return repositoryRegistry.isRegistered(model) ? repositoryRegistry.get(model) : null;
	}

	/**
	 * Returns the {@link ModelBeanRegistry} instance for handling {@link RepositoryOperations} bean
	 *   registration.
	 * 
	 * @return registry instance.
	 */
	public ModelBeanRegistry<RepositoryOperations> getRepositoryRegistry() {
		return repositoryRegistry;
	}

	/**
	 * Sets the @link ModelBeanRegistry} instance for handling {@link RepositoryOperations} bean
	 *   registration.
	 * 
	 * @param repositoryRegistry registry instance.
	 */
	public void setRepositoryRegistry(ModelBeanRegistry<RepositoryOperations> repositoryRegistry) {
		this.repositoryRegistry = repositoryRegistry;
	}
	
	/* Processors */

	/**
	 * Returns the default {@link RecordProcessor} for handling the supplied {@link Model} class
	 *   supplied.  Returns null if one is not registered, or if the default cannot be determined.
	 * 
	 * @param model registered model.
	 * @return processor instance.
	 */
	public RecordProcessor getModelProcessor(Class<? extends Model> model){
		if (processorRegistry.isRegistered(model)){
			return processorRegistry.get(model);
		} 
		return null;
	}

	/**
	 * Returns the {@link ModelBeanRegistry} instance for handling {@link RecordProcessor} bean
	 *   registration.
	 *
	 * @return registry instance.
	 */
	public ModelBeanRegistry<RecordProcessor> getProcessorRegistry() {
		return processorRegistry;
	}

	/**
	 * Sets the @link ModelBeanRegistry} instance for handling {@link RecordProcessor} bean
	 *   registration.
	 *
	 * @param processorRegistry registry instance.
	 */
	public void setProcessorRegistry(
			ModelBeanRegistry<RecordProcessor> processorRegistry) {
		this.processorRegistry = processorRegistry;
	}
	
	/* Listener */

	/**
	 * Event handler for {@link ContextRefreshedEvent}, which triggers available {@link ModelBeanRegistry}
	 *   instances to check the application context for supportable beans and their associated
	 *   {@link Model} classes.  
	 * 
	 * @param event
	 */
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

	/**
	 * Constructs a instance from a {@link ComponentScan} annotation instance.
	 * 
	 * @param componentScan
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ModelRegistry fromComponentScan(ComponentScan componentScan){
		ModelRegistry registry = new ModelRegistry();
		registry.addComponentScanModels(componentScan);
		return registry;
	}

	/**
	 * Constructs an instance from a {@link ModelScan} instance.
	 * 
	 * @param modelScan
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ModelRegistry fromModelScan(ModelScan modelScan){
		ModelRegistry registry = new ModelRegistry();
		registry.addModelScanModels(modelScan);
		return registry;
	}
	
}
