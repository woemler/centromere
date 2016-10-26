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

package org.oncoblocks.centromere.web.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.blueprint.centromere.core.config.ModelRegistry;
import org.oncoblocks.centromere.web.controller.ResponseEnvelope;
import org.oncoblocks.centromere.web.documentation.MappedModelApiListingPlugin;
import org.oncoblocks.centromere.web.documentation.ModelParameterBuilderPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Performs configuration for Swagger spec datatables-buttons.0 API documentation via SpringFox.  API documentation 
 *   parameters are set in the {@code centromere.properties} file.
 * 
 * @author woemler
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Autowired private Environment env;
	@Autowired private TypeResolver typeResolver;
	@Autowired private ModelRegistry modelRegistry;

	public static final Class<?>[] FILTERED_TYPES = {
			Pageable.class,
			PageRequest.class,
			PagedResourcesAssembler.class
	};
	
	@Bean
	public Docket api(){
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(apiPaths())
				.build()
				.apiInfo(apiInfo())
				.ignoredParameterTypes(FILTERED_TYPES)
				.alternateTypeRules(
						AlternateTypeRules.newRule(
								typeResolver.resolve(ResponseEntity.class, 
										typeResolver.resolve(ResponseEnvelope.class, WildcardType.class)), 
								typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false)
				.additionalModels(typeResolver.resolve(PageImpl.class), registeredModelTypes());
	}

	private ApiInfo apiInfo(){
		return new ApiInfo(
				env.getRequiredProperty("centromere.api.name"),
				env.getRequiredProperty("centromere.api.description"),
				env.getRequiredProperty("centromere.api.version"),
				env.getRequiredProperty("centromere.api.tos"),
				env.getRequiredProperty("centromere.api.contact-email"),
				env.getRequiredProperty("centromere.api.license"),
				env.getRequiredProperty("centromere.api.license-url")
		);
	}
	
	private ResolvedType[] registeredModelTypes(){
		ArrayList<ResolvedType> types = new ArrayList<>();
		for (Class<?> model: modelRegistry.getModels()){
			types.add(typeResolver.resolve(model));
		}
		return types.toArray(new ResolvedType[types.size()]);
	}

	private Predicate<String> apiPaths(){
		return regex(env.getRequiredProperty("centromere.api.regex-url"));
	}
	
	@Bean
	@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
	public ModelParameterBuilderPlugin modelParameterBuilderPlugin(){
		return new ModelParameterBuilderPlugin();
	}
	
	@Bean
	@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
	public MappedModelApiListingPlugin mappedModelApiListingPlugin(){
		return new MappedModelApiListingPlugin();
	}
	
}
