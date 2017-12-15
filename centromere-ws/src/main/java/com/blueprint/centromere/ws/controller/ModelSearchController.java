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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import com.blueprint.centromere.core.commons.model.Data;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.repository.MetadataOperations;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.exception.InvalidParameterException;
import com.blueprint.centromere.ws.exception.ResourceNotFoundException;
import com.blueprint.centromere.ws.exception.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller methods for generic {@link Model} implementations and their search methods
 *
 * @author woemler
 * @since 0.5.0
 */
@Controller
@RequestMapping("${centromere.web.api.root-url}")
@SuppressWarnings({"unchecked", "SpringJavaAutowiringInspection"})
public class ModelSearchController {

  @Autowired private ModelResourceRegistry resourceRegistry;
  @Autowired private ModelRepositoryRegistry repositoryRegistry;
  @Autowired private ModelResourceAssembler assembler;
  @Autowired /*@Qualifier("defaultConversionService")*/ private ConversionService conversionService;
  @Autowired private ObjectMapper objectMapper;

  @Value("${centromere.web.api.root-url}")
  private String rootUrl;

  private static final Logger logger = LoggerFactory.getLogger(ModelSearchController.class);

  /**
   * {@code GET /{model}/search/distinct}
   * Fetches the distinct values of the model attribute, {@code field}, which fulfill the given
   *   query parameters.
   *
   * @param field Name of the model attribute to retrieve unique values of.
   * @param request {@link HttpServletRequest}
   * @return List of distinct field values.
   */
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Invalid parameters", response = RestError.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
  })
  @RequestMapping(
      value = "/{uri}/search/distinct/{field}",
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
      
      Link selfLink = new Link(linkTo(this.getClass()).slash("search").slash("distinct").toString() +
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
   * {@code GET /{model}/search/guess}
   * Fetches the distinct values of the model attribute, {@code field}, which fulfill the given
   *   query parameters.
   *
   * @param keyword Keyword term to use to search for records.
   * @param request {@link HttpServletRequest}
   * @return List of distinct field values.
   */
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Invalid parameters", response = RestError.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
  })
  @RequestMapping(
      value = "/{uri}/search/guess",
      method = RequestMethod.GET,
      produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
          ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
          MediaType.TEXT_PLAIN_VALUE })
  public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> guess(
      @ApiParam(name = "keyword", value = "Keyword term.") @RequestParam String keyword,
      @PathVariable String uri,
      HttpServletRequest request)
  {

    Class<T> model;
    ModelRepository<T, ID> repository;
    try {
      if (!resourceRegistry.isRegisteredResource(uri)){
        logger.error(String.format("URI does not map to a registered model: %s", uri));
        throw new ResourceNotFoundException();
      }
      model = (Class<T>) resourceRegistry.getModelByUri(uri);
      repository = (ModelRepository<T, ID>) repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }
    
    if (!(repository instanceof MetadataOperations)){
      throw new ResourceNotFoundException();
    }
    
    MetadataOperations<T> metadataOperations = (MetadataOperations<T>) repository;
    List<T> entities = metadataOperations.guess(keyword);
    ResponseEnvelope<Object> envelope = null;

    Set<String> fields = RequestUtils.getFilteredFieldsFromRequest(request);
    Set<String> exclude = RequestUtils.getExcludedFieldsFromRequest(request);
    if (!fields.isEmpty()) logger.info(String.format("Selected fields: %s", fields.toString()));
    if (!exclude.isEmpty()) logger.info(String.format("Excluded fields: %s", exclude.toString()));

    if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
      Link selfLink = new Link(linkTo(this.getClass()).slash("search").slash("guess").toString() +
          (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");
      List<FilterableResource> resourceList = assembler.toResources(entities);
      Resources<FilterableResource> resources = new Resources<>(resourceList);
      resources.add(selfLink);
      envelope = new ResponseEnvelope<>(resources, fields, exclude);
    } else {
      envelope = new ResponseEnvelope<>(entities, fields, exclude);
    }
    
    return new ResponseEntity<>(envelope, HttpStatus.OK);
  }

  /**
   * {@code GET /{uri}/search/{linked}}
   * Fetches all data records associated with the queried {@link com.blueprint.centromere.core.commons.model.Sample}
   *   record.
   *
   * @param pagedResourcesAssembler {@link PagedResourcesAssembler}
   * @param request {@link HttpServletRequest}
   * @return a {@link List} of {@link Model} objects.
   */
  @ApiImplicitParams({
      @ApiImplicitParam(name = "page", value = "Page number.", defaultValue = "0", dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "size", value = "Number of records per page.", defaultValue = "1000",
          dataType = "int", paramType = "query"),
      @ApiImplicitParam(name = "sort", value = "Sort order field and direction.", dataType = "string",
          paramType = "query", example = "name,asc"),
      @ApiImplicitParam(name = "fields", value = "List of fields to be included in response objects",
          dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "exclude", value = "List of fields to be excluded from response objects",
          dataType = "string", paramType = "query")
  })
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 400, message = "Invalid parameters", response = RestError.class),
      @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
      @ApiResponse(code = 404, message = "Record not found.", response = RestError.class)
  })
  @RequestMapping(
      value = "/{uri}/search/{meta}",
      method = RequestMethod.GET,
      produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
          ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
          MediaType.TEXT_PLAIN_VALUE})
  public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<T>> findByLinkedMetadata(
      @PageableDefault(size = 1000) Pageable pageable,
      @PathVariable("uri") String uri,
      @PathVariable("meta") String meta,
      PagedResourcesAssembler pagedResourcesAssembler,
      HttpServletRequest request
  ) {

    Class<T> model;
    ModelRepository<T, ID> repository;
    try {
      if (!resourceRegistry.isRegisteredResource(uri)){
        logger.error(String.format("URI does not map to a registered model: %s", uri));
        throw new ResourceNotFoundException();
      }
      model = (Class<T>) resourceRegistry.getModelByUri(uri);
      if (!Data.class.isAssignableFrom(model)){
        logger.error(String.format("URI does not map to a valid model: %s", uri));
        throw new ResourceNotFoundException();
      }
      repository = (ModelRepository<T, ID>) repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }
    logger.info(String.format("Resolved request to model %s and repository %s",
        model.getName(), repository.getClass().getName()));

    Set<String> fields = RequestUtils.getFilteredFieldsFromRequest(request);
    Set<String> exclude = RequestUtils.getExcludedFieldsFromRequest(request);
    if (!fields.isEmpty()) logger.info(String.format("Selected fields: %s", fields.toString()));
    if (!exclude.isEmpty()) logger.info(String.format("Excluded fields: %s", exclude.toString()));

    ResponseEnvelope<T> envelope;
    Map<String,String[]> parameterMap = request.getParameterMap();
    String mediaType = request.getHeader("Accept");

    Class<? extends Model<?>> metaModel;
    ModelRepository<?,?> metaRepository;
    String metaField;
    
    try {
      metaModel = resourceRegistry.getModelByUri(meta);
      metaRepository = repositoryRegistry.getRepositoryByModel(metaModel);
    } catch (ModelRegistryException e){
      e.printStackTrace();
      throw new ResourceNotFoundException();
    }
    if (metaModel == null){
      logger.error(String.format("URI does not map to a linked metadata model: %s", meta));
      throw new ResourceNotFoundException();
    }
    
    //TODO: more programatic way of assigning metaField from model
    if (Sample.class.isAssignableFrom(metaModel)){
      metaField = "sampleId";
    } else if (Gene.class.isAssignableFrom(metaModel)){
      metaField = "geneId";
    } else if (DataFile.class.isAssignableFrom(metaModel)){
      metaField = "dataFileId";
    } else if (DataSet.class.isAssignableFrom(metaModel)){
      metaField = "dataSetId";
    } else {
      logger.error(String.format("URI does not map to a linked metadata model: %s", meta));
      throw new ResourceNotFoundException();
    }

    logger.info(String.format("Resolved linked metadata model to %s and repository to %s", 
        metaModel.getName(), metaRepository.getClass().getName()));

    List<QueryCriteria> metaCriteria = RequestUtils.getQueryCriteriaFromFindRequest(metaModel , request);
    List<String> metaIds = new ArrayList<>();
    for (Model m: metaRepository.find(metaCriteria)){
      metaIds.add((String) m.getId());
    }
    List<QueryCriteria> criterias
        = Collections.singletonList(new QueryCriteria(metaField, metaIds, Evaluation.IN));

    Link selfLink = new Link(rootUrl + "/" + uri + "/sample" +
        (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");

    if (parameterMap.containsKey("page") || parameterMap.containsKey("size")){

      Page<T> page = repository.find(criterias, pageable);

      if (ApiMediaTypes.isHalMediaType(mediaType)){

        PagedResources<FilterableResource> pagedResources
            = pagedResourcesAssembler.toResource(page, assembler, selfLink);
        envelope = new ResponseEnvelope<>(pagedResources, fields, exclude);

      } else {

        envelope = new ResponseEnvelope<>(page, fields, exclude);

      }

    } else {

      Sort sort = pageable.getSort();
      List<T> entities;

      if (sort != null){
        entities = (List<T>) repository.find(criterias, sort);
      } else {
        entities = (List<T>) repository.find(criterias);
      }

      if (ApiMediaTypes.isHalMediaType(mediaType)){
        List<FilterableResource> resourceList = assembler.toResources(entities);
        Resources<FilterableResource> resources = new Resources<>(resourceList);
        resources.add(selfLink);
        envelope = new ResponseEnvelope<>(resources, fields, exclude);
      } else {
        envelope = new ResponseEnvelope<>(entities, fields, exclude);
      }

    }

    return new ResponseEntity<>(envelope, HttpStatus.OK);

  }
  
}
