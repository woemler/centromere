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

import com.blueprint.centromere.core.exceptions.QueryParameterException;
import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * @author woemler
 */
public class QueryParameterUtil {

  private static final Logger logger = LoggerFactory.getLogger(QueryParameterUtil.class);

  /**
   * Inspects a {@link Model} class and returns all of the available and acceptable query parameter
   *   definitions, as a map of parameter names and {@link QueryParameterDescriptor} objects.
   *
   * @param model model to inspect
   * @return map of parameter names and their descriptors
   */
  public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(
      Class<? extends Model> model, boolean recursive)
  {

    logger.debug(String.format("Determining available query parameters for model: %s", model.getName()));
    Class<?> current = model;
    Map<String,QueryParameterDescriptor> paramMap = new HashMap<>();

    while (current.getSuperclass() != null) {

      for (Field field : current.getDeclaredFields()) {

        String fieldName = field.getName();
        String paramName = field.getName();
        Class<?> type = field.getType();
        Class<?> keyType = type;
        boolean regex = false;
        boolean dynamic = true;

        if (Collection.class.isAssignableFrom(type)) {

          ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
          type = (Class<?>) parameterizedType.getActualTypeArguments()[0];

        } else if (Map.class.isAssignableFrom(type)){

          ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
          keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
          type = (Class<?>) parameterizedType.getActualTypeArguments()[1];
          paramName = paramName + ".\\w+";
          regex = true;

        }

        if (field.isSynthetic()) continue;

        if (field.isAnnotationPresent(Ignored.class)) continue;

        QueryParameterDescriptor descriptor = new QueryParameterDescriptor(paramName, fieldName,
            type, Evaluation.EQUALS, regex, dynamic);
        paramMap.put(paramName, descriptor);

        logger.debug(String.format("Adding default query parameter: %s = %s",
            fieldName, descriptor.toString()));

        // TODO: Linked model queries?
//        if (field.isAnnotationPresent(Linked.class)) {
//
//          if (!recursive) continue;
//
//          Linked linked = field.getAnnotation(Linked.class);
//
//          logger.debug(String.format("Adding foreign key parameters for model: %s",
//              linked.model().getName()));
//
//          String relField = !"".equals(linked.rel()) ? linked.rel() : fieldName;
//          Map<String, QueryParameterDescriptor> foreignModelMap =
//              getAvailableQueryParameters(linked.model(), false);
//
//          for (QueryParameterDescriptor descriptor : foreignModelMap.values()) {
//
//            String newParamName = relField + "." + descriptor.getParamName();
//            descriptor.setParamName(newParamName);
//            paramMap.put(newParamName, descriptor);
//
//            logger.debug(String.format("Adding foreign key parameter: %s = %s",
//                newParamName, descriptor.toString()));
//
//          }
//
//        }

      }
      current = current.getSuperclass();
    }
    logger.debug(String.format("Found %d query parameters for model: %s", paramMap.size(), model.getName()));
    return paramMap;
  }

  /**
   * Inspects a {@link Model} class and returns all of the available and acceptable query parameter
   *   definitions, as a map of parameter names and {@link QueryParameterDescriptor} objects.
   *
   * @param model model to inspect
   * @return map of parameter names and their descriptors
   */
  public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(Class<? extends Model> model) {
    return getAvailableQueryParameters(model, true);
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
  public static QueryCriteria getQueryCriteriaFromParameter(
      String param,
      Object[] values,
      Class<?> type,
      Evaluation evaluation
  ){
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
      case LIKE :
        return new QueryCriteria(param, values[0], Evaluation.LIKE);
      case NOT_LIKE:
        return new QueryCriteria(param, values[0], Evaluation.NOT_LIKE);
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
  private static Object convertParameter(Object param, Class<?> type, ConversionService conversionService){
    if (conversionService.canConvert(param.getClass(), type)){
      try {
        return conversionService.convert(param, type);
      } catch (ConversionFailedException e){
        e.printStackTrace();
        throw new QueryParameterException("Unable to convert parameter string to " + type.getName());
      }
    } else {
      return param;
    }
  }

  /**
   * {@link QueryParameterUtil#convertParameter(Object, Class, ConversionService)}
   */
  private static Object convertParameter(Object param, Class<?> type){
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
  private static List<Object> convertParameterArray(Object[] params, Class<?> type){
    List<Object> objects = new ArrayList<>();
    for (Object param: params){
      objects.add(convertParameter(param, type));
    }
    return objects;
  }

}
