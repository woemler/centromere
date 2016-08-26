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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.Introspector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author woemler
 * @since 0.4.3
 */
public abstract class AbstractModelBeanRegistry<T extends ModelSupport> implements ModelBeanRegistry<T> {

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
	public T get(Class<? extends Model> model) {
		return map.containsKey(model) ? map.get(model) : null;
	}

	@Override 
	public boolean isSupported(Class<? extends Model> model) {
		return map.containsKey(model);
	}

	@Override 
	public void registerBean(Class<? extends Model> model, T bean) {
		map.put(model, bean);
	}

	@Override 
	public T createBean(Class<? extends Model> model) {
		if (beanFactory != null){
			return beanFactory.getComponent(model);
		} else {
			throw new ModelRegistryException(String.format("No model bean factory set for component class: %s.",
					getBeanClass().getName()));
		}
	}

	@Override
	public void addModelBeans(Collection<Class<? extends Model>> models) {
		Map<Class<? extends Model>, T> foundBeans = new HashMap<>();
		for (Map.Entry entry: applicationContext.getBeansOfType(getBeanClass(), false, false).entrySet()){
			T repo = (T) entry.getValue();
			if (repo != null && repo.getModel() != null){
				foundBeans.put(repo.getModel(), repo);
			} else {
				logger.warn(String.format("Found bean is null or has no set model: %s", entry.getKey()));
			}
		}
		for (Class<? extends Model> model: models){
			if (foundBeans.containsKey(model)){
				this.registerBean(model, foundBeans.get(model));
				logger.info(String.format("Registered bean %s for model %s", 
						foundBeans.get(model).getClass().getName(), model.getName()));
			} else if (createIfNull) {
				T bean = createBean(model);
				((AnnotationConfigApplicationContext) applicationContext).getBeanFactory()
						.registerSingleton(Introspector.decapitalize(model.getSimpleName()) 
								+ getCreatedBeanNameSuffix(), bean);
				this.registerBean(model, bean);
				logger.info(String.format("Created and registered bean %s for model %s",
						bean.getClass().getName(), model.getName()));
			} else {
				logger.warn(String.format("No beans found for model %s and no factory present.", model));
			}
		}
	}

	public void setCreateIfNull(boolean createIfNull) {
		this.createIfNull = createIfNull;
	}

	public void setModelComponentFactory(
			ModelComponentFactory<T> modelComponentFactory) {
		this.beanFactory = modelComponentFactory;
	}
}
