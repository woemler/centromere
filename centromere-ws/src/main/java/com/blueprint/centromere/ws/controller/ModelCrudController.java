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
import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelReflectionUtils;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.config.ModelResourceRegistry;
import com.blueprint.centromere.ws.exception.InvalidParameterException;
import com.blueprint.centromere.ws.exception.MalformedEntityException;
import com.blueprint.centromere.ws.exception.ModelDefinitionException;
import com.blueprint.centromere.ws.exception.RequestFailureException;
import com.blueprint.centromere.ws.exception.ResourceNotFoundException;
import com.blueprint.centromere.ws.exception.RestError;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base model resource controller for standard CRUD operations and queries.
 *
 * @author woemler
 */
@Controller
@RequestMapping("${centromere.web.api.root-url}/search")
@SuppressWarnings({"unchecked", "SpringJavaAutowiringInspection"})
public class ModelCrudController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCrudController.class);

    @Autowired
    private ModelRepositoryRegistry repositoryRegistry;

    @Autowired
    private ModelResourceRegistry resourceRegistry;

    @Autowired
    private ModelResourceAssembler assembler;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${centromere.web.api.root-url}")
    private String rootUrl;

    /**
     * {@code GET /{id}}
     * Fetches a single record by its primary I and returns it, or a {@code Not Found} exception if not.
     *
     * @param id primary I for the target record.
     * @return {@code T} instance
     */
    @ApiImplicitParams({
        @ApiImplicitParam(name = ReservedRequestParameters.INCLUDED_FIELDS_PARAMETER, 
            value = "List of fields to be included in response objects",
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.EXCLUDED_FIELDS_PARAMETER, 
            value = "List of fields to be excluded from response objects",
            dataType = "string", paramType = "query")
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Invalid options", response = RestError.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
        @ApiResponse(code = 404, message = "Record not found.", response = RestError.class)
    })
    @RequestMapping(
        value = "/{uri}/{id}",
        method = RequestMethod.GET,
        produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
            ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    public <T extends Model<I>, I extends Serializable> ResponseEntity<ResponseEnvelope> findById(
        @ApiParam(name = "id", value = "Model record primary id.") @PathVariable String id,
        @PathVariable String uri,
        HttpServletRequest request
    ) {
        ModelRepository<T, I> repository;
        Class<T> model;
        try {
            if (!resourceRegistry.isRegisteredResource(uri)) {
                LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
                throw new ResourceNotFoundException();
            }
            model = (Class<T>) resourceRegistry.getModelByUri(uri);
            repository = (ModelRepository<T, I>) repositoryRegistry.getRepositoryByModel(model);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        if (RequestUtils.requestContainsNonDefaultParameters(RequestUtils.findOneParameters(), request.getParameterMap())) {
            throw new InvalidParameterException("Request contains invalid query string options.");
        }
        Set<String> includedFields = RequestUtils.getIncludedFieldsFromRequest(request);
        Set<String> excludedFields = RequestUtils.getExcludedFieldsFromRequest(request);
        Optional<T> optional = repository.findById(convertModelIdParameter(id, model));
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException();
        }
        ResponseEnvelope envelope;
        T entity = optional.get();
        if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))) {
            FilterableResource resource = assembler.toResource(entity);
            envelope = new ResponseEnvelope(resource, includedFields, excludedFields);
        } else {
            envelope = new ResponseEnvelope(entity, includedFields, excludedFields);
        }
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    /**
     * {@code GET /}
     * Queries the repository using inputted query string paramters, defined within a annotated
     *   {@link Model} classes.  Supports hypermedia, pagination, sorting, field
     *   filtering, and field exclusion.
     *
     * @param pagedResourcesAssembler {@link PagedResourcesAssembler}
     * @param request {@link HttpServletRequest}
     * @return a {@link List} of {@link Model} objects.
     */
    @ApiImplicitParams({
        @ApiImplicitParam(name = ReservedRequestParameters.PAGE_PARAMETER, 
            value = "Page number.", 
            defaultValue = "0", 
            dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.SIZE_PARAMETER, 
            value = "Number of records per page.", 
            defaultValue = "1000",
            dataType = "int", 
            paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.SORT_PARAMETER, 
            value = "Sort order field and direction.", 
            dataType = "string",
            paramType = "query", 
            example = "name,asc"),
        @ApiImplicitParam(name = ReservedRequestParameters.INCLUDED_FIELDS_PARAMETER, 
            value = "List of fields to be included in response objects",
            dataType = "string", 
            paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.EXCLUDED_FIELDS_PARAMETER, 
            value = "List of fields to be excluded from response objects",
            dataType = "string", 
            paramType = "query")
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Invalid options", response = RestError.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
        @ApiResponse(code = 404, message = "Record not found.", response = RestError.class)
    })
    @RequestMapping(
        value = "/{uri}",
        method = RequestMethod.GET,
        produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
            ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
    public <T extends Model<I>, I extends Serializable> ResponseEntity<ResponseEnvelope> find(
        @PathVariable String uri,
        Pageable pageable,
        PagedResourcesAssembler pagedResourcesAssembler,
        HttpServletRequest request
    ) {
        Class<T> model;
        ModelRepository<T, I> repository;
        try {
            if (!resourceRegistry.isRegisteredResource(uri)) {
                LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
                throw new ResourceNotFoundException();
            }
            model = (Class<T>) resourceRegistry.getModelByUri(uri);
            repository = (ModelRepository<T, I>) repositoryRegistry.getRepositoryByModel(model);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        LOGGER.info(String.format("Resolved request to model %s and repository %s",
            model.getName(), repository.getClass().getName()));

        Set<String> includedFields = RequestUtils.getIncludedFieldsFromRequest(request);
        Set<String> excludedFields = RequestUtils.getExcludedFieldsFromRequest(request);
        if (!includedFields.isEmpty()) {
            LOGGER.info(String.format("Selected includedFields: %s", includedFields.toString()));
        }
        if (!excludedFields.isEmpty()) {
            LOGGER.info(String.format("Excluded fields: %s", excludedFields.toString()));
        }

        ResponseEnvelope envelope;
        String mediaType = request.getHeader("Accept");

        List<QueryCriteria> criterias = RequestUtils.getQueryCriteriaFromFindRequest(model, request);

        Link selfLink = new Link(rootUrl + "/search/" + uri +
            (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");
        
        if (RequestUtils.isPagedRequest(request)) {

            Page<T> page = repository.find(criterias, pageable);
            LOGGER.info(String.format("Query returned %d paged records, out of %d total", page.getSize(), page.getTotalElements()));

            if (ApiMediaTypes.isHalMediaType(mediaType)) {

                PagedResources<FilterableResource> pagedResources
                    = pagedResourcesAssembler.toResource(page, assembler, selfLink);
                envelope = new ResponseEnvelope(pagedResources, includedFields, excludedFields);

            } else {

                envelope = new ResponseEnvelope(page, includedFields, excludedFields);

            }

        } else {
            
            List<T> entities;

            if (RequestUtils.isSortableRequest(request)) {
                entities = (List<T>) repository.find(criterias, pageable.getSort());
            } else {
                entities = (List<T>) repository.find(criterias);
            }
            LOGGER.info(String.format("Query returned %d records", entities.size()));

            if (ApiMediaTypes.isHalMediaType(mediaType)) {
                List<FilterableResource> resourceList = assembler.toResources(entities);
                Resources<FilterableResource> resources = new Resources<>(resourceList);
                resources.add(selfLink);
                envelope = new ResponseEnvelope(resources, includedFields, excludedFields);
            } else {
                envelope = new ResponseEnvelope(entities, includedFields, excludedFields);
            }

        }

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    /**
     * {@code POST /}
     * Attempts to create a new record using the submitted entity. Throws an exception if the
     *   entity already exists.
     *
     * @param entity entity representation to be persisted
     * @return updated representation of the submitted entity
     */
    @ApiResponses({
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
        @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class),
        @ApiResponse(code = 406, message = "Malformed entity", response = RestError.class)
    })
    @RequestMapping(
        value = "/{uri}",
        method = RequestMethod.POST,
        produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
            ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
    public <T extends Model<I>, I extends Serializable> ResponseEntity<?> create(
        @ApiParam(name = "entity", value = "Model record entity.") @RequestBody Object entity,
        @PathVariable String uri,
        HttpServletRequest request
    ) {

        Class<T> model;
        ModelRepository<T, I> repository;
        Object body;
        try {
            if (!resourceRegistry.isRegisteredResource(uri)) {
                LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
                throw new ResourceNotFoundException();
            }
            model = (Class<T>) resourceRegistry.getModelByUri(uri);
            body = convertObjectToModel(entity, model);
            repository = (ModelRepository<T, I>) repositoryRegistry.getRepositoryByModel(model);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        LOGGER.info(String.format("Resolved request to model %s and repository %s",
            model.getName(), repository.getClass().getName()));

        T created = repository.insert((T) body);

        if (created == null) {
            throw new RequestFailureException("There was a problem creating the record.");
        }

        ResponseEnvelope envelope;

        if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))) {
            FilterableResource resource = getAssembler().toResource(created);
            envelope = new ResponseEnvelope(resource);
        } else {
            envelope = new ResponseEnvelope(body);
        }

        return new ResponseEntity<>(envelope, HttpStatus.CREATED);

    }

    /**
     * {@code PUT /{id}}
     * Attempts to update an existing entity record, replacing it with the submitted entity. Throws
     *   an exception if the target entity does not exist.
     *
     * @param entity entity representation to update.
     * @param id primary I of the target entity
     * @return updated representation of the submitted entity.
     */
    @ApiResponses({
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
        @ApiResponse(code = 404, message = "Record not found", response = RestError.class),
        @ApiResponse(code = 406, message = "Malformed entity", response = RestError.class)
    })
    @RequestMapping(
        value = "/{uri}/{id}",
        method = RequestMethod.PUT,
        produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
            ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
    public <T extends Model<I>, I extends Serializable> ResponseEntity<?> update(
        @ApiParam(name = "entity", value = "Model record entity.") @RequestBody Object entity,
        @ApiParam(name = "id", value = "Model record primary id.") @PathVariable String id,
        @PathVariable String uri,
        HttpServletRequest request
    ) {

        Class<T> model;
        ModelRepository<T, I> repository;
        T record;
        try {
            if (!resourceRegistry.isRegisteredResource(uri)) {
                LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
                throw new ResourceNotFoundException();
            }
            model = (Class<T>) resourceRegistry.getModelByUri(uri);
            record = convertObjectToModel(entity, model);
            LOGGER.info(String.format("Converted object: %s", record.toString()));
            repository = (ModelRepository<T, I>) repositoryRegistry.getRepositoryByModel(model);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        LOGGER.info(String.format("Resolved request to model %s and repository %s",
            model.getName(), repository.getClass().getName()));

        if (!repository.existsById(record.getId())) {
            throw new ResourceNotFoundException();
        }

        T updated = repository.update(record);

        if (updated == null) {
            throw new RequestFailureException("There was a problem updating the record.");
        }

        ResponseEnvelope envelope;

        if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))) {
            FilterableResource resource = getAssembler().toResource(updated);
            envelope = new ResponseEnvelope(resource);
        } else {
            envelope = new ResponseEnvelope(entity);
        }

        return new ResponseEntity<>(envelope, HttpStatus.CREATED);

    }

    /**
     * {@code DELETE /{id}}
     * Attempts to delete the an entity identified by the submitted primary I.
     *
     * @param id primary I of the target record.
     * @return {@link HttpStatus} indicating success or failure.
     */
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
        @ApiResponse(code = 404, message = "Record not found", response = RestError.class)
    })
    @RequestMapping(
        value = "/{uri}/{id}",
        method = RequestMethod.DELETE)
    public <T extends Model<I>, I extends Serializable> ResponseEntity<?> delete(
        @ApiParam(name = "id", value = "Model record primary id.") @PathVariable String id,
        @PathVariable String uri,
        HttpServletRequest request) {

        Class<T> model;
        ModelRepository<T, I> repository;
        try {
            if (!resourceRegistry.isRegisteredResource(uri)) {
                LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
                throw new ResourceNotFoundException();
            }
            model = (Class<T>) resourceRegistry.getModelByUri(uri);
            repository = (ModelRepository<T, I>) repositoryRegistry.getRepositoryByModel(model);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        repository.deleteById(convertModelIdParameter(id, model));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * {@code GET /{uri}/{id}/{linked}}
     * Fetches all data records associated with the queried {@link Model} record.
     *
     * @param pagedResourcesAssembler {@link PagedResourcesAssembler}
     * @param request {@link HttpServletRequest}
     * @return a {@link List} of {@link Model} objects.
     */
    @ApiImplicitParams({
        @ApiImplicitParam(name = ReservedRequestParameters.PAGE_PARAMETER,
            value = "Page number.",
            defaultValue = "0",
            dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.SIZE_PARAMETER,
            value = "Number of records per page.",
            defaultValue = "1000",
            dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.SORT_PARAMETER,
            value = "Sort order field and direction.",
            dataType = "string",
            paramType = "query",
            example = "name,asc"),
        @ApiImplicitParam(name = ReservedRequestParameters.INCLUDED_FIELDS_PARAMETER,
            value = "List of fields to be included in response objects",
            dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = ReservedRequestParameters.EXCLUDED_FIELDS_PARAMETER,
            value = "List of fields to be excluded from response objects",
            dataType = "string",
            paramType = "query")
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Invalid options", response = RestError.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
        @ApiResponse(code = 404, message = "Record not found.", response = RestError.class)
    })
    @RequestMapping(
        value = "/{uri}/{id}/{meta}",
        method = RequestMethod.GET,
        produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
            ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
    public <T extends Model<I>, I extends Serializable> ResponseEntity<ResponseEnvelope> findLinked(
        @PathVariable("uri") String uri,
        @PathVariable("id") I id,
        @PathVariable("meta") String meta,
        Pageable pageable,
        PagedResourcesAssembler pagedResourcesAssembler,
        HttpServletRequest request
    ) {

        // Get the requested model and its repository instance.
        Class<T> model;
        ModelRepository<T, I> repository;

        try {
            if (!resourceRegistry.isRegisteredResource(uri)) {
                LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
                throw new ResourceNotFoundException();
            }
            model = (Class<T>) resourceRegistry.getModelByUri(uri);
            repository = (ModelRepository<T, I>) repositoryRegistry.getRepositoryByModel(model);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        LOGGER.info(String.format("Resolved request to model %s and repository %s",
            model.getName(), repository.getClass().getName()));

        // Get the requested model record instance
        Optional<T> recordOptional = repository.findById(id);
        if (!recordOptional.isPresent()) {
            throw new ResourceNotFoundException();
        }
        T record = recordOptional.get();

        // Check that the requested relationship is valid
        List<Field> annotatedFields = ModelReflectionUtils.getLinkedAnnotationsFromRelName(model, meta);
        if (annotatedFields.isEmpty()) {
            throw new InvalidParameterException(String.format("Requested model relationship is not found: %s", meta));
        } else if (annotatedFields.size() > 1) {
            throw new ModelDefinitionException("The requested model has more than one relationship defined withthe same 'rel' name: " + meta);
        }

        // Get the model and repository instance for the linked model.
        Field foreignKeyField = annotatedFields.get(0);
        Linked linked = foreignKeyField.getAnnotation(Linked.class);
        Class<? extends Model<?>> relModel = (Class<? extends Model<?>>) linked.model();
        String relFieldName = linked.field();
        ModelRepository<?, ?> metaRepository;

        try {
            metaRepository = repositoryRegistry.getRepositoryByModel(relModel);
        } catch (ModelRegistryException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        LOGGER.info(String.format("Resolved requested relModel %s and repository %s",
            relModel.getName(), metaRepository.getClass().getName()));

        // Get the include/exclude fields
        Set<String> includedFields = RequestUtils.getIncludedFieldsFromRequest(request);
        Set<String> excludedFields = RequestUtils.getExcludedFieldsFromRequest(request);
        if (!includedFields.isEmpty()) {
            LOGGER.info(String.format("Selected included fields: %s", includedFields.toString()));
        }
        if (!excludedFields.isEmpty()) {
            LOGGER.info(String.format("Excluded fields: %s", excludedFields.toString()));
        }

        ResponseEnvelope envelope;
        String mediaType = request.getHeader("Accept");

        // Get the foreign key field values to be used in the query
        List<Object> foreignKeyValues;
        BeanWrapper wrapper = new BeanWrapperImpl(record);
        if (Collection.class.isAssignableFrom(foreignKeyField.getType())) {
            foreignKeyValues = new ArrayList<>((Collection<?>) wrapper.getPropertyValue(foreignKeyField.getName()));
        } else {
            foreignKeyValues = Collections.singletonList(wrapper.getPropertyValue(foreignKeyField.getName()));
        }

        // Generate the query
        List<QueryCriteria> criterias
            = RequestUtils.getQueryCriteriaFromFindLinkedRequest(relModel, relFieldName, foreignKeyValues, request);

        // Query and package the response
        Link selfLink = new Link(rootUrl + "/search/" + uri + "/" + id + "/" + meta +
            (request.getQueryString() != null ? "?" + request.getQueryString() : ""), "self");

        if (RequestUtils.isPagedRequest(request)) {

            Page<?> page = metaRepository.find(criterias, pageable);

            if (ApiMediaTypes.isHalMediaType(mediaType)) {

                PagedResources<FilterableResource> pagedResources
                    = pagedResourcesAssembler.toResource(page, assembler, selfLink);
                envelope = new ResponseEnvelope(pagedResources, includedFields, excludedFields);

            } else {

                envelope = new ResponseEnvelope(page, includedFields, excludedFields);

            }

        } else {
            
            List<? extends Model<?>> entities;

            if (RequestUtils.isSortableRequest(request)) {
                entities = (List<? extends Model<?>>) metaRepository.find(criterias, pageable.getSort());
            } else {
                entities = (List<? extends Model<?>>) metaRepository.find(criterias);
            }

            if (ApiMediaTypes.isHalMediaType(mediaType)) {
                List<FilterableResource> resourceList = assembler.toResources(entities);
                Resources<FilterableResource> resources = new Resources<>(resourceList);
                resources.add(selfLink);
                envelope = new ResponseEnvelope(resources, includedFields, excludedFields);
            } else {
                envelope = new ResponseEnvelope(entities, includedFields, excludedFields);
            }

        }

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }
//
//  /**
//   * {@code HEAD /**}
//   * Performs a test on the resource endpoints availability.
//   *
//   * @return headers only.
//   */
//  @ApiResponses({
//      @ApiResponse(code = 200, message = "OK"),
//      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
//  })
//  @RequestMapping(value = { "/search/{uri}", "/search/{uri}/**" }, method = RequestMethod.HEAD)
//  public ResponseEntity<?> head(HttpServletRequest request, @PathVariable String uri) {
//    try {
//      if (!resourceRegistry.isRegisteredResource(uri)) {
//        LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
//        throw new ResourceNotFoundException();
//      }
//    } catch (ModelRegistryException e) {
//      e.printStackTrace();
//      throw new ResourceNotFoundException();
//    }
//    return new ResponseEntity<>(HttpStatus.OK);
//  }
//
//  /**
//   * {@code OPTIONS /}
//   * Returns an information about the endpoint and available options.
//   * TODO
//   *
//   * @return TBD
//   */
//  @ApiResponses({
//      @ApiResponse(code = 200, message = "OK"),
//      @ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
//  })
//  @RequestMapping(value = { "/{uri}", "/{uri}/**" }, method = RequestMethod.OPTIONS)
//  public ResponseEntity<?> options(HttpServletRequest request, @PathVariable String uri) {
//    try {
//      if (!resourceRegistry.isRegisteredResource(uri)) {
//        LOGGER.error(String.format("URI does not map to a registered model: %s", uri));
//        throw new ResourceNotFoundException();
//      }
//    } catch (ModelRegistryException e) {
//      e.printStackTrace();
//      throw new ResourceNotFoundException();
//    }
//    return new ResponseEntity<>(HttpStatus.OK);
//  }

    /**
     * Converts a String query parameter to the appropriate model I type.
     *
     * @param param String value of I parameter.
     * @param model Model type to interrogate to determine I type.
     * @return converted I object.
     */
    protected <T extends Model<I>, I extends Serializable> I convertModelIdParameter(String param, Class<T> model) {
        try {
            Class<I> type = (Class<I>) model.getMethod("getId").getReturnType();
            if (conversionService.canConvert(String.class, type)) {
                return conversionService.convert(param, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new MalformedEntityException(String.format("Cannot convert I parameter to model I type: %s", param));
    }

    /**
     * Attempts to convert a generic object, supplied in a HTTP request, to the target type.
     *
     * @param object object to be converted.
     * @param type class the object should be converted to.
     * @param <T>  generic type of the target class.
     * @return converted object.
     */
    protected <T> T convertObjectToModel(Object object, Class<T> type) {
//    objectMapper.setSerializationInclusion(Include.ALWAYS);
        try {
            return objectMapper.convertValue(object, type);
        } catch (Exception e) {
            throw new MalformedEntityException(String.format("Cannot convert object to model type %s: %s",
                type.getName(), object.toString()));
        }
    }

    public ResourceAssemblerSupport<Model, FilterableResource> getAssembler() {
        return assembler;
    }

}
