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
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.util.MultiValueMap;

/**
 * @author woemler
 */
public class QueryUtils {

  private static final Logger logger = LoggerFactory.getLogger(QueryUtils.class);

  /**
   * Extracts request parameters and matches them to available database query parameters, as defined
   *   in the {@code model} class definition.
   *
   * @param model
   * @param parameters
   * @return
   */
  public static List<QueryCriteria> getQueryCriteriaFromRequestParameters(
      Class<?> model, 
      MultiValueMap<String, String> parameters
  ){
    logger.info(String.format("Generating QueryCriteria for request parameters: model=%s params=%s",
        model.getName(), parameters.toString()));
    List<QueryCriteria> criteriaList = new ArrayList<>();
    Map<String, QueryParameterDescriptor> paramMap = getAvailableQueryParameters(model);
    for (Map.Entry<String, List<String>> entry: parameters.entrySet()){
      String paramName = entry.getKey();
      String[] paramValue = entry.getValue().get(0).split(",");
      if (excludedParameters.contains(paramName)) continue;
      if (paramMap.containsKey(paramName)) {
        QueryParameterDescriptor descriptor = paramMap.get(paramName);
        QueryCriteria criteria = createCriteriaFromRequestParameter(descriptor.getFieldName(),
            paramValue, descriptor.getType(), descriptor.getEvaluation());
        criteriaList.add(criteria);
      } else {
        logger.warn(String
            .format("Unable to map request parameter to available model parameters: %s",
                paramName));
        throw new InvalidParameterException("Invalid request parameter: " + paramName);
      }
    }
    logger.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));
    return criteriaList;
  }

  /**
   * Inspects a {@link Model} class and returns all of the available and acceptable query parameter
   *   definitions, as a map of parameter names and {@link QueryParameterDescriptor} objects.
   *
   * @param model
   * @return
   */
  public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(Class<?> model){
    
    Map<String,QueryParameterDescriptor> paramMap = new HashMap<>();
    PathBuilder<?> pathBuilder = new PathBuilder<>(model, model.getSimpleName());
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

        if (type.equals(String.class)){
          path = Expressions.stringPath(root, fieldName);
        } else if (Number.class.isAssignableFrom(type)) {
          path = pathBuilder.getNumber(fieldName, Number.class);
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

  /**
   * Creates a {@link QueryCriteria} object based upon a request parameter and {@link Evaluation}
   *   value.
   *
   * @param param
   * @param values
   * @param type
   * @param evaluation
   * @return
   */
  public static QueryCriteria createCriteriaFromRequestParameter(
      String param, 
      Object[] values, 
      Class<?> type, 
      Evaluation evaluation
  ){
    logger.debug(String.format("Generating QueryCriteria object for query string parameter: "
        + "param=%s values=%s type=%s eval=%s", param, values.toString(), type.getName(), evaluation.toString()));
    if (evaluation.equals(Evaluation.EQUALS) && values.length > 1) evaluation = Evaluation.IN;
    switch (evaluation){
      case EQUALS:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.EQUALS);
      case NOT_EQUALS:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.NOT_EQUALS);
      case IN:
        return new QueryCriteria(param, convertParameterArray(values, type), Evaluation.IN);
      case NOT_IN:
        return new QueryCriteria(param, Arrays.asList(values), Evaluation.NOT_IN);
      case IS_NULL:
        return new QueryCriteria(param, true, Evaluation.IS_NULL);
      case NOT_NULL:
        return new QueryCriteria(param, true, Evaluation.NOT_NULL);
      case IS_TRUE:
        return new QueryCriteria(param, true, Evaluation.IS_TRUE);
      case IS_FALSE:
        return new QueryCriteria(param, true, Evaluation.IS_FALSE);
      case GREATER_THAN:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.GREATER_THAN);
      case GREATER_THAN_EQUALS:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.GREATER_THAN_EQUALS);
      case LESS_THAN:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.LESS_THAN);
      case LESS_THAN_EQUALS:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.LESS_THAN_EQUALS);
      case BETWEEN:
        return new QueryCriteria(param, Arrays.asList(convertParameter(values[0], type),
            convertParameter(values[1], type)), Evaluation.BETWEEN);
      case OUTSIDE:
        return new QueryCriteria(param, Arrays.asList(convertParameter(values[0], type),
            convertParameter(values[1], type)), Evaluation.OUTSIDE);
      case BETWEEN_INCLUSIVE:
        return new QueryCriteria(param, Arrays.asList(convertParameter(values[0], type),
            convertParameter(values[1], type)), Evaluation.BETWEEN_INCLUSIVE);
      case OUTSIDE_INCLUSIVE:
        return new QueryCriteria(param, Arrays.asList(convertParameter(values[0], type),
            convertParameter(values[1], type)), Evaluation.OUTSIDE_INCLUSIVE);
      case STARTS_WITH:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.STARTS_WITH);
      case ENDS_WITH:
        return new QueryCriteria(param, convertParameter(values[0], type), Evaluation.ENDS_WITH);
      default:
        return null;
    }
  }

  /**
   * Converts an object into the appropriate type defined by the model field being queried.
   *
   * @param param
   * @param type
   * @return
   */
  public static Object convertParameter(Object param, Class<?> type, ConversionService conversionService){
    logger.debug(String.format("Attempting to convert parameter: from=%s to=%s",
        param.getClass().getName(), type.getName()));
    if (conversionService.canConvert(param.getClass(), type)){
      try {
        return conversionService.convert(param, type);
      } catch (ConversionFailedException e){
        e.printStackTrace();
        throw new InvalidParameterException("Unable to convert String to " + type.getName());
      }
    } else {
      return param;
    }
  }

  /**
   * {@link QueryUtils#convertParameter(Object, Class, ConversionService)}
   */
  public static Object convertParameter(Object param, Class<?> type){
    ConversionService conversionService = new DefaultConversionService();
    return convertParameter(param, type, conversionService);
  }

  /**
   * Converts an array of objects into the appropriate type defined by the model field being queried
   *
   * @param params
   * @param type
   * @return
   */
  public static List<Object> convertParameterArray(Object[] params, Class<?> type){
    List<Object> objects = new ArrayList<>();
    for (Object param: params){
      objects.add(convertParameter(param, type));
    }
    return objects;
  }

  /**
   * Extracts the requested filtered fields parameter from a request.
   *
   * @param parameters
   * @return
   */
  public static Set<String> getFilteredFieldsFromRequest(MultiValueMap<String, String> parameters){
    Set<String> fields = null;
    if (parameters.containsKey("fields")){
      fields = new HashSet<>();
      String[] params = parameters.get("fields").get(0).split(",");
      for (String field: params){
        fields.add(field.trim());
      }
    }
    return fields;
  }

  /**
   * Extracts the requested filtered fields parameter from a request.
   *
   * @param parameters
   * @return
   */
  public static Set<String> getExcludedFieldsFromRequest(MultiValueMap<String, String> parameters){
    Set<String> exclude = null;
    if (parameters.containsKey("exclude")){
      exclude = new HashSet<>();
      String[] params = parameters.get("exclude").get(0).split(",");
      for (String field: params){
        exclude.add(field.trim());
      }
    }
    return exclude;
  }
  
  
}
