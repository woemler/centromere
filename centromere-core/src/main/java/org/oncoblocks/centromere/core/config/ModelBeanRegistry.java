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
import org.springframework.beans.BeansException;

import java.util.Collection;

/**
 * @author woemler
 * @since 0.4.3
 */
public interface ModelBeanRegistry<T> {
	T get(Class<? extends Model> model);
	void registerBean(Class<? extends Model> model, T component);
	T createBean(Class<? extends Model> model) throws BeansException;
	boolean isRegistered(Class<? extends Model> model);
	boolean isRegistered(T bean);
	void addModelBeans(Collection<Class<? extends Model>> models);
	void addModelBeansFromContext();
	Collection<T> getRegisteredBeans();
	Collection<Class<? extends Model>> getRegisteredBeanModels();
}
