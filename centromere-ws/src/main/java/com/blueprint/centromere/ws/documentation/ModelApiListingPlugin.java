package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.model.Model;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * @author woemler
 * @since 0.4.1
 */
public class ModelApiListingPlugin implements ApiListingBuilderPlugin {

  @Autowired private ModelRepositoryRegistry registry;
  @Autowired private Environment env;
  @Autowired private TypeResolver typeResolver;

  @Override
  public void apply(ApiListingContext apiListingContext) {
    apiListingContext.apiListingBuilder().apis(getApiDescriptions());

  }

  protected List<ApiDescription> getApiDescriptions(){
    Assert.notNull(registry, "ModelRegistry must not be null.");
    Assert.notNull(env, "Environment must not be null.");
    List<ApiDescription> descriptions = new ArrayList<>();
    for (Class<? extends Model<?>> model: registry.getRegisteredModels()){
      String path = env.getRequiredProperty("centromere.api.root-url") + "/" + registry.getModelUri(model);
      descriptions.addAll(SwaggerPluginUtil.getModelApiDescriptions(model, typeResolver, path));
    }
    return descriptions;
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return SwaggerPluginSupport.pluginDoesApply(documentationType);
  }

}
