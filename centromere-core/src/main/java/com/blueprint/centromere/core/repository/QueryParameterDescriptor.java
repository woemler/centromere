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

package com.blueprint.centromere.core.repository;

import com.blueprint.centromere.core.model.Model;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Transient;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * POJO that describes a model query parameter, used when reflecting {@link com.blueprint.centromere.core.model.Model}
 *   classes and mapping HTTP requests to {@link QueryCriteria}.
 *
 * @author woemler
 * @since 0.4.2
 */
public class QueryParameterDescriptor {

  private String name;
  private Path path;
  private Class<?> type;
  private Evaluation evaluation;

  public QueryParameterDescriptor() { }

  public QueryParameterDescriptor(String name, Path path, Class<?> type,
      Evaluation evaluation) {
    this.name = name;
    this.path = path;
    this.type = type;
    this.evaluation = evaluation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  public Evaluation getEvaluation() {
    return evaluation;
  }

  public void setEvaluation(Evaluation evaluation) {
    this.evaluation = evaluation;
  }

  @Override
  public String toString() {
    return "QueryParameterDescriptor{" +
        "name='" + name + '\'' +
        ", path=" + path +
        ", type=" + type +
        ", evaluation=" + evaluation +
        '}';
  }

  /**
   * Inspects a {@link Model} class and returns all of the available and acceptable query parameter
   *   definitions, as a map of parameter names and {@link QueryParameterDescriptor} objects.
   *
   * @param model
   * @return
   */
  public static Map<String,QueryParameterDescriptor> getModelQueryDescriptors(Class<?> model){

    Map<String,QueryParameterDescriptor> paramMap = new HashMap<>();
    PathBuilder pathBuilder = new PathBuilder<>(model, model.getSimpleName());
    Path root = Expressions.path(model, model.getSimpleName());
    Class<?> currentClass = model;

    while (currentClass.getSuperclass() != null) {

      for (Field field : currentClass.getDeclaredFields()) {

        String fieldName = field.getName();
        String paramName = fieldName;
        Class<?> type = field.getType();
        Class<?> paramType = type;
        Path path = null;

        if (field.isSynthetic() || field.isAnnotationPresent(Transient.class))
          continue;
        if (field.isAnnotationPresent(RestResource.class)) {
          RestResource restResource = field.getAnnotation(RestResource.class);
          if (!restResource.exported())
            continue;
        }

        if (String.class.isAssignableFrom(type)){
          path = Expressions.stringPath(root, fieldName);
        } else if (Number.class.isAssignableFrom(type)) {
          path = pathBuilder.getNumber(fieldName, type);
        } else if (Map.class.isAssignableFrom(type)){
          ParameterizedType pType = (ParameterizedType) field.getGenericType();
          Class<?> keyType = pType.getActualTypeArguments()[0].getClass();
          Class<?> valueType = pType.getActualTypeArguments()[1].getClass();
          path = pathBuilder.getMap(fieldName, keyType, valueType);
          paramType = valueType;
        } else if (Collection.class.isAssignableFrom(type)) {
          ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
          paramType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
          path = pathBuilder.getList(fieldName, paramType);
        }

        QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
        descriptor.setName(paramName);
        descriptor.setType(paramType);
        descriptor.setPath(path);
        descriptor.setEvaluation(Evaluation.EQUALS);
        paramMap.put(paramName, descriptor);

      }

      currentClass = currentClass.getSuperclass();

    }

    return paramMap;
  }
  
}
