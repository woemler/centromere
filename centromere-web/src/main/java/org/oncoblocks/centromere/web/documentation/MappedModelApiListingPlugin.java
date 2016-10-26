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


/**
 * @author woemler
 */
@Deprecated
public class MappedModelApiListingPlugin /*implements ApiListingBuilderPlugin*/ {

//	@Autowired private ModelRegistry modelRegistry;
//	@Autowired private Environment env;
//	@Autowired private TypeResolver typeResolver;
//
//	@Override
//	public void apply(ApiListingContext apiListingContext) {
//		apiListingContext.apiListingBuilder().apis(getApiDescriptions());
//
//	}
//
//	protected List<ApiDescription> getApiDescriptions(){
//		Assert.notNull(modelRegistry, "ModelRegistry must not be null.");
//		Assert.notNull(env, "Environment must not be null.");
//		List<ApiDescription> descriptions = new ArrayList<>();
//		for (Class<? extends Model> model: modelRegistry.getModels()){
//			String path = env.getRequiredProperty("centromere.api.root-url") + "/" + modelRegistry.getModelUri(model);
//			descriptions.addAll(SwaggerPluginUtil.getModelApiDescriptions(model, typeResolver, path));
//		}
//		return descriptions;
//	}
//
//	@Override
//	public boolean supports(DocumentationType documentationType) {
//		return SwaggerPluginSupport.pluginDoesApply(documentationType);
//	}

}
