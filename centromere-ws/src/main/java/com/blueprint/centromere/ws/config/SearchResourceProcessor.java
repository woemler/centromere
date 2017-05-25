package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.repository.MetadataOperations;
import com.blueprint.centromere.ws.controller.ModelController;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * @author woemler
 */
public class SearchResourceProcessor implements ResourceProcessor<RepositorySearchesResource> {

  private Repositories repositories;

  @Override
  public RepositorySearchesResource process(RepositorySearchesResource repositorySearchesResource) {

    String root = repositorySearchesResource.getId().getHref();
    Class<?> model = repositorySearchesResource.getDomainType();
    Object repository = repositories.getRepositoryFor(model);

    if (repository != null){

      if (MetadataOperations.class.isAssignableFrom(repository.getClass())){

      }

    }

//    if (Gene.class.equals(model)){
//      repositorySearchesResource.add(
//          ControllerLinkBuilder.linkTo(
//              ControllerLinkBuilder.methodOn(ModelController.class))
//          .slash("search").slash("guess").withRel("guess"));
//    }

    return repositorySearchesResource;

  }
}
