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
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
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
    for (Map.Entry entry: context.getBeansOfType(ModelRepository.class, false, false).entrySet()){
      Class<?> type = entry.getValue().getClass();
      ModelRepository repository = (ModelRepository) entry.getValue();
      Class<? extends Model<?>> model = repository.getModel();
      String name = model.getSimpleName().toLowerCase();
      repositoryTypeMap.put(model, repository);
      if (AnnotatedElementUtils.hasAnnotation(type, ModelResource.class)){
        ModelResource annotation = AnnotatedElementUtils.findMergedAnnotation(repository.getClass(), ModelResource.class);
        if (!"".equals(annotation.name())){
          name = annotation.name().toLowerCase();
        } else if (!"".equals(annotation.value())){
          name = annotation.value().toLowerCase();
        }
      }
      typeNameMap.put(name, model);

      logger.info(String.format("Registered repository %s for model %s",
          repository.getClass().getName(), model.getName()));
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

  public ModelRepository getRepositoryByModel(Class<?> model){
    return repositoryTypeMap.containsKey(model) ? repositoryTypeMap.get(model) : null;
  }

  public Collection<Class<? extends Model<?>>> getRegisteredModels(){
    return repositoryTypeMap.keySet();
  }

  public Collection<ModelRepository<?,?>> getModelRepositories(){
    return repositoryTypeMap.values();
  }

}
