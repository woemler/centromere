/*
 * Copyright 2017 the original author or authors
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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.exceptions.ConfigurationException;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelResource;
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
public class DefaultModelRepositoryRegistry implements ModelRepositoryRegistry, ModelResourceRegistry {

  private static final Logger logger = LoggerFactory.getLogger(DefaultModelRepositoryRegistry.class);

  private final ApplicationContext context;
  private Map<String, Class<? extends Model<?>>> uriMap = new HashMap<>();
  private Map<Class<? extends Model<?>>, ModelRepository<?,?>> repositoryTypeMap = new HashMap<>();

  public DefaultModelRepositoryRegistry(ApplicationContext context) {
    this.context = context;
  }

  @PostConstruct
  public void afterPropertiesSet() throws ConfigurationException{
    for (Map.Entry<String, Object> entry: context.getBeansWithAnnotation(ModelResource.class).entrySet()){
      Class<?> type = entry.getValue().getClass();
      ModelRepository repository = (ModelRepository) entry.getValue();
      Class<? extends Model<?>> model = repository.getModel();
      String name = model.getSimpleName().toLowerCase();
      if (repositoryTypeMap.containsKey(model)) throw new ModelRegistryException(String.format(
          "Duplicate model registered for repository %s.  Does more than one repository have the "
              + "same model?", model.getName()));
      repositoryTypeMap.put(model, repository);
      ModelResource annotation = context.findAnnotationOnBean(entry.getKey(), ModelResource.class);
      if (!annotation.name().trim().equals("")){
        name = annotation.name().toLowerCase();
      } else if (!annotation.value().trim().equals("")){
        name = annotation.value().toLowerCase();
      }
      if (uriMap.containsKey(name)) throw new ModelRegistryException(String.format("Duplicate URI "
          + "registered for %s.  Does another model class have the same URI?", name));
      uriMap.put(name, model);
      logger.info(String.format("Registered repository %s for model %s",
          type.getName(), model.getName()));
    }
  }

  @Override
  public String getUriByModel(Class<? extends Model<?>> model) throws ModelRegistryException{
    List<String> uris = new ArrayList<>();
    for (Map.Entry<String, Class<? extends Model<?>>> entry: uriMap.entrySet()){
      if (model.isAssignableFrom(entry.getValue())) uris.add(entry.getKey());
    }
    if (uris.isEmpty()) return null;
    else if (uris.size() == 1) return uris.get(0);
    else throw new ModelRegistryException(String.format("More than one URI applies to model %s.  "
          + "Is this a superclass with multiple model subclasses?", model.getName()));
  }

  @Override
  public boolean isRegisteredResource(String name){
    return uriMap.containsKey(name.toLowerCase());
  }

  @Override
  public Class<? extends Model<?>> getModelByUri(String name){
    return uriMap.getOrDefault(name.toLowerCase(), null);
  }

  @Override
  public boolean isRegisteredModel(Class<?> model) throws ModelRegistryException {
    int i = 0;
    for (Class<?> type: repositoryTypeMap.keySet()){
      if (model.isAssignableFrom(type)) i++;
    }
    if (i > 1) throw new ModelRegistryException(String.format("Type %s matches more than one "
        + "registered model. Is this a superclass with multiple model subclasses?", model.getName()));
    return i == 1;
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
  
  public Collection<String> getRegisteredResources(){
    return uriMap.keySet();
  }

  @Override
  public Collection<? extends ModelRepository<?,?>> getRegisteredModelRepositories(){
    return repositoryTypeMap.values();
  }

  @Override
  public Collection<String> getRegisteredModelUris() {
    return uriMap.keySet();
  }
}
