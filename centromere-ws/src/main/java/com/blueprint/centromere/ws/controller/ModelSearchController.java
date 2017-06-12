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

import com.blueprint.centromere.core.commons.repository.MetadataOperations;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.exception.MalformedEntityException;
import com.blueprint.centromere.ws.exception.ResourceNotFoundException;
import com.blueprint.centromere.ws.exception.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
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
@RequestMapping("${centromere.api.root-url}")
@SuppressWarnings({"unchecked", "SpringJavaAutowiringInspection"})
public class ModelSearchController {

  @Autowired private ModelRepositoryRegistry registry;
  @Autowired private ModelResourceAssembler assembler;
  @Autowired @Qualifier("defaultConversionService") private ConversionService conversionService;
  @Autowired private ObjectMapper objectMapper;

  @Value("${centromere.api.root-url}")
  private String rootUrl;

  private static final Logger logger = LoggerFactory.getLogger(ModelSearchController.class);

  /**
   * {@code GET /search/distinct}
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
      value = "/{uri}/search/distinct",
      method = RequestMethod.GET,
      produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
          ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
          MediaType.TEXT_PLAIN_VALUE })
  public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> findDistinct(
      @ApiParam(name = "field", value = "Model field name.") @RequestParam String field,
      @PathVariable String uri,
      HttpServletRequest request)
  {
    
    if (!registry.isRegisteredResource(uri)){
      logger.error(String.format("URI does not map to a registered model: %s", uri));
      throw new ResourceNotFoundException();
    }
    
    Class<T> model = (Class<T>) registry.getModelByResource(uri);
    ModelRepository<T, ID> repository = (ModelRepository<T, ID>) registry.getRepositoryByModel(model);
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
   * {@code GET /search/guess}
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
    
    if (!registry.isRegisteredResource(uri)){
      logger.error(String.format("URI does not map to a registered model: %s", uri));
      throw new ResourceNotFoundException();
    }
    
    Class<T> model = (Class<T>) registry.getModelByResource(uri);
    ModelRepository<T, ID> repository = (ModelRepository<T, ID>) registry.getRepositoryByModel(model);
    
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
   * Converts a String query parameter to the appropriate model ID type.
   *
   * @param param String value of ID parameter.
   * @param model Model type to interrogate to determine ID type.
   * @return converted ID object.
   */
  protected <T extends Model<ID>, ID extends Serializable> ID convertModelIdParameter(String param, Class<T> model){
    try {
      Class<ID> type = (Class<ID>) model.getMethod("getId").getReturnType();
      if (conversionService.canConvert(String.class, type)){
        return conversionService.convert(param, type);
      }
    } catch (Exception e){
      e.printStackTrace();
    }
    throw new MalformedEntityException(String.format("Cannot convert ID parameter to model ID type: %s", param));
  }

  /**
   * Attempts to convert a generic object, supplied in a HTTP request, to the target type.
   *
   * @param object object to be converted.
   * @param type class the object should be converted to.
   * @param <T>  generic type of the target class.
   * @return converted object.
   */
  protected <T> T convertObjectToModel(Object object, Class<T> type){
//    objectMapper.setSerializationInclusion(Include.ALWAYS);
    try {
      return objectMapper.convertValue(object, type);
    } catch (Exception e){
      throw new MalformedEntityException(String.format("Cannot convert object to model type %s: %s",
          type.getName(), object.toString()));
    }
  }

  public ResourceAssemblerSupport<Model, FilterableResource> getAssembler() {
    return assembler;
  }

}
