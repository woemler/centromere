package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.ws.config.ModelResourceRegistry;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.builders.ModelBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * @author woemler
 */
public class ResponseModelBuilderPlugin implements ModelBuilderPlugin {

  @Autowired private ModelResourceRegistry registry;
  @Autowired private TypeResolver typeResolver;
  @Autowired private ObjectMapper objectMapper;

  @Override
  public void apply(ModelContext modelContext) {
    ModelBuilder builder = modelContext.getBuilder();
    for (Class<?> model: registry.getRegisteredModels()){
      Model m = null;
      try {
        m = builder
            .type(typeResolver.resolve(model))
            .name(model.getSimpleName())
            .baseModel(model.getSimpleName())
            .description(String.format("Response model for %s", model.getSimpleName()))
            .example(objectMapper.writeValueAsString(new BeanWrapperImpl(model).getWrappedInstance()))
            .id(model.getSimpleName())
            .build();
      } catch (Exception e){
        e.printStackTrace();
      }
      
    }
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return SwaggerPluginSupport.pluginDoesApply(documentationType);
  }
}
