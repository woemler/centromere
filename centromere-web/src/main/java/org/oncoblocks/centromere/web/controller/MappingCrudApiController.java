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

import io.swagger.annotations.*;
import org.oncoblocks.centromere.core.config.ModelRegistry;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.QueryCriteria;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.oncoblocks.centromere.web.exceptions.InvalidParameterException;
import org.oncoblocks.centromere.web.exceptions.RequestFailureException;
import org.oncoblocks.centromere.web.exceptions.ResourceNotFoundException;
import org.oncoblocks.centromere.web.exceptions.RestError;
import org.oncoblocks.centromere.web.util.ApiMediaTypes;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author woemler
 * @since 0.4.3
 */
@SuppressWarnings("unchecked")
public class MappingCrudApiController implements ApplicationContextAware, InitializingBean {

	private ModelRegistry registry;
	private ResourceAssemblerSupport<Model, FilterableResource> assembler;
	private ApplicationContext applicationContext;
	private ConversionService conversionService;
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MappingCrudApiController.class);
	
	@PostConstruct
	public void afterPropertiesSet(){
		if (assembler == null){
			Assert.notNull(registry, "ModelComponentRegistry must not be null!");
			assembler = new MappingModelResourceAssembler(registry, MappingCrudApiController.class);
		}
		if (conversionService == null){
			conversionService = new DefaultConversionService();
		}
	}

	/**
	 * {@code GET /{id}}
	 * Fetches a single record by its primary ID and returns it, or a {@code Not Found} exception if not.
	 *
	 * @param id primary ID for the target record.
	 * @return {@code T} instance
	 */
	@ApiImplicitParams({
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
			value = "/{uri}/{id}", 
			method = RequestMethod.GET,
			produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE, 
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE, 
					MediaType.TEXT_PLAIN_VALUE })
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<T>> findById(
			@ApiParam(name = "id", value = "Model record primary id.") @PathVariable String id,
			@PathVariable String uri,
			HttpServletRequest request
	) {
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		if (RequestUtils.requestContainsNonDefaultParameters(RequestUtils.findOneParameters(), request.getParameterMap())){
			throw new InvalidParameterException("Request contains invalid query string parameters.");
		}
		Set<String> fields = RequestUtils.getFilteredFieldsFromRequest(request);
		Set<String> exclude = RequestUtils.getExcludedFieldsFromRequest(request);
		
		T entity = repository.findOne((ID) convertModelIdParameter(id, model));
		if (entity == null) throw new ResourceNotFoundException();
		ResponseEnvelope<T> envelope = null;
		if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
			FilterableResource resource = (FilterableResource) assembler.toResource(entity);
			envelope = new ResponseEnvelope<>(resource, fields, exclude);
		} else {
			envelope = new ResponseEnvelope<>(entity, fields, exclude);
		}
		return new ResponseEntity<>(envelope, HttpStatus.OK);
	}

	/**
	 * {@code GET /distinct}
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
			value = "/{uri}/distinct", 
			method = RequestMethod.GET,
			produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.TEXT_PLAIN_VALUE })
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> findDistinct(
			@ApiParam(name = "field", value = "Model field name.") @RequestParam String field,
			@PathVariable String uri,
			HttpServletRequest request)
	{
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		List<QueryCriteria> queryCriterias = RequestUtils.getQueryCriteriaFromFindDistinctRequest(model, request);
		List<Object> distinct = (List<Object>) repository.distinct(field, queryCriterias);
		ResponseEnvelope<Object> envelope = null;
		if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
			Link selfLink = new Link(linkTo(this.getClass()).slash("distinct").toString() + 
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
	 * Queries the repository using inputted query string paramters, defined within a annotated 
	 *   {@link Model} classes.  Supports hypermedia, pagination, sorting, field 
	 *   filtering, and field exclusion.
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
			value = "/{uri}", 
			method = RequestMethod.GET,
			produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.TEXT_PLAIN_VALUE})
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<T>> find(
			@PageableDefault(size = 1000) Pageable pageable,
			@PathVariable String uri,
			PagedResourcesAssembler pagedResourcesAssembler, 
			HttpServletRequest request)
	{
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		ResponseEnvelope<T> envelope;
		Set<String> fields = RequestUtils.getFilteredFieldsFromRequest(request);
		Set<String> exclude = RequestUtils.getExcludedFieldsFromRequest(request);
		pageable = RequestUtils.remapPageable(pageable, model);
		Map<String,String[]> parameterMap = request.getParameterMap();
		List<QueryCriteria> criterias = RequestUtils.getQueryCriteriaFromFindRequest(model, request);
		String mediaType = request.getHeader("Accept");
		Link selfLink = new Link(linkTo(this.getClass()).slash("").toString() +
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
			List<T> entities = null;
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
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<?> create(
			@ApiParam(name = "entity", value = "Model record entity.") @RequestBody T entity,
			@PathVariable String uri,
			HttpServletRequest request
	) {
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		T created = repository.insert(entity);
		if (created == null) throw new RequestFailureException(40003, "There was a problem creating the record.", "", "");
		if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
			FilterableResource resource = getAssembler().toResource(created);
			return new ResponseEntity<>(resource, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(created, HttpStatus.CREATED);
		}
	}

	/**
	 * {@code PUT /{id}}
	 * Attempts to update an existing entity record, replacing it with the submitted entity. Throws
	 *   an exception if the target entity does not exist.
	 *
	 * @param entity entity representation to update.
	 * @param id primary ID of the target entity
	 * @return updated representation of the submitted entity.
	 */
	@ApiResponses({
			@ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
			@ApiResponse(code	= 404, message = "Record not found", response = RestError.class),
			@ApiResponse(code = 406, message = "Malformed entity", response = RestError.class)
	})
	@RequestMapping(
			value = "/{uri}/{id}",
			method = RequestMethod.PUT,
			produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.TEXT_PLAIN_VALUE})
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<?> update(
			@ApiParam(name = "entity", value = "Model record entity.") @RequestBody T entity,
			@ApiParam(name = "id", value = "Model record primary id.") @PathVariable String id,
			@PathVariable String uri,
			HttpServletRequest request
	) {
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		if (repository.exists((ID) convertModelIdParameter(id, model))) throw new ResourceNotFoundException();
		T updated = repository.update(entity);
		if (updated == null) throw new RequestFailureException(40004, "There was a problem updating the record.", "", "");
		if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
			FilterableResource resource = getAssembler().toResource(updated);
			return new ResponseEntity<>(resource, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(updated, HttpStatus.CREATED);
		}
	}

	/**
	 * {@code DELETE /{id}}
	 * Attempts to delete the an entity identified by the submitted primary ID.
	 *
	 * @param id primary ID of the target record.
	 * @return {@link HttpStatus} indicating success or failure.
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 401, message = "Unauthorized", response = RestError.class),
			@ApiResponse(code	= 404, message = "Record not found", response = RestError.class)
	})
	@RequestMapping(
			value = "/{uri}/{id}",
			method = RequestMethod.DELETE)
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<?> delete(
			@ApiParam(name = "id", value = "Model record primary id.") @PathVariable String id,
			@PathVariable String uri,
			HttpServletRequest request) {
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		Class<? extends Model> model = registry.getModelFromUri(uri);
		RepositoryOperations repository = (RepositoryOperations) registry.getModelRepository(model);
		repository.delete(convertModelIdParameter(id, model));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * {@code HEAD /**}
	 * Performs a test on the resource endpoints availability.
	 *
	 * @return headers only.
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
	})
	@RequestMapping(value = { "/{uri}", "/{uri}/**" }, method = RequestMethod.HEAD)
	public ResponseEntity<?> head(HttpServletRequest request, @PathVariable String uri){
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * {@code OPTIONS /}
	 * Returns an information about the endpoint and available parameters.
	 * TODO
	 *
	 * @return TBD
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Resource not found.", response = RestError.class)
	})
	@RequestMapping(value = { "/{uri}", "/{uri}/**" }, method = RequestMethod.OPTIONS)
	public ResponseEntity<?> options(HttpServletRequest request, @PathVariable String uri) {
		if (!registry.isSupportedUri(uri)){
			throw new ResourceNotFoundException();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Converts a String query parameter to the appropriate model ID type.
	 *
	 * @param param
	 * @param model
	 * @return
	 */
	private Object convertModelIdParameter(String param, Class<? extends Model> model){
		try {
			Class<?> type = model.getMethod("getId").getReturnType();
			if (conversionService.canConvert(String.class, type)){
				return conversionService.convert(param, type);
			} else {
				return param;
			}
		} catch (NoSuchMethodException e){
			return param;
		}
	}
	
	public ResourceAssemblerSupport<Model, FilterableResource> getAssembler() {
		return assembler;
	}

	@Autowired 
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Autowired
	public void setConversionService(ConversionService conversionService){
		this.conversionService = conversionService;
	}
	
	@Autowired
	public void setModelRegistry(ModelRegistry registry){
		this.registry = registry;
	}
}
