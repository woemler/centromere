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
public class ResponseModelBuilderPlugin {} /*implements ModelBuilderPlugin {
	
	@Autowired private ModelRegistry modelRegistry;
	@Autowired private TypeResolver typeResolver;
	@Autowired private ObjectMapper objectMapper;

	@Override 
	public void apply(ModelContext modelContext) {
		ModelBuilder builder = modelContext.getBuilder();
		for (Class<? extends Model> model: modelRegistry.getModels()){
			try {
				springfox.documentation.schema.Model m = builder
						.type(typeResolver.resolve(model))
						.name(ModelUtil.getDisplayName(model))
						.baseModel(ModelUtil.getDisplayName(model))
						.description(String.format("Response model for %s", model.getSimpleName()))
						.example(objectMapper.writeValueAsString(new BeanWrapperImpl(model).getWrappedInstance()))
						.id(ModelUtil.getDisplayName(model))
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
*/