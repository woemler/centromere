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

package com.blueprint.centromere.core.ws;

import com.blueprint.centromere.core.commons.repositories.MetadataOperations;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
@RepositoryRestController
public class ModelController {

    private static final Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Autowired private Repositories repositories;
    @Autowired @Qualifier("defaultConversionService") private ConversionService conversionService;
    @Autowired RepositoryEntityLinks entityLinks;

    @RequestMapping(value = "/{repository}/guess", method = RequestMethod.GET)
    public HttpEntity guess(@QuerydslPredicate RootResourceInformation resourceInformation,
                            @RequestParam String keyword,
                            DefaultedPageable pageable,
                            Sort sort,
                            PersistentEntityResourceAssembler assembler)
            throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
        Class<?> model = resourceInformation.getDomainType();
        Object repo = repositories.getRepositoryFor(model);
        if (repo == null || !(repo instanceof MetadataOperations)) throw new ResourceNotFoundException();
        List<Object> results = (List<Object>) ((MetadataOperations) repo).guess(keyword);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping(value = "/{repository/distinct", method = RequestMethod.GET)
    public HttpEntity distinct(@QuerydslPredicate RootResourceInformation resourceInformation,
                               @RequestParam String field,
                               HttpServletRequest request) throws ResourceNotFoundException {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{repository}/query", method = RequestMethod.GET)
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

        logger.info(String.format("Generating dynamic query for model: %s", model.getName()));
        QueryDslPredicateExecutor repository = (QueryDslPredicateExecutor) repo;
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

        Iterable<?> results = repository.findAll(builder.getValue());
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}
