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

import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.config.ModelResourceRegistry;
import com.blueprint.centromere.ws.exception.InvalidParameterException;
import com.blueprint.centromere.ws.exception.ResourceNotFoundException;
import com.blueprint.centromere.ws.exception.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller methods for generic {@link Model} implementations and their search methods
 *
 * @author woemler
 * @since 0.5.0
 */
@Controller
@RequestMapping("${centromere.web.api.root-url}/aggregation")
@SuppressWarnings({"unchecked", "SpringJavaAutowiringInspection"})
public class ModelAggregationController {

  @Autowired private ModelResourceRegistry resourceRegistry;
  @Autowired private ModelRepositoryRegistry repositoryRegistry;
  @Autowired private ModelResourceAssembler assembler;
  @Autowired /*@Qualifier("defaultConversionService")*/ private ConversionService conversionService;
  @Autowired private ObjectMapper objectMapper;

  @Value("${centromere.web.api.root-url}")
  private String rootUrl;

  private static final Logger logger = LoggerFactory.getLogger(ModelAggregationController.class);

  /**
   * {@code GET /api/aggregation/{model}/distinct/{field}}
   * Fetches the distinct values of the model attribute, {@code field}, which fulfill the given
   *   query options.
   *
   * @param field Name of the model attribute to retrieve unique values of.
   * @param request {@link HttpServletRequest}
   * @return List of distinct field values.
   */
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Invalid options", response = RestError.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
  })
  @RequestMapping(
      value = "/{uri}/distinct/{field}",
      method = RequestMethod.GET,
      produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
          ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
          MediaType.TEXT_PLAIN_VALUE })
  public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> findDistinct(
      /*@ApiParam(name = "field", value = "Model field name.")*/ @PathVariable("field") String field,
      @PathVariable("uri") String uri,
      HttpServletRequest request)
  {

    Class<T> model;
    try {
      if (!resourceRegistry.isRegisteredResource(uri)) {
        logger.error(String.format("URI does not map to a registered model: %s", uri));
        throw new ResourceNotFoundException();
      }
      model = (Class<T>) resourceRegistry.getModelByUri(uri);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }

    BeanWrapper wrapper = new BeanWrapperImpl(model);
    if (!wrapper.isReadableProperty(field)){
      throw new InvalidParameterException(String.format("Requested field is not a valid model property: %s", field));
    }

    ModelRepository<T, ID> repository;
    try {
      repository = (ModelRepository<T, ID>) repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }

    List<QueryCriteria> queryCriterias = RequestUtils.getQueryCriteriaFromFindDistinctRequest(model, request);
    Set<Object> distinct = repository.distinct(field, queryCriterias);
    ResponseEnvelope<Object> envelope = null;
    
    if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
      
      Link selfLink = new Link(rootUrl + "/aggregation/" + uri + "/distinct/" + field +
          (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");
      Resources<Object> resources = new Resources<>(distinct);
      resources.add(selfLink);
      envelope = new ResponseEnvelope<>(resources);
      
    } else {
      
      envelope = new ResponseEnvelope<>(distinct);
      
    }
    
    return new ResponseEntity<>(envelope, HttpStatus.OK);
  }

  /**
   * {@code GET /api/aggregation/{model}/count}
   * Fetches the count of records for the requested model, which fulfill the given
   *   query options.
   *
   * @param request {@link HttpServletRequest}
   * @return The count of records that satisfy the query.
   */
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Invalid options", response = RestError.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
  })
  @RequestMapping(
      value = "/{uri}/count",
      method = RequestMethod.GET,
      produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
          ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
          MediaType.TEXT_PLAIN_VALUE })
  public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> count(
      @PathVariable("uri") String uri,
      HttpServletRequest request)
  {

    Class<T> model;
    
    try {
      if (!resourceRegistry.isRegisteredResource(uri)) {
        logger.error(String.format("URI does not map to a registered model: %s", uri));
        throw new ResourceNotFoundException();
      }
      model = (Class<T>) resourceRegistry.getModelByUri(uri);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }

    ModelRepository<T, ID> repository;
    try {
      repository = (ModelRepository<T, ID>) repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }

    List<QueryCriteria> queryCriterias = RequestUtils.getQueryCriteriaFromFindDistinctRequest(model, request);
    Long count = repository.count(queryCriterias);
    Map<String, Object> responseObject = Collections.singletonMap("count", count);
    ResponseEnvelope<Object> envelope;

    if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
      Link selfLink = new Link(rootUrl + "/aggregation/" + uri + "/count" +
          (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");
      Resource<Object> resource = new Resource<>(responseObject);
      resource.add(selfLink);
      envelope = new ResponseEnvelope<>(resource);
    } else {
      envelope = new ResponseEnvelope<>(responseObject);
    }

    return new ResponseEntity<>(envelope, HttpStatus.OK);
    
  }

  /**
   * {@code GET /api/aggregation/{model}/group/{field}}
   * Fetches a collection of records, grouped by the requested field.
   *
   * @param field Name of the model attribute to group records by.
   * @param request {@link HttpServletRequest}
   * @return List of grouped records.
   */
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Invalid options", response = RestError.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
  })
  @RequestMapping(
      value = "/{uri}/group/{field}",
      method = RequestMethod.GET,
      produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
          ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
          MediaType.TEXT_PLAIN_VALUE })
  public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> groupBy(
      @PathVariable("field") String field,
      @PathVariable("uri") String uri,
      HttpServletRequest request)
  {

    Class<T> model;
    try {
      if (!resourceRegistry.isRegisteredResource(uri)) {
        logger.error(String.format("URI does not map to a registered model: %s", uri));
        throw new ResourceNotFoundException();
      }
      model = (Class<T>) resourceRegistry.getModelByUri(uri);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }

    BeanWrapper wrapper = new BeanWrapperImpl(model);
    if (!wrapper.isReadableProperty(field)){
      throw new InvalidParameterException(String.format("Requested field is not a valid model property: %s", field));
    }

    ModelRepository<T, ID> repository;
    try {
      repository = (ModelRepository<T, ID>) repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }

    List<QueryCriteria> queryCriterias = RequestUtils.getQueryCriteriaFromFindDistinctRequest(model, request);
    Map<Object, List<T>> grouped = new LinkedHashMap<>();
    for (T record: repository.find(queryCriterias)){
      wrapper = new BeanWrapperImpl(record);
      Object fieldValue = wrapper.getPropertyValue(field);
      List<T> recordList = new ArrayList<>();
      if (grouped.containsKey(fieldValue)){
        recordList = grouped.get(fieldValue);
      }
      recordList.add(record);
      grouped.put(fieldValue, recordList);
    }
    
    ResponseEnvelope<Object> envelope;

    if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){

      Link selfLink = new Link(rootUrl + "/aggregation/" + uri + "/group/" + field +
          (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");
      Resource<Object> resource = new Resource<>(grouped);
      resource.add(selfLink);
      envelope = new ResponseEnvelope<>(resource);

    } else {

      envelope = new ResponseEnvelope<>(grouped);

    }

    return new ResponseEntity<>(envelope, HttpStatus.OK);
  }
  
}
