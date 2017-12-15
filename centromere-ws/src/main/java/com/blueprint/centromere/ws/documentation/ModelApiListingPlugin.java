package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.config.WebProperties;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private ModelResourceRegistry registry;
  @Autowired private WebProperties webProperties;
  @Autowired private TypeResolver typeResolver;

  @Override
  public void apply(ApiListingContext apiListingContext) {
    apiListingContext.apiListingBuilder().apis(getApiDescriptions());

  }

  protected List<ApiDescription> getApiDescriptions(){
    Assert.notNull(registry, "ModelRegistry must not be null.");
    Assert.notNull(webProperties, "WebProperties must not be null.");
    List<ApiDescription> descriptions = new ArrayList<>();
    for (Class<? extends Model<?>> model: registry.getRegisteredModels()){
      try {
        String path = webProperties.getApi().getRootUrl() + "/" + registry.getUriByModel(model);
        descriptions.addAll(SwaggerPluginUtil.getModelApiDescriptions(model, typeResolver, path));
      } catch (ModelRegistryException e){
        throw new RuntimeException(e); //TODO better exception handling
      }
    }
    return descriptions;
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return SwaggerPluginSupport.pluginDoesApply(documentationType);
  }

}
