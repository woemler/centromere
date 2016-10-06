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

package org.oncoblocks.centromere.core.util;

import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.model.ModelAttributes;

/**
 * Helper methods for inspecting {@link Model} class implementations.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class ModelUtil {

	/**
	 * Determines the default URI to use for {@link Model} web service resources.  Uses class name as
	 *   default.
	 * 
	 * @param model model class
	 * @return uri
	 */
	public static String getDefaultUri(Class<? extends Model> model){
		String uri = model.getSimpleName().toLowerCase();
		if (model.isAnnotationPresent(ModelAttributes.class)){
			ModelAttributes modelAttributes = model.getAnnotation(ModelAttributes.class);
			if (!"".equals(modelAttributes.uri())){
				uri = modelAttributes.uri();
			}
		}
		return uri;
	}

	/**
	 * Determines the display name to use for a given {@link Model}.  Uses the simple class name as 
	 *   default.
	 * 
	 * @param model model class
	 * @return display name
	 */
	public static String getDisplayName(Class<? extends Model> model){
		String name = model.getSimpleName();
		if (model.isAnnotationPresent(ModelAttributes.class)){
			ModelAttributes modelAttributes = model.getAnnotation(ModelAttributes.class);
			if (!"".equals(modelAttributes.name())){
				name = modelAttributes.name();
			}
		}
		return name;
	}
	
}
