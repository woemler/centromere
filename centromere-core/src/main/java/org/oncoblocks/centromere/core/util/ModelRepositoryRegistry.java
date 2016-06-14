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

import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Registers {@link RepositoryOperations} beans and associates them with their respective 
 *   {@link org.oncoblocks.centromere.core.model.Model} classes.  Maps both the bean names and 
 *   class references of the repositories.
 * 
 * @author woemler
 * @since 0.4.1
 */
public class ModelRepositoryRegistry extends AbstractComponentRegistry<RepositoryOperations> 
		implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	private static final Logger logger = LoggerFactory.getLogger(ModelRepositoryRegistry.class);
	private Map<Class<?>, Set<RepositoryOperations>> repositoryClassMap = new HashMap<>();

	public ModelRepositoryRegistry() {
	}

	public ModelRepositoryRegistry(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Configures the registry object and builds the object mappings.
	 */
	@PostConstruct
	public void configure(){
		Assert.notNull(applicationContext, "ApplicationContext must not be null.");
		for (Map.Entry entry: applicationContext.getBeansOfType(RepositoryOperations.class).entrySet()){
			this.add((String) entry.getKey(), (RepositoryOperations) entry.getValue());
		}
	}

	/**
	 * Adds a new model repository mapping.
	 * 
	 * @param repository {@link RepositoryOperations} bean instance.
	 */
	@Override 
	public void add(RepositoryOperations repository) {
		this.add(repository.getClass());
	}
	
	private void addRepositoryClassMapping(RepositoryOperations repository){
		Set<RepositoryOperations> set = new HashSet<>();
		if (repositoryClassMap.containsKey(repository.getModel())){
			set = repositoryClassMap.get(repository.getModel());
		}
		set.add(repository);
		repositoryClassMap.put(repository.getModel(), set);
	}

	@Override 
	public void add(String name, RepositoryOperations repository) {
		super.add(name, repository);
		addRepositoryClassMapping(repository);
	}

	/**
	 * Searches the {@link ApplicationContext} for {@link RecordProcessor} beans and attempts to 
	 *   register them.
	 * 
	 * @param type class of {@link RepositoryOperations} bean.
	 */
	public void add(Class<? extends RepositoryOperations> type){
		Map<String, ? extends RepositoryOperations> map = applicationContext.getBeansOfType(type);
		for (Map.Entry entry: map.entrySet()){
			this.add((String) entry.getKey(), (RepositoryOperations) entry.getValue());
		}
	}

	/**
	 * Returns all instances of {@link RepositoryOperations} beans that are associated with a 
	 *   particular {@link Model} class.
	 * 
	 * @param type {@link Model} class.
	 * @return
	 */
	public List<RepositoryOperations> findByModel(Class<? extends Model> type){
		if (repositoryClassMap.containsKey(type)){
			return new ArrayList<>(repositoryClassMap.get(type));
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Tests to see if a {@link Model} has an {@link RepositoryOperations} bean registered.
	 * 
	 * @param type {@link Model} class.
	 * @return
	 */
	public boolean exists(Class<? extends Model> type){
		return repositoryClassMap.containsKey(type);
	}

	@Autowired 
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
