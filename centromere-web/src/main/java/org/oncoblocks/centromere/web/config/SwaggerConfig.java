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

/**
 * Performs configuration for Swagger spec datatables-buttons.0 API documentation via SpringFox.  API documentation 
 *   parameters are set in the {@code centromere.properties} file.
 * 
 * @author woemler
 */
@Deprecated
//@Configuration
//@EnableSwagger2
public class SwaggerConfig {
	
//	@Autowired private Environment env;
//	@Autowired private TypeResolver typeResolver;
//	@Autowired private ModelRegistry modelRegistry;
//
//	public static final Class<?>[] FILTERED_TYPES = {
//			Pageable.class,
//			PageRequest.class,
//			PagedResourcesAssembler.class
//	};
//
//	@Bean
//	public Docket api(){
//		return new Docket(DocumentationType.SWAGGER_2)
//				.select()
//				.apis(RequestHandlerSelectors.any())
//				.paths(apiPaths())
//				.build()
//				.apiInfo(apiInfo())
//				.ignoredParameterTypes(FILTERED_TYPES)
//				.alternateTypeRules(
//						AlternateTypeRules.newRule(
//								typeResolver.resolve(ResponseEntity.class,
//										typeResolver.resolve(ResponseEnvelope.class, WildcardType.class)),
//								typeResolver.resolve(WildcardType.class)))
//				.useDefaultResponseMessages(false)
//				.additionalModels(typeResolver.resolve(PageImpl.class), registeredModelTypes());
//	}
//
//	private ApiInfo apiInfo(){
//		return new ApiInfo(
//				env.getRequiredProperty("centromere.api.name"),
//				env.getRequiredProperty("centromere.api.description"),
//				env.getRequiredProperty("centromere.api.version"),
//				env.getRequiredProperty("centromere.api.tos"),
//				env.getRequiredProperty("centromere.api.contact-email"),
//				env.getRequiredProperty("centromere.api.license"),
//				env.getRequiredProperty("centromere.api.license-url")
//		);
//	}
//
//	private ResolvedType[] registeredModelTypes(){
//		ArrayList<ResolvedType> types = new ArrayList<>();
//		for (Class<?> model: modelRegistry.getModels()){
//			types.add(typeResolver.resolve(model));
//		}
//		return types.toArray(new ResolvedType[types.size()]);
//	}
//
//	private Predicate<String> apiPaths(){
//		return regex(env.getRequiredProperty("centromere.api.regex-url"));
//	}
//
//	@Bean
//	@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
//	public ModelParameterBuilderPlugin modelParameterBuilderPlugin(){
//		return new ModelParameterBuilderPlugin();
//	}
//
//	@Bean
//	@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
//	public MappedModelApiListingPlugin mappedModelApiListingPlugin(){
//		return new MappedModelApiListingPlugin();
//	}
	
}
