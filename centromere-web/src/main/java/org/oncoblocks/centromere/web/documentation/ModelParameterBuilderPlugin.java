/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.web.documentation;

import com.fasterxml.classmate.TypeResolver;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.QueryParameterDescriptor;
import org.oncoblocks.centromere.core.util.QueryParameterUtil;
import org.oncoblocks.centromere.web.controller.AbstractApiController;
import org.oncoblocks.centromere.web.exceptions.ApiDocumentationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	@Override public void apply(OperationContext context) {
		Object controller = applicationContext.getBean(context.getHandlerMethod().getBeanType());
		if (controller == null) throw new ApiDocumentationException(String.format("Controller bean is null: %s", 
				context.getHandlerMethod().getBeanType().getName()));
		if (context.getHandlerMethod().getMethod().getDeclaringClass().equals(AbstractApiController.class) 
				&& FIND_METHOD.equals(context.getHandlerMethod().getMethod().getName())){
			Class<? extends Model<?>> model = ((AbstractApiController) controller).getModel();
			Map<String, QueryParameterDescriptor> paramMap = QueryParameterUtil.getAvailableQueryParameters(model);
			Operation operation = context.operationBuilder().build();
			List<Parameter> parameters = operation.getParameters() != null ? operation.getParameters() : new ArrayList<>();
			for (QueryParameterDescriptor descriptor: paramMap.values()){
				parameters.add(createParameterFromDescriptior(descriptor));
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
