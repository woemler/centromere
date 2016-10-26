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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.blueprint.centromere.core.config.ModelRegistry;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.RepositoryOperations;
import org.oncoblocks.centromere.web.exceptions.*;
import org.oncoblocks.centromere.web.util.ApiMediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
@Deprecated
@Controller
@RequestMapping("${centromere.api.root-url}")
@SuppressWarnings("unchecked")
public class MappingCrudApiController implements ModelController {

	@Autowired private ModelRegistry registry;
	@Autowired private MappingModelResourceAssembler assembler;
	@Autowired private ConversionService conversionService;
	@Autowired private ObjectMapper objectMapper;
	
	@Value("${centromere.api.root-url}")
	private String rootUrl;
	
	private static final Logger logger = LoggerFactory.getLogger(MappingCrudApiController.class);

	/**
	 * {@code GET /{id}}
	 * Fetches a single record by its primary ID and returns it, or a {@code Not Found} exception if not.
	 *
	 * @param id primary ID for the target record.
	 * @return {@code T} instance
	 */
	@RequestMapping(
			value = "/{uri}/{id}", 
			method = RequestMethod.GET,
			produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE, 
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE, 
					MediaType.TEXT_PLAIN_VALUE })
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<T>> findById(
			@PathVariable String id,
			@PathVariable String uri,
			HttpServletRequest request
	) {
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		if (RequestUtils.requestContainsNonDefaultParameters(RequestUtils.findOneParameters(), request.getParameterMap())){
			throw new InvalidParameterException("Request contains invalid query string parameters.");
		}
		Set<String> fields = RequestUtils.getFilteredFieldsFromRequest(request);
		Set<String> exclude = RequestUtils.getExcludedFieldsFromRequest(request);
		T entity = repository.findOne(convertModelIdParameter(id, model));
		if (entity == null) throw new ResourceNotFoundException();
		ResponseEnvelope<T> envelope = null;
		if (ApiMediaTypes.isHalMediaType(request.getHeader("Accept"))){
			FilterableResource resource = assembler.toResource(entity);
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
	@RequestMapping(
			value = "/{uri}/distinct", 
			method = RequestMethod.GET,
			produces = { ApiMediaTypes.APPLICATION_HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE,
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.TEXT_PLAIN_VALUE })
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<ResponseEnvelope<Object>> findDistinct(
			@RequestParam String field,
			@PathVariable String uri,
			HttpServletRequest request)
	{
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
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
	 * {@code GET /}
	 * Queries the repository using inputted query string paramters, defined within a annotated 
	 *   {@link Model} classes.  Supports hypermedia, pagination, sorting, field 
	 *   filtering, and field exclusion.
	 * 
	 * @param pagedResourcesAssembler {@link PagedResourcesAssembler}
	 * @param request {@link HttpServletRequest}
	 * @return a {@link List} of {@link Model} objects.
	 */
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
			logger.error(String.format("URI does not map to a registered model: %s", uri));
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
		Link selfLink = new Link(rootUrl + "/" + uri + 
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
	@RequestMapping(
			value = "/{uri}",
			method = RequestMethod.POST,
			produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.TEXT_PLAIN_VALUE})
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<?> create(
			@RequestBody Object entity,
			@PathVariable String uri,
			HttpServletRequest request
	) {
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		entity = convertObjectToModel(entity, model);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		T created = repository.insert((T) entity);
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
	@RequestMapping(
			value = "/{uri}/{id}",
			method = RequestMethod.PUT,
			produces = { MediaType.APPLICATION_JSON_VALUE, ApiMediaTypes.APPLICATION_HAL_JSON_VALUE,
					ApiMediaTypes.APPLICATION_HAL_XML_VALUE, MediaType.APPLICATION_XML_VALUE,
					MediaType.TEXT_PLAIN_VALUE})
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<?> update(
			@RequestBody Object entity,
			@PathVariable String id,
			@PathVariable String uri,
			HttpServletRequest request
	) {
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		entity = convertObjectToModel(entity, model);
		RepositoryOperations<T, ID> repository = (RepositoryOperations<T, ID>) registry.getModelRepository(model);
		if (!repository.exists(convertModelIdParameter(id, model))) {
			throw new ResourceNotFoundException();
		}
		T updated = repository.update((T) entity);
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
	@RequestMapping(
			value = "/{uri}/{id}",
			method = RequestMethod.DELETE)
	public <T extends Model<ID>, ID extends Serializable> ResponseEntity<?> delete(
			@PathVariable String id,
			@PathVariable String uri,
			HttpServletRequest request) {
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
			throw new ResourceNotFoundException();
		}
		Class<T> model = (Class<T>) registry.getModelFromUri(uri);
		RepositoryOperations repository = registry.getModelRepository(model);
		repository.delete(convertModelIdParameter(id, model));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * {@code HEAD /**}
	 * Performs a test on the resource endpoints availability.
	 *
	 * @return headers only.
	 */
	@RequestMapping(value = { "/{uri}", "/{uri}/**" }, method = RequestMethod.HEAD)
	public ResponseEntity<?> head(HttpServletRequest request, @PathVariable String uri){
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
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
	@RequestMapping(value = { "/{uri}", "/{uri}/**" }, method = RequestMethod.OPTIONS)
	public ResponseEntity<?> options(HttpServletRequest request, @PathVariable String uri) {
		if (!registry.isSupportedUri(uri)){
			logger.error(String.format("URI does not map to a registered model: %s", uri));
			throw new ResourceNotFoundException();
		}
		return new ResponseEntity<>(HttpStatus.OK);
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
