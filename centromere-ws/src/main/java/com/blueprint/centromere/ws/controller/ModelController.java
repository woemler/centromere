/*
 * Copyright 2016 the original author or authors
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

import com.blueprint.centromere.core.commons.repository.MetadataOperations;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.ProfileController;
import org.springframework.data.rest.webmvc.ProfileResourceProcessor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author woemler
 */
@RepositoryRestController
public class ModelController {

  private static final Logger logger = LoggerFactory.getLogger(ModelController.class);
  private static final String BASE_URL = "/{repository}/search";

  private final List<String> halMediaTypes = Arrays.asList(MediaTypes.HAL_JSON_VALUE, "application/hal+json;charset=utf8");

  @Autowired private PagedResourcesAssembler pagedResourcesAssembler;
  @Autowired private Repositories repositories;
  @Autowired private RepositoryEntityLinks entityLinks;
  @Autowired private RepositoryRestConfiguration config;

  @SuppressWarnings("unchecked")
  @RequestMapping(value = BASE_URL+"/guess", method = RequestMethod.GET)
  public HttpEntity<Resources<?>> guess(
      RootResourceInformation resourceInformation,
      @RequestParam(name = "keyword") String keyword,
      PersistentEntityResourceAssembler assembler
  ) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

    Class<?> model = resourceInformation.getDomainType();
    if (model == null) {
      throw new ResourceNotFoundException();
    }

    Object repo = repositories.getRepositoryFor(model);
    if (repo == null || !(repo instanceof MetadataOperations)) {
      throw new ResourceNotFoundException();
    }

    List<Object> results = (List<Object>) ((MetadataOperations) repo).guess(keyword);

    Link baseLink = entityLinks.linkToPagedResource(resourceInformation.getDomainType(), null);
    Resources<?> resources = toResources(results, model, assembler,  baseLink);
    resources.add(getCollectionResourceLinks(resourceInformation));

    return new ResponseEntity<>(resources, HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = BASE_URL+"/distinct", method = RequestMethod.GET)
  public HttpEntity<Collection<Object>> distinct(
      @QuerydslPredicate RootResourceInformation resourceInformation,
      @RequestParam MultiValueMap<String, String> parameters
  ) throws ResourceNotFoundException, NoSuchMethodException {

    if (!parameters.containsKey("field") || parameters.get("field").isEmpty() ||
        parameters.get("field").get(0).trim().equals("")) {
        throw new ResourceNotFoundException("Parameter 'field' not present.");
    }
    String field = parameters.get("field").get(0);
    parameters.remove("field");

    Class<?> model = resourceInformation.getDomainType();
    if (model == null) {
      throw new ResourceNotFoundException();
    }

//    try {
//      model.getClass().getDeclaredField(field);
//    } catch (NoSuchFieldException e){
//      logger.warn(String.format("Unable to find field %s for model %s", field, model.getName()));
//      return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //TODO: more informative response and/or exception
//    }

    Object repo = repositories.getRepositoryFor(model);
    if (repo == null || !(repo instanceof ModelRepository)) {
      throw new ResourceNotFoundException();
    }

    ModelRepository repository = (ModelRepository) repo;
    List<QueryCriteria> criterias = QueryUtils.getQueryCriteriaFromRequestParameters(model, parameters);
    Set<Object> distinct =  repository.distinct(field, criterias);

    return new ResponseEntity<>(distinct, HttpStatus.OK);

  }

  @RequestMapping(value = BASE_URL + "/query", method = RequestMethod.GET)
  public HttpEntity<?> find(
      @QuerydslPredicate RootResourceInformation resourceInformation,
      PersistentEntityResourceAssembler assembler,
      DefaultedPageable pageable,
      Sort sort,
      HttpServletRequest request
  ){
    RepositoryInvoker invoker = resourceInformation.getInvoker();
    Class<?> model = resourceInformation.getDomainType();
    Map<String, String[]> params = request.getParameterMap();
    Set<String> fieldSet = params.containsKey("fields") ?
        Sets.newHashSet(params.get("fields")) : new HashSet<>();
    Set<String> exclude = params.containsKey("exclude") ?
        Sets.newHashSet(params.get("exclude")) : new HashSet<>();
    if (null == invoker) {
      throw new ResourceNotFoundException();
    }
    Iterable<?> results = pageable.getPageable() != null ? invoker.invokeFindAll(pageable.getPageable())
        : invoker.invokeFindAll(sort);
    ResponseEnvelope<?> envelope;
    if (isHalMediaType(request.getHeader("Accept"))){
      Link link = entityLinks.linkToPagedResource(model, null);
      Resources<?> resources = pageable.getPageable() != null ?
          toPagedResources((Page<?>) results, model, assembler, link) :
          toResources(iterableToList(results), model, assembler, link);
      envelope = new ResponseEnvelope<>(resources, fieldSet, exclude);
    } else {
      envelope = new ResponseEnvelope<>(results, fieldSet, exclude);
    }
    return new ResponseEntity<>(envelope, HttpStatus.OK);
  }

  protected boolean isHalMediaType(String mediaType){
    return halMediaTypes.contains(mediaType);
  }

  @SuppressWarnings("unchecked")
  protected Resources<?> toPagedResources(
      Page<?> page,
      Class<?> model,
      PersistentEntityResourceAssembler assembler,
      Link baseLink
  ){
    if (page.getContent().isEmpty()) return pagedResourcesAssembler.toEmptyResource(page, model, baseLink);
    else if (baseLink != null) return pagedResourcesAssembler.toResource(page, assembler, baseLink);
    else return pagedResourcesAssembler.toResource(page, assembler);
  }

  @SuppressWarnings("unchecked")
  protected Resources<?> toResources(
      Collection<?> records,
      Class<?> model,
      PersistentEntityResourceAssembler assembler,
      Link baseLink
  ){
    Link selfLink = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
    if (records.isEmpty()) return new Resources<>(Collections.EMPTY_LIST, selfLink);
    List<Resource<?>> resources = new ArrayList<>();
    for (Object record: records){
      if (record != null) {
        resources.add(assembler.toResource(record));
      }
    }
    return new Resources<>(resources, selfLink);
  }

  protected List<Link> getCollectionResourceLinks(RootResourceInformation resourceInformation) {
    ResourceMetadata metadata = resourceInformation.getResourceMetadata();
    SearchResourceMappings searchMappings = metadata.getSearchResourceMappings();
    List<Link> links = new ArrayList<>();
    links.add(new Link(ProfileController.getPath(this.config, metadata), ProfileResourceProcessor.PROFILE_REL));
    if (searchMappings.isExported()) {
      links.add(entityLinks.linkFor(metadata.getDomainType()).slash(searchMappings.getPath())
          .withRel(searchMappings.getRel()));
    }
    return links;
  }

  protected List<Object> iterableToList(Iterable<?> iterable){
    List<Object> list = new ArrayList<>();
    for (Object obj: iterable){
      list.add(obj);
    }
    return list;
  }

}
