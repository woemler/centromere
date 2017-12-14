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

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelResource;
import java.util.Collection;
import java.util.HashMap;
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
public class ModelRepositoryRegistry {

  private static final Logger logger = LoggerFactory.getLogger(ModelRepositoryRegistry.class);

  private final ApplicationContext context;
  private Map<String, Class<? extends Model<?>>> typeNameMap = new HashMap<>();
  private Map<Class<? extends Model<?>>, ModelRepository<?,?>> repositoryTypeMap = new HashMap<>();

  public ModelRepositoryRegistry(ApplicationContext context) {
    this.context = context;
  }

  @PostConstruct
  public void afterPropertiesSet(){
    for (Map.Entry<String, Object> entry: context.getBeansWithAnnotation(ModelResource.class).entrySet()){
      Class<?> type = entry.getValue().getClass();
      ModelRepository repository = (ModelRepository) entry.getValue();
      Class<? extends Model<?>> model = repository.getModel();
      String name = model.getSimpleName().toLowerCase();
      repositoryTypeMap.put(model, repository);
      ModelResource annotation = context.findAnnotationOnBean(entry.getKey(), ModelResource.class);
      //ModelResource annotation = AnnotatedElementUtils.findMergedAnnotation(repository.getClass(), ModelResource.class);
      if (!annotation.name().trim().equals("")){
        name = annotation.name().toLowerCase();
      } else if (!annotation.value().trim().equals("")){
        name = annotation.value().toLowerCase();
      }
      typeNameMap.put(name, model);
      logger.info(String.format("Registered repository %s for model %s",
          type.getName(), model.getName()));
    }
  }

  public String getModelUri(Class<?> model){
    for (Map.Entry<String, Class<? extends Model<?>>> entry: typeNameMap.entrySet()){
      if (model.equals(entry.getValue())) return entry.getKey();
    }
    return null;
  }

  public boolean isRegisteredResource(String name){
    return typeNameMap.containsKey(name.toLowerCase());
  }

  public Class<?> getModelByResource(String name){
    return typeNameMap.containsKey(name.toLowerCase()) ? typeNameMap.get(name.toLowerCase()) : null;
  }

  public boolean isRegisteredModel(Class<?> model){
    return repositoryTypeMap.containsKey(model);
  }
  
  public boolean isRegisteredModel(String name) {
    for (Class<?> model: repositoryTypeMap.keySet()){
      if (name.equalsIgnoreCase(model.getName())) return true;
      else if (name.equalsIgnoreCase(model.getSimpleName())) return true;
      else if (name.equalsIgnoreCase(model.getSimpleName().replace(".class", ""))) return true;
    }
    return false;
  }
  
  public Class<? extends Model<?>> getRegisteredModel(String name){
    for (Class<? extends Model<?>> model: repositoryTypeMap.keySet()){
      if (name.equalsIgnoreCase(model.getName())) return model;
      else if (name.equalsIgnoreCase(model.getSimpleName())) return model;
      else if (name.equalsIgnoreCase(model.getSimpleName().replace(".class", ""))) return model;
    }
    return null;
  }

  public ModelRepository getRepositoryByModel(Class<?> model){
    return repositoryTypeMap.containsKey(model) ? repositoryTypeMap.get(model) : null;
  }

  public Collection<Class<? extends Model<?>>> getRegisteredModels(){
    return repositoryTypeMap.keySet();
  }
  
  public Collection<String> getRegisteredResources(){
    return typeNameMap.keySet();
  }

  public Collection<ModelRepository<?,?>> getModelRepositories(){
    return repositoryTypeMap.values();
  }

}
