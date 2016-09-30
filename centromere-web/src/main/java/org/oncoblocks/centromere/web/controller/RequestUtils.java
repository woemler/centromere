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

package org.oncoblocks.centromere.web.controller;

import org.oncoblocks.centromere.core.model.Alias;
import org.oncoblocks.centromere.core.model.Aliases;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.QueryCriteria;
import org.oncoblocks.centromere.core.repository.QueryParameterDescriptor;
import org.oncoblocks.centromere.core.repository.QueryParameterException;
import org.oncoblocks.centromere.core.util.QueryParameterUtil;
import org.oncoblocks.centromere.web.exceptions.InvalidParameterException;
import org.oncoblocks.centromere.web.exceptions.ParameterMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Helper methods for processing API controller requests.
 * 
 * @author woemler
 */
public class RequestUtils {

	private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);
	private static final List<String> FIELD_FILTER_PARAMETERS = Arrays.asList("fields", "exclude");
	private static final List<String> DISTINCT_PARAMETERS = Arrays.asList("field");
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
		return params;
	}

	public static List<String> findOneParameters(){
		List<String> params = new ArrayList<>();
		params.addAll(FIELD_FILTER_PARAMETERS);
		return params;
	}

	public static List<String> findDistinctParameters(){
		List<String> params = new ArrayList<>();
		params.addAll(FIELD_FILTER_PARAMETERS);
		params.addAll(DISTINCT_PARAMETERS);
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
		List<String> defaultParameters = findAllParameters();
		Map<String, QueryParameterDescriptor> paramMap = QueryParameterUtil.getAvailableQueryParameters(model);
		List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(paramMap, defaultParameters, request);
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
		List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(paramMap, defaultParameters, request);
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
		List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(paramMap, defaultParameters, request);
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
	 * Extracts request parameters and matches them to available database query parameters, as defined
	 *   in the {@code model} class definition.
	 *
	 * @param request {@link HttpServletRequest}
	 * @return
	 */
	public static List<QueryCriteria> getQueryCriteriaFromRequest(
			Map<String, QueryParameterDescriptor> paramMap, 
			List<String> defaultParameters,
			HttpServletRequest request
	){
		List<QueryCriteria> criteriaList = new ArrayList<>();
		for (Map.Entry entry: request.getParameterMap().entrySet()){
			String paramName = (String) entry.getKey();
			String[] paramValue = ((String[]) entry.getValue())[0].split(",");
			if (!defaultParameters.contains(paramName)) {
				QueryCriteria criteria = null;
				for (Map.Entry e: paramMap.entrySet()){
					String p = (String) e.getKey();
					QueryParameterDescriptor descriptor = (QueryParameterDescriptor) e.getValue();
					try {
						if (descriptor.parameterNameMatches(paramName)) {
							criteria = QueryParameterUtil
									.getQueryCriteriaFromParameter(descriptor.getQueryableFieldName(paramName),
											paramValue, descriptor.getType(), descriptor.getDynamicEvaluation(p));
							break;
						}
					} catch (QueryParameterException ex){
						throw new ParameterMappingException(ex.getMessage());
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
		Set<String> fields = null;
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
		Set<String> exclude = null;
		if (request.getParameterMap().containsKey("exclude")){
			exclude = new HashSet<>();
			String[] params = request.getParameter("exclude").split(",");
			for (String field: params){
				exclude.add(field.trim());
			}
		}
		return exclude;
	}

	/**
	 * Uses annotated {@link Model} class definitions to remap any request attribute names in a 
	 *   {@link Pageable} so that they match repository attribute names.
	 *
	 * @param pageable {@link Pageable}
	 * @return
	 */
	public static Pageable remapPageable(Pageable pageable, Class<? extends Model<?>> model){
		logger.debug("Attempting to remap Pageable parameter names.");
		Sort sort = null;
		if (pageable.getSort() != null){
			List<Sort.Order> orders = new ArrayList<>();
			for (Sort.Order order: pageable.getSort()){
				orders.add(new Sort.Order(order.getDirection(), remapParameterName(order.getProperty(), model)));
			}
			sort = new Sort(orders);
		}
		return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
	}

	/**
	 * Checks a request parameter name against all possible {@link Model} attributes, converting it to
	 *   the appropriate repository field name for querying and sorting.
	 *
	 * @param param
	 * @return
	 */
	public static String remapParameterName(String param, Class<? extends Model<?>> model){
		logger.debug(String.format("Attempting to remap query string parameter: %s", param));
		for (Field field: model.getDeclaredFields()){
			String fieldName = field.getName();
			if (field.isAnnotationPresent(Aliases.class)){
				Aliases aliases = field.getAnnotation(Aliases.class);
				for (Alias alias: aliases.value()){
					if (alias.value().equals(param)) return fieldName;
				}
			} else if (field.isAnnotationPresent(Alias.class)){
				Alias alias = field.getAnnotation(Alias.class);
				if (alias.value().equals(param)) return fieldName;
			}
		}
		logger.debug(String.format("Parameter remapped to: %s", param));
		return param;
	}
	
}
