package com.blueprint.centromere.ws.controller;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
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
  private static final List<String> excludedParameters = Arrays
      .asList("fields", "exclude", "page", "size", "sort", "field");

  private static final String notLikePrefix = "!~";
  private static final String notEqualsPrefix = "!";
  private static final String likePrefix = "~";
  private static final String startsWithPrefix = "^";
  private static final String endsWithPrefix = "$";
  private static final String betweenPrefix = "<>";
  private static final String outsidePrefix = "><";
  private static final String greaterThanEqualsPrefix = ">>";
  private static final String greaterThanPrefix = ">";
  private static final String lessThanEqualsPrefix = "<<";
  private static final String lessThanPrefix = "<";

  private Evaluation getEvaluationFromValue(String value){
    if (value.startsWith(notLikePrefix)) return Evaluation.NOT_LIKE;
    else if (value.startsWith(notEqualsPrefix)) return Evaluation.NOT_EQUALS;
    else if (value.startsWith(likePrefix)) return Evaluation.LIKE;
    else if (value.startsWith(startsWithPrefix)) return Evaluation.STARTS_WITH;
    else if (value.startsWith(endsWithPrefix)) return Evaluation.ENDS_WITH;
    else if (value.startsWith(betweenPrefix)) return Evaluation.BETWEEN;
    else if (value.startsWith(outsidePrefix)) return Evaluation.OUTSIDE;
    else if (value.startsWith(greaterThanEqualsPrefix)) return Evaluation.GREATER_THAN_EQUALS;
    else if (value.startsWith(greaterThanPrefix)) return Evaluation.GREATER_THAN;
    else if (value.startsWith(lessThanEqualsPrefix)) return Evaluation.LESS_THAN_EQUALS;
    else if (value.startsWith(lessThanPrefix)) return Evaluation.LESS_THAN;
    else return Evaluation.EQUALS;
  }

  private String getEvaluationPrefix(Evaluation evaluation){
    if (evaluation.equals(Evaluation.NOT_LIKE)) return notLikePrefix;
    else if (evaluation.equals(Evaluation.NOT_EQUALS)) return notEqualsPrefix;
    else if (evaluation.equals(Evaluation.LIKE)) return likePrefix;
    else if (evaluation.equals(Evaluation.STARTS_WITH)) return startsWithPrefix;
    else if (evaluation.equals(Evaluation.ENDS_WITH)) return endsWithPrefix;
    else if (evaluation.equals(Evaluation.BETWEEN)) return betweenPrefix;
    else if (evaluation.equals(Evaluation.OUTSIDE)) return outsidePrefix;
    else if (evaluation.equals(Evaluation.GREATER_THAN_EQUALS)) return greaterThanEqualsPrefix;
    else if (evaluation.equals(Evaluation.GREATER_THAN)) return greaterThanPrefix;
    else if (evaluation.equals(Evaluation.LESS_THAN_EQUALS)) return lessThanEqualsPrefix;
    else if (evaluation.equals(Evaluation.LESS_THAN)) return lessThanPrefix;
    else return "";
  }

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
  public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(
      Class<?> model, 
      boolean recursive
  ){
    
    Map<String,QueryParameterDescriptor> paramMap = new HashMap<>();
    
    for (Field field: model.getDeclaredFields()){
      
      String fieldName = field.getName();
      Class<?> type = field.getType();
      
      if (Collection.class.isAssignableFrom(field.getType())){
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
      }
      
      if (field.isSynthetic() || field.isAnnotationPresent(Transient.class)) continue;
      if (field.isAnnotationPresent(RestResource.class)){
        RestResource restResource = field.getAnnotation(RestResource.class);
        if (!restResource.exported()) continue;
      }
      
      if (field.isAnnotationPresent(DBRef.class)){
        
        if (!recursive) continue;
        
        DBRef dbRef = field.getAnnotation(DBRef.class);
        String relField = fieldName;
        Class<?> relType = field.getType();
        if (Collection.class.isAssignableFrom(relType)){
          ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
          relType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        
        Map<String,QueryParameterDescriptor> foreignModelMap = getAvailableQueryParameters(relType, false);
        for(QueryParameterDescriptor descriptor: foreignModelMap.values()){
          String newParamName = relField + "." + descriptor.getParamName();
          descriptor.setParamName(newParamName);
          paramMap.put(newParamName, descriptor);
        }
        
      } else {
        paramMap.put(fieldName, new QueryParameterDescriptor(fieldName, fieldName, type, Evaluation.EQUALS));
      }
      
    }
    return paramMap;
  }

  public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(Class<?> model) {
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
