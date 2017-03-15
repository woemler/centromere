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

import com.blueprint.centromere.core.commons.repositories.MetadataOperations;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.QueryCriteria;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.SearchResourceMappings;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.ProfileController;
import org.springframework.data.rest.webmvc.ProfileResourceProcessor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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
  private static final String BASE_URL = "/{repository}";

  @Autowired private PagedResourcesAssembler pagedResourcesAssembler;
  @Autowired private Repositories repositories;
  @Autowired private RepositoryEntityLinks entityLinks;
  @Autowired private RepositoryRestConfiguration config;


  @SuppressWarnings("unchecked")
  @RequestMapping(value = BASE_URL+"/guess", method = RequestMethod.GET)
  public HttpEntity guess(
      RootResourceInformation resourceInformation,
      @RequestParam String keyword,
      PersistentEntityResourceAssembler assembler
  ) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
    Class<?> model = resourceInformation.getDomainType();
    Object repo = repositories.getRepositoryFor(model);
    if (repo == null || !(repo instanceof MetadataOperations)) throw new ResourceNotFoundException();
    List<Object> results = (List<Object>) ((MetadataOperations) repo).guess(keyword);
    Link baseLink = entityLinks.linkToPagedResource(resourceInformation.getDomainType(), null);
    Resources<?> resources = toResources(results, model, assembler,  baseLink);
    resources.add(getCollectionResourceLinks(resourceInformation));

    return new ResponseEntity<>(resources, HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = BASE_URL+"/distinct", method = RequestMethod.GET)
  public HttpEntity distinct(
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
    if (model == null) throw new ResourceNotFoundException();
    Object repo = repositories.getRepositoryFor(model);
    if (repo == null || !(repo instanceof ModelRepository)) throw new ResourceNotFoundException();
    ModelRepository repository = (ModelRepository) repo;
    List<QueryCriteria> criterias = QueryUtils.getQueryCriteriaFromRequestParameters(model, parameters);
    Set<Object> distinct =  repository.distinct(field, criterias);

    return new ResponseEntity<>(distinct, HttpStatus.OK);

  }
  

//    @RequestMapping(value = BASE_URL+"/query", method = RequestMethod.GET)
//    public HttpEntity dynamic(
//        @QuerydslPredicate RootResourceInformation resourceInformation,
//        DefaultedPageable pageable,
//        Sort sort,
//        PersistentEntityResourceAssembler assembler,
//        HttpServletRequest request
//    ) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
//
//
//        Class<?> model = resourceInformation.getDomainType();
//        if (model == null) throw new ResourceNotFoundException();
//        Object repo = repositories.getRepositoryFor(model);
//        if (repo == null || !(repo instanceof QueryDslPredicateExecutor)) throw new ResourceNotFoundException();
//        QueryDslPredicateExecutor repository = (QueryDslPredicateExecutor) repo;
//        Predicate predicate = getPredicateFromRequest(model, request);
//
//        if (pageable.getPageable() != null){
//            Page<?> page = repository.findAll(predicate, pageable.getPageable());
//            return new ResponseEntity<>(page, HttpStatus.OK);
//        } else {
//            Iterable<?> results = repository.findAll(predicate, sort);
//            return new ResponseEntity<>(results, HttpStatus.OK);
//        }
//    }
//
//    protected Predicate getPredicateFromRequest(Class<?> model, HttpServletRequest request) {
//
//        logger.info(String.format("Generating dynamic query for model: %s", model.getName()));
//        BooleanBuilder builder = new BooleanBuilder();
//
//        for (Map.Entry<String, String[]> param: request.getParameterMap().entrySet()){
//
//            logger.info(String.format("Inspecting query parameter: key=%s  value=%s",
//                    param.getKey(), Arrays.asList(param.getValue()).toString()));
//
//            if (param.getValue().length == 0 || param.getValue()[0].trim().equals("")) continue;
//
//            // Is the param name a valid model field?
//              // Is it a direct match to field name?
//                // return default criteria path
//              // Is it a suffixed field name?
//                // return evaluation criteria
//
//            QueryCriteria criteria = QueryUtil.getCriteriaFromParameter(param.getKey(), model);
//            if (criteria == null) throw new QueryParameterException(
//                    String.format("Invalid query parameter: %s", param.getKey()));
//
//            // Convert parameter value in to target type.
//        }
//
//        return builder.getValue();
//
//    }
    

//    // TODO: Do we still need this?
//    @RequestMapping(value = BASE_URL+"/query", method = RequestMethod.GET)
//    public HttpEntity dynamic(@QuerydslPredicate RootResourceInformation resourceInformation,
//                              DefaultedPageable pageable,
//                              Sort sort,
//                              PersistentEntityResourceAssembler assembler,
//                              HttpServletRequest request)
//            throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
//
//        Class<?> model = resourceInformation.getDomainType();
//        if (model == null) throw new ResourceNotFoundException();
//        Object repo = repositories.getRepositoryFor(model);
//        if (repo == null || !(repo instanceof QueryDslPredicateExecutor)) throw new ResourceNotFoundException();
//        QueryDslPredicateExecutor repository = (QueryDslPredicateExecutor) repo;
//        Predicate predicate = getPredicateFromRequest(model, request);
//
//        if (pageable.getPageable() != null){
//            Page<?> page = repository.findAll(predicate, pageable.getPageable());
//            return new ResponseEntity<>(page, HttpStatus.OK);
//        } else {
//            Iterable<?> results = repository.findAll(predicate, sort);
//            return new ResponseEntity<>(results, HttpStatus.OK);
//        }
//    }

//    protected Predicate getPredicateFromRequest(Class<?> model, HttpServletRequest request) {
//
//        logger.info(String.format("Generating dynamic query for model: %s", model.getName()));
//        QueryParameterDescriptors descriptors = QueryUtil.getAvailableQueryParameters(model);
//        BooleanBuilder builder = new BooleanBuilder();
//
//        for (Map.Entry<String, String[]> param: request.getParameterMap().entrySet()){
//
//            logger.info(String.format("Inspecting query parameter: key=%s  value=%s",
//                    param.getKey(), Arrays.asList(param.getValue()).toString()));
//
//            if (param.getValue().length == 0 || param.getValue()[0].trim().equals("")) continue;
//
//            if (descriptors.matches(param.getKey())){
//                QueryParameterDescriptor descriptor = descriptors.get(param.getKey());
//                logger.info(String.format("Matched descriptor: %s", descriptor.toString()));
//                Predicate predicate = QueryUtil.getParameterPredicate(param.getKey(),
//                        param.getValue()[0], descriptor, conversionService);
//                if (predicate != null) builder.and(predicate);
//            }
//        }
//
//        return builder.getValue();
//
//    }
    
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

}
