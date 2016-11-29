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

package com.blueprint.centromere.core.ws.controller;

import com.blueprint.centromere.core.commons.repositories.MetadataOperations;
import com.blueprint.centromere.core.repository.BaseRepository;
import com.blueprint.centromere.core.ws.QueryParameterDescriptor;
import com.blueprint.centromere.core.ws.QueryParameterDescriptors;
import com.blueprint.centromere.core.ws.QueryUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
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
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.rest.webmvc.ControllerUtils.EMPTY_RESOURCE_LIST;

/**
 * @author woemler
 */
@RepositoryRestController
public class ModelController {

    private static final Logger logger = LoggerFactory.getLogger(ModelController.class);
    private static final String BASE_URL = "/{repository}";

    @Autowired private PagedResourcesAssembler pagedResourcesAssembler;
    @Autowired private Repositories repositories;
    @Autowired @Qualifier("defaultConversionService") private ConversionService conversionService;
    @Autowired private RepositoryEntityLinks entityLinks;
    @Autowired private RepositoryRestConfiguration config;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = BASE_URL+"/guess", method = RequestMethod.GET)
    public HttpEntity guess(@QuerydslPredicate RootResourceInformation resourceInformation,
                            @RequestParam String keyword,
                            PersistentEntityResourceAssembler assembler)
            throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

        Class<?> model = resourceInformation.getDomainType();
        Object repo = repositories.getRepositoryFor(model);
        if (repo == null || !(repo instanceof MetadataOperations)) throw new ResourceNotFoundException();

        List<Object> results = (List<Object>) ((MetadataOperations) repo).guess(keyword);

        Link baseLink = entityLinks.linkToPagedResource(resourceInformation.getDomainType(), null);
        Resources<?> resources = toResources(results, assembler, resourceInformation.getDomainType(), baseLink);
        resources.add(getCollectionResourceLinks(resourceInformation, null));

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = BASE_URL+"/distinct", method = RequestMethod.GET)
    public HttpEntity distinct(@QuerydslPredicate RootResourceInformation resourceInformation,
                               @RequestParam String field,
                               HttpServletRequest request) throws ResourceNotFoundException {

        Class<?> model = resourceInformation.getDomainType();
        if (model == null) throw new ResourceNotFoundException();
        Object repo = repositories.getRepositoryFor(model);
        if (repo == null || !(repo instanceof BaseRepository)) throw new ResourceNotFoundException();
        BaseRepository repository = (BaseRepository) repo;
        Predicate predicate = getPredicateFromRequest(model, request);
        Set<Object> distinct = repository.distinct(field, predicate);
        return new ResponseEntity<>(distinct, HttpStatus.OK);

    }

    @RequestMapping(value = BASE_URL+"/query", method = RequestMethod.GET)
    public HttpEntity dynamic(@QuerydslPredicate RootResourceInformation resourceInformation,
                              DefaultedPageable pageable,
                              Sort sort,
                              PersistentEntityResourceAssembler assembler,
                              HttpServletRequest request)
            throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {

        Class<?> model = resourceInformation.getDomainType();
        if (model == null) throw new ResourceNotFoundException();
        Object repo = repositories.getRepositoryFor(model);
        if (repo == null || !(repo instanceof QueryDslPredicateExecutor)) throw new ResourceNotFoundException();
        QueryDslPredicateExecutor repository = (QueryDslPredicateExecutor) repo;
        Predicate predicate = getPredicateFromRequest(model, request);

        if (pageable.getPageable() != null){
            Page<?> page = repository.findAll(predicate, pageable.getPageable());
            return new ResponseEntity<>(page, HttpStatus.OK);
        } else {
            Iterable<?> results = repository.findAll(predicate, sort);
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
    }

    protected Predicate getPredicateFromRequest(Class<?> model, HttpServletRequest request) {

        logger.info(String.format("Generating dynamic query for model: %s", model.getName()));
        QueryParameterDescriptors descriptors = QueryUtil.getAvailableQueryParameters(model);
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, String[]> param: request.getParameterMap().entrySet()){

            logger.info(String.format("Inspecting query parameter: key=%s  value=%s",
                    param.getKey(), Arrays.asList(param.getValue()).toString()));

            if (param.getValue().length == 0 || param.getValue()[0].trim().equals("")) continue;

            if (descriptors.matches(param.getKey())){
                QueryParameterDescriptor descriptor = descriptors.get(param.getKey());
                logger.info(String.format("Matched descriptor: %s", descriptor.toString()));
                Predicate predicate = QueryUtil.getParameterPredicate(param.getKey(),
                        param.getValue()[0], descriptor, conversionService);
                if (predicate != null) builder.and(predicate);
            }
        }

        return builder.getValue();

    }

    @SuppressWarnings({ "unchecked" })
    protected Resources<?> toResources(Iterable<?> source, PersistentEntityResourceAssembler assembler,
                                       Class<?> domainType, Link baseLink) {

        if (source instanceof Page) {
            Page<Object> page = (Page<Object>) source;
            return entitiesToResources(page, assembler, domainType, baseLink);
        } else if (source instanceof Iterable) {
            return entitiesToResources((Iterable<Object>) source, assembler, domainType);
        } else {
            return new Resources(EMPTY_RESOURCE_LIST);
        }
    }

    protected Resources<?> entitiesToResources(Page<Object> page, PersistentEntityResourceAssembler assembler,
                                               Class<?> domainType, Link baseLink) {

        if (page.getContent().isEmpty()) {
            return pagedResourcesAssembler.toEmptyResource(page, domainType, baseLink);
        }

        return baseLink == null ? pagedResourcesAssembler.toResource(page, assembler)
                : pagedResourcesAssembler.toResource(page, assembler, baseLink);
    }

    protected Resources<?> entitiesToResources(Iterable<Object> entities, PersistentEntityResourceAssembler assembler,
                                               Class<?> domainType) {

        if (!entities.iterator().hasNext()) {

            List<Object> content = Collections.EMPTY_LIST;
            return new Resources<Object>(content, getDefaultSelfLink());
        }

        List<Resource<Object>> resources = new ArrayList<Resource<Object>>();

        for (Object obj : entities) {
            resources.add(obj == null ? null : assembler.toResource(obj));
        }

        return new Resources<>(resources, getDefaultSelfLink());
    }

    protected Link getDefaultSelfLink() {
        return new Link(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
    }

    private List<Link> getCollectionResourceLinks(RootResourceInformation resourceInformation,
                                                  DefaultedPageable pageable) {

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
