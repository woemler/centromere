package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.repository.QueryParameterUtil;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Springfox plugin for adding {@link Model} query parameters to the auto-generated Swagger
 *   documentation.
 *
 * @author woemler
 * @since 0.4.1
 */
public class ModelParameterBuilderPlugin implements OperationBuilderPlugin {

  @Autowired private TypeResolver typeResolver;
  @Autowired private ModelResourceRegistry registry;
  private static final String FIND_METHOD = "find";
  private static final Logger logger = LoggerFactory.getLogger(ModelParameterBuilderPlugin.class);

  @Override
  public void apply(OperationContext context) {
    logger.info("Swagger Plugin - OperationContext - Name: " + context.getName());
    if (FIND_METHOD.equals(context.getName())){
      Operation operation = context.operationBuilder().build();
      List<Parameter> parameters = operation.getParameters() != null
          ? operation.getParameters() : new ArrayList<>();
      for (Class<? extends Model<?>> model: registry.getRegisteredModels() ){
        for (QueryParameterDescriptor descriptor: QueryParameterUtil.getAvailableQueryParameters(model).values()){
          parameters.add(createParameterFromDescriptior(descriptor));
        }
      }
      context.operationBuilder().parameters(parameters);
    }
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return SwaggerPluginSupport.pluginDoesApply(documentationType);
  }

  private Parameter createParameterFromDescriptior(QueryParameterDescriptor descriptor){
    return new ParameterBuilder()
        .name(descriptor.getFieldName())
        .type(typeResolver.resolve(typeResolver.resolve(descriptor.getType())))
        .modelRef(new ModelRef("string"))
        .parameterType("query")
        .required(false)
        .description(String.format("Query against the '%s' field.", descriptor.getFieldName()))
        .defaultValue("")
        .allowMultiple(false)
        .parameterAccess("")
        .build();
  }

}
