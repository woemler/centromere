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
public final class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);
    private static final List<String> FIELD_FILTER_PARAMETERS = Arrays.asList("fields", "exclude");
    private static final List<String> OTHER_PARAMETERS = Arrays.asList("format");
    private static final List<String> PAGINATION_PARAMETERS = Arrays.asList("page", "size", "sort");

    private RequestUtils() {
    }

    /**
     * Returns a list of the default query string options used by {@link }.
     *
     * @return
     */
    public static List<String> findAllParameters() {
        List<String> params = new ArrayList<>();
        params.addAll(FIELD_FILTER_PARAMETERS);
        params.addAll(PAGINATION_PARAMETERS);
        params.addAll(OTHER_PARAMETERS);
        return params;
    }

    public static List<String> findOneParameters() {
        List<String> params = new ArrayList<>();
        params.addAll(FIELD_FILTER_PARAMETERS);
        params.addAll(OTHER_PARAMETERS);
        return params;
    }

    public static List<String> findDistinctParameters() {
        List<String> params = new ArrayList<>();
        params.addAll(FIELD_FILTER_PARAMETERS);
        params.addAll(OTHER_PARAMETERS);
        return params;
    }

    /**
     * Converts query string options in a {@link HttpServletRequest} to a list of {@link QueryCriteria},
     *   based upon the available model query options and the default {@code GET} method options.
     *
     * @param model
     * @param request
     * @return
     */
    public static List<QueryCriteria> getQueryCriteriaFromFindRequest(
        Class<? extends Model<?>> model,
        HttpServletRequest request
    ) {

        LOGGER.info(String.format("Generating QueryCriteria for 'find' request options: model=%s params=%s",
            model.getName(), request.getQueryString()));

        List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(
            QueryParameterUtil.getAvailableQueryParameters(model),
            findAllParameters(),
            request.getParameterMap());

        LOGGER.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));

        return criteriaList;
    }

    /**
     * Converts query string options in a {@link HttpServletRequest} to a list of {@link QueryCriteria},
     *   based upon the available model query options and the distinct operation endpoint options.
     *
     * @param model
     * @param request
     * @return
     */
    public static List<QueryCriteria> getQueryCriteriaFromFindDistinctRequest(
        Class<? extends Model<?>> model,
        HttpServletRequest request
    ) {
        LOGGER.info(String.format("Generating QueryCriteria for 'findDistinct' request options: model=%s params=%s",
            model.getName(), request.getQueryString()));
        List<String> defaultParameters = findDistinctParameters();
        Map<String, QueryParameterDescriptor> paramMap = QueryParameterUtil.getAvailableQueryParameters(model);
        List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(paramMap, defaultParameters, request.getParameterMap());
        LOGGER.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));
        return criteriaList;
    }

    /**
     * Generates the {@link QueryCriteria} required to query a linked {@link Model} based upon it's 
     *   relationship to the parent resource.
     *
     * @param model
     * @param relField
     * @param relFieldValues
     * @param request
     * @return
     */
    public static List<QueryCriteria> getQueryCriteriaFromFindLinkedRequest(
        Class<? extends Model<?>> model,
        String relField,
        Collection<Object> relFieldValues,
        HttpServletRequest request
    ) {

        LOGGER.info(String.format("Generating QueryCriteria for 'find' request options: model=%s params=%s",
            model.getName(), request.getQueryString()));

        List<QueryCriteria> criteriaList = getQueryCriteriaFromRequest(
            QueryParameterUtil.getAvailableQueryParameters(model),
            findAllParameters(),
            request.getParameterMap());
        criteriaList.add(new QueryCriteria(relField, relFieldValues, Evaluation.IN));

        LOGGER.info(String.format("Generated QueryCriteria for request: %s", criteriaList.toString()));

        return criteriaList;
    }

    /**
     * Checks to see if the request contains invalid query string options.
     *
     * @param defaultParameters
     * @param requestParams
     * @return
     */
    public static boolean requestContainsNonDefaultParameters(Collection<String> defaultParameters,
        Map<String, String[]> requestParams) {
        for (String param: requestParams.keySet()) {
            if (!defaultParameters.contains(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts valid repository query options from a map of submitted request options, and
     *   generates a list of {@link QueryCriteria} for querying the database.
     *
     * @param validParams map of valid query options for the target {@link Model}
     * @param defaultParameters default query options for the given controller method
     * @param paramMap map of options in the HTTP request
     * @return list of query criteria
     */
    public static List<QueryCriteria> getQueryCriteriaFromRequest(
        Map<String, QueryParameterDescriptor> validParams,
        List<String> defaultParameters,
        Map<String, String[]> paramMap
    ) {

        List<QueryCriteria> criteriaList = new ArrayList<>();

        for (Map.Entry<String, String[]> entry: paramMap.entrySet()) {

            String paramName = entry.getKey();
            String[] paramValue = entry.getValue()[0] != null
                ? entry.getValue()[0].split(",") : new String[]{""};
            QueryCriteria criteria = null;

            if (defaultParameters.contains(paramName)) {
                continue;
            }

            for (Map.Entry<String, QueryParameterDescriptor> e: validParams.entrySet()) {

                QueryParameterDescriptor descriptor = e.getValue();

                if (descriptor.parameterNameMatches(paramName)) {

                    LOGGER.info(String.format("Request param '%s' matches model parameter: %s",
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

            if (criteria != null) {
                criteriaList.add(criteria);
            } else {
                LOGGER.warn(String.format("Unable to map request parameter to available model options: "
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
    public static Set<String> getFilteredFieldsFromRequest(HttpServletRequest request) {
        Set<String> fields = new HashSet<>();
        if (request.getParameterMap().containsKey("fields")) {
            fields = new HashSet<>();
            String[] params = request.getParameter("fields").split(",");
            for (String field: params) {
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
    public static Set<String> getExcludedFieldsFromRequest(HttpServletRequest request) {
        Set<String> exclude = new HashSet<>();
        if (request.getParameterMap().containsKey("exclude")) {
            exclude = new HashSet<>();
            String[] params = request.getParameter("exclude").split(",");
            for (String field: params) {
                exclude.add(field.trim());
            }
        }
        return exclude;
    }

}
