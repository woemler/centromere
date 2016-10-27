package com.blueprint.centromere.core.ws;

import com.blueprint.centromere.core.repository.MetadataOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author woemler
 */
@RepositoryRestController
public class ModelController {

    @Autowired private Repositories repositories;
    @Autowired @Qualifier("defaultConversionService") private ConversionService conversionService;

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

}
