package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.ws.controller.ModelController;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * @author woemler
 */
public class SearchResourceProcessor implements ResourceProcessor<RepositorySearchesResource> {

  @Override
  public RepositorySearchesResource process(RepositorySearchesResource repositorySearchesResource) {

    String root = repositorySearchesResource.getId().getHref();
    Class<?> model = repositorySearchesResource.getDomainType();

//    if (Gene.class.equals(model)){
//      repositorySearchesResource.add(
//          ControllerLinkBuilder.linkTo(
//              ControllerLinkBuilder.methodOn(ModelController.class))
//          .slash("search").slash("guess").withRel("guess"));
//    }

    return repositorySearchesResource;

  }
}
