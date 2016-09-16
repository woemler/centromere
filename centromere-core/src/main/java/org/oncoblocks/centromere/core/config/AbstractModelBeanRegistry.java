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

import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.model.ModelSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.Introspector;
import java.util.*;

/**
 * Base implementation of {@link ModelBeanRegistry}, for registering beans that implement {@link ModelSupport}
 *   and associating them with their appropriate {@link Model}.  Can be initialized and configured 
 *   by {@link ModelRegistry}, or can pick up instatiated beans as a {@link BeanPostProcessor} as 
 *   they are created.
 * 
 * @author woemler
 * @since 0.4.3
 */
public abstract class AbstractModelBeanRegistry<T extends ModelSupport> 
		implements ModelBeanRegistry<T>, BeanPostProcessor {

	private final Map<Class<? extends Model>, T> map = new HashMap<>();
	private boolean createIfNull = false;
	private ModelComponentFactory<T> beanFactory;
	private final ApplicationContext applicationContext;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractModelBeanRegistry.class);

	abstract protected Class<T> getBeanClass();
	abstract protected String getCreatedBeanNameSuffix();
	
	public AbstractModelBeanRegistry(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override 
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override 
	@SuppressWarnings("unchecked")
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (getBeanClass().isInstance(bean)){
			T component = (T) bean;
			this.registerBean(component.getModel(), component);
		}
		return bean;
	}

	@Override 
	public T get(Class<? extends Model> model) {
		return map.containsKey(model) ? map.get(model) : null;
	}

	@Override 
	public boolean isRegistered(Class<? extends Model> model) {
		return map.containsKey(model);
	}

	@Override 
	public boolean isRegistered(T bean) {
		return map.containsValue(bean);
	}

	@Override 
	public void registerBean(Class<? extends Model> model, T bean) {
		map.put(model, bean);
		logger.info(String.format("Registering %s bean %s for model %s", 
				this.getBeanClass().getName(), bean.getClass().getName(), model.getName()));
	}

	@Override 
	public T createBean(Class<? extends Model> model) {
		if (beanFactory != null){
			return (T) beanFactory.getComponent(model);
		} else {
			throw new ModelRegistryException(String.format("No model bean factory set for component class: %s.",
					getBeanClass().getName()));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addModelBeans(Collection<Class<? extends Model>> models) {
		List<T> foundBeans = new ArrayList<>();
		for (Map.Entry entry: applicationContext.getBeansOfType(getBeanClass(), false, false).entrySet()){
			T bean = (T) entry.getValue();
			if (bean != null && bean.getModel() != null){
				foundBeans.add(bean);
			} else {
				logger.warn(String.format("Found bean is null or has no set model: %s", entry.getKey()));
			}
		}
		for (T bean: foundBeans) {
			for (Class<? extends Model> model : models) {
				if (bean.getModel().equals(model)) {
					registerBean(model, bean);
					logger.info(String.format("Registered %s bean %s for model %s",
							getBeanClass().getName(), bean.getClass().getName(),
							model.getName()));
				}
			}
		}
		for (Class<? extends Model> model: models){
			if (!this.isRegistered(model)){
				if (createIfNull) {
					T bean = createBean(model);
					((AnnotationConfigApplicationContext) applicationContext).getBeanFactory()
							.registerSingleton(Introspector.decapitalize(model.getSimpleName())
									+ getCreatedBeanNameSuffix(), bean);
					registerBean(model, bean);
					logger.info(String.format("Created and registered bean %s for model %s",
							bean.getClass().getName(), model.getName()));
				} else {
					logger.warn(String.format("No %s beans found for model %s and no factory present.",
							getBeanClass().getName(), model));
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addModelBeansFromContext() {
		List<T> foundBeans = new ArrayList<>();
		for (Map.Entry entry: applicationContext.getBeansOfType(getBeanClass(), false, false).entrySet()){
			T bean = (T) entry.getValue();
			if (bean != null && bean.getModel() != null){
				registerBean(bean.getModel(), bean);
				logger.info(String.format("Registered %s bean %s for model %s",
						getBeanClass().getName(), bean.getClass().getName(),
						bean.getModel().getName()));
			} else {
				logger.warn(String.format("Found bean is null or has no set model: %s", entry.getKey()));
			}
		}
	}

	public void setCreateIfNull(boolean createIfNull) {
		this.createIfNull = createIfNull;
	}

	public void setModelComponentFactory(ModelComponentFactory<T> modelComponentFactory) {
		this.beanFactory = modelComponentFactory;
	}

	@Override 
	public List<T> getRegisteredBeans() {
		return new ArrayList<>(map.values());
	}

	@Override 
	public List<Class<? extends Model>> getRegisteredBeanModels() {
		return new ArrayList<>(map.keySet());
	}
}
