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

package com.blueprint.centromere.ws.controller;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.repository.QueryParameterUtil;
import com.blueprint.centromere.ws.exception.InvalidParameterException;
import com.blueprint.centromere.ws.exception.ParameterMappingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public class RequestUtils {

  private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);
  private static final List<String> FIELD_FILTER_PARAMETERS = Arrays.asList("fields", "exclude");
  private static final List<String> OTHER_PARAMETERS = Arrays.asList("format");
  private static final List<String> PAGINATION_PARAMETERS = Arrays.asList("page", "size", "sort");

  /**
   * Returns a list of the default query string parameters used by {@link }.
   *
   * @return
   */
  public static List<String> findAllParameters(){
    List<String> params = new ArrayList<>();
    params.addAll(FIELD_FILTER_PARAMETERS);
    params.addAll(PAGINATION_PARAMETERS);
    params.addAll(OTHER_PARAMETERS);
    return params;
  }

  public static List<String> findOneParameters(){
    List<String> params = new ArrayList<>();
    params.addAll(FIELD_FILTER_PARAMETERS);
    params.addAll(OTHER_PARAMETERS);
    return params;
  }

  public static List<String> findDistinctParameters(){
    List<String> params = new ArrayList<>();
    params.addAll(FIELD_FILTER_PARAMETERS);
    params.addAll(OTHER_PARAMETERS);
    return params;
  }

  /**
   * Converts query string parameters in a {@link HttpServletRequest} to a list of {@link QueryCriteria},
   *   based upon the available model query parameters and the default {@code GET} method parameters.
   *
   * @param model
   * @param request
   * @return
   */
  public static List<QueryCriteria> getQueryCriteriaFromFindRequest(Class<? extends Model<?>> model,
      HttpServletRequest request
  ){
    
    logger.info(String.format("Generating QueryCriteria for 'find' request parameters: model=%s params=%s",
        model.getName(), request.getQueryString()));
    
    List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(
        QueryParameterUtil.getAvailableQueryParameters(model), 
        findAllParameters(), 
        request.getParameterMap());
    
    logger.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));
    
    return criteriaList;
  }

  public static List<QueryCriteria> getQueryCriteriaFromFindOneRequest(Class<? extends Model<?>> model,
      HttpServletRequest request
  ){
    logger.info(String.format("Generating QueryCriteria for 'findOne' request parameters: model=%s params=%s",
        model.getName(), request.getQueryString()));
    List<String> defaultParameters = findOneParameters();
    Map<String, QueryParameterDescriptor> paramMap = QueryParameterUtil.getAvailableQueryParameters(model);
    List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(paramMap, defaultParameters, request.getParameterMap());
    logger.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));
    return criteriaList;
  }

  public static List<QueryCriteria> getQueryCriteriaFromFindDistinctRequest(Class<? extends Model<?>> model,
      HttpServletRequest request
  ){
    logger.info(String.format("Generating QueryCriteria for 'findDistinct' request parameters: model=%s params=%s",
        model.getName(), request.getQueryString()));
    List<String> defaultParameters = findDistinctParameters();
    Map<String, QueryParameterDescriptor> paramMap = QueryParameterUtil.getAvailableQueryParameters(model);
    List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(paramMap, defaultParameters, request.getParameterMap());
    logger.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));
    return criteriaList;
  }

  public static List<QueryCriteria> getQueryCriteriaFromFindLinkedRequest(
      Class<? extends Model<?>> model,
      String relField,
      Collection<Object> relFieldValues,
      HttpServletRequest request
  ){

    logger.info(String.format("Generating QueryCriteria for 'find' request parameters: model=%s params=%s",
        model.getName(), request.getQueryString()));

    List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(
        QueryParameterUtil.getAvailableQueryParameters(model),
        findAllParameters(),
        request.getParameterMap());
    criteriaList.add(new QueryCriteria(relField, relFieldValues, Evaluation.IN));

    logger.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));

    return criteriaList;
  }

  public static boolean requestContainsNonDefaultParameters(Collection<String> defaultParameters,
      Map<String, String[]> requestParams){
    for (String param: requestParams.keySet()){
      if (!defaultParameters.contains(param)){
        return true;
      }
    }
    return false;
  }

  /**
   * Extracts valid repository query parameters from a map of submitted request parameters, and
   *   generates a list of {@link QueryCriteria} for querying the database.
   *
   * @param validParams map of valid query parameters for the target {@link Model}
   * @param defaultParameters default query parameters for the given controller method
   * @param paramMap map of parameters in the HTTP request
   * @return list of query criteria
   */
  public static List<QueryCriteria> getQueryCriteriaFromRequest(
      Map<String, QueryParameterDescriptor> validParams,
      List<String> defaultParameters,
      Map<String, String[]> paramMap
  ){
    
    List<QueryCriteria> criteriaList = new ArrayList<>();
    
    for (Map.Entry<String, String[]> entry: paramMap.entrySet()){
      
      String paramName = entry.getKey();
      String[] paramValue = entry.getValue()[0] != null
          ? entry.getValue()[0].split(",") : new String[]{""};
      QueryCriteria criteria = null;
          
      if (defaultParameters.contains(paramName)) continue;
      
      for (Map.Entry<String, QueryParameterDescriptor> e: validParams.entrySet()){
        
        QueryParameterDescriptor descriptor = e.getValue();

        if (descriptor.parameterNameMatches(paramName)) {

          logger.info(String.format("Request param '%s' matches model parameter: %s",
              paramName, descriptor.toString()));
          
          try {
            criteria = QueryParameterUtil.getQueryCriteriaFromParameter(
                descriptor.getQueryableFieldName(paramName),
                paramValue,
                descriptor.getType(),
                descriptor.getDynamicEvaluation(paramName));
            break;
          } catch (Exception ex) {
            throw new ParameterMappingException(ex.getMessage());
          }

        }
        
      }
      
      if (criteria != null){
        
        criteriaList.add(criteria);
        
      } else {
        
        logger.warn(String.format("Unable to map request parameter to available model parameters: "
            + "%s", paramName));
        throw new InvalidParameterException("Invalid request parameter: " + paramName);
        
      }
      
    }
    
    return criteriaList;
    
  }



  /**
   * Extracts the requested filtered fields parameter from a request.
   *
   * @param request
   * @return
   */
  public static Set<String> getFilteredFieldsFromRequest(HttpServletRequest request){
    Set<String> fields = new HashSet<>();
    if (request.getParameterMap().containsKey("fields")){
      fields = new HashSet<>();
      String[] params = request.getParameter("fields").split(",");
      for (String field: params){
        fields.add(field.trim());
      }
    }
    return fields;
  }

  /**
   * Extracts the requested filtered fields parameter from a request.
   *
   * @param request
   * @return
   */
  public static Set<String> getExcludedFieldsFromRequest(HttpServletRequest request){
    Set<String> exclude = new HashSet<>();
    if (request.getParameterMap().containsKey("exclude")){
      exclude = new HashSet<>();
      String[] params = request.getParameter("exclude").split(",");
      for (String field: params){
        exclude.add(field.trim());
      }
    }
    return exclude;
  }

//  /**
//   * Uses annotated {@link Model} class definitions to remap any request attribute names in a
//   *   {@link Pageable} so that they match repository attribute names.
//   *
//   * @param pageable {@link Pageable}
//   * @return
//   */
//  public static Pageable remapPageable(Pageable pageable, Class<? extends Model<?>> model){
//    logger.debug("Attempting to remap Pageable parameter names.");
//    Sort sort = null;
//    if (pageable.getSort() != null){
//      List<Sort.Order> orders = new ArrayList<>();
//      for (Sort.Order order: pageable.getSort()){
//        orders.add(new Sort.Order(order.getDirection(), order.getProperty()));
//      }
//      sort = new Sort(orders);
//    }
//    return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
//  }

}
