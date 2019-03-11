/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.repository;

import com.blueprint.centromere.core.exceptions.ConfigurationException;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Creates a registry of {@link ModelRepository} instances that have the {@link ModelResource} annotation,
 *   and creates a map of {@link Model} classes to repositories, and model URIs to repositories.
 *   This allows lookup of repository classes by model or by HTTP request URL.
 * 
 * @author woemler
 */
public class DefaultModelRepositoryRegistry implements ModelRepositoryRegistry {

  private static final Logger logger = LoggerFactory.getLogger(DefaultModelRepositoryRegistry.class);

  private final ApplicationContext context;
  private Map<Class<? extends Model<?>>, ModelRepository<?,?>> repositoryTypeMap = new HashMap<>();

  public DefaultModelRepositoryRegistry(ApplicationContext context) {
    this.context = context;
  }

  /**
   * Populates the registry with {@link ModelRepository} beans and their associated {@link Model} types.
   * 
   * @throws ConfigurationException
   */
  @PostConstruct
  public void afterPropertiesSet() throws ConfigurationException{
    //TODO: try this to get rid of warnings: https://stackoverflow.com/questions/14236424/how-can-i-find-all-beans-with-the-custom-annotation-foo
    for (Map.Entry<String, Object> entry: context.getBeansWithAnnotation(ModelResource.class).entrySet()){
      Class<?> type = entry.getValue().getClass();
      ModelRepository repository = (ModelRepository) entry.getValue();
      Class<? extends Model<?>> model = repository.getModel();
      if (repositoryTypeMap.containsKey(model)) throw new ModelRegistryException(String.format(
          "Duplicate model registered for repository %s.  Does more than one repository have the "
              + "same model?", model.getName()));
      repositoryTypeMap.put(model, repository);
      logger.debug(String.format("Registered repository %s for model %s",
          type.getName(), model.getName()));
    }
  }

  @Override
  public boolean isRegisteredModel(Class<?> model) {
    for (Class<?> type: repositoryTypeMap.keySet()){
      if (model.isAssignableFrom(type)) return true;
    }
    return false;
  }

  @Override
  public ModelRepository getRepositoryByModel(Class<? extends Model<?>> model)
      throws ModelRegistryException{
    List<ModelRepository> repositories = new ArrayList<>();
    for (Map.Entry<Class<? extends Model<?>>, ModelRepository<?,?>> entry: repositoryTypeMap.entrySet()){
      if (model.isAssignableFrom(entry.getKey())) repositories.add(entry.getValue());
    }
    if (repositories.size() > 1) throw new ModelRegistryException(String.format("Type %s matches "
        + "more than one registered repository.  Is this a superclass with multiple model "
        + "subclasses?", model.getName()));
    return repositories.size() > 0 ? repositories.get(0) : null;
  }

  @Override
  public Collection<Class<? extends Model<?>>> getRegisteredModels(){
    return repositoryTypeMap.keySet();
  }
  
  @Override
  public Collection<? extends ModelRepository<?,?>> getRegisteredModelRepositories(){
    return repositoryTypeMap.values();
  }
  
}
