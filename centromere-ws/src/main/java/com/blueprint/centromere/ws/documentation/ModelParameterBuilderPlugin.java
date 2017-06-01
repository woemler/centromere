package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
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
  @Autowired private ApplicationContext applicationContext;
  private static final String FIND_METHOD = "find";
  private static final Logger logger = LoggerFactory.getLogger(ModelParameterBuilderPlugin.class);

  @Override
  public void apply(OperationContext context) {
    logger.info("RequestMappingPattern: " + context.requestMappingPattern());
//    Object controller = applicationContext.getBean(context.getHandlerMethod().getBeanType());
//    if (controller == null) throw new ApiDocumentationException(String.format("Controller bean is null: %s",
//        context.getHandlerMethod().getBeanType().getName()));
//    if (context.getHandlerMethod().getMethod().getDeclaringClass().equals(ModelCrudController.class)
//        && FIND_METHOD.equals(context.getHandlerMethod().getMethod().getName())){
//      Class<? extends Model<?>> model = ((ModelCrudController) controller).getModel();
//      Map<String, QueryParameterDescriptor> paramMap = QueryParameterUtil.getAvailableQueryParameters(model);
//      Operation operation = context.operationBuilder().build();
//      List<Parameter> parameters = operation.getParameters() != null ? operation.getParameters() : new ArrayList<>();
//      for (QueryParameterDescriptor descriptor: paramMap.values()){
//        parameters.add(createParameterFromDescriptior(descriptor));
//      }
//      context.operationBuilder().parameters(parameters);
//    }
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return SwaggerPluginSupport.pluginDoesApply(documentationType);
  }

  private Parameter createParameterFromDescriptior(QueryParameterDescriptor descriptor){
    return new ParameterBuilder()
        .name(descriptor.getParamName())
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
