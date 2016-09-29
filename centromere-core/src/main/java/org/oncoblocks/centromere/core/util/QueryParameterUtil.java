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

import org.oncoblocks.centromere.core.model.*;
import org.oncoblocks.centromere.core.repository.Evaluation;
import org.oncoblocks.centromere.core.repository.QueryParameterDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for working with {@link org.oncoblocks.centromere.core.repository.QueryCriteria} and for
 *   generating {@link QueryParameterDescriptor} objects describing valid query parameters for 
 *   {@link Model} classes.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class QueryParameterUtil {

	/**
	 * Inspects a {@link Model} class and returns all of the available and acceptable query parameter
	 *   definitions, as a map of parameter names and {@link QueryParameterDescriptor} objects.
	 *
	 * @param model model to inspect
	 * @return map of parameter names and their descriptors
	 */
	public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(
			Class<? extends Model<?>> model, boolean recursive)
	{
		Class<?> current = model;
		Map<String,QueryParameterDescriptor> paramMap = new HashMap<>();
		while (current.getSuperclass() != null) {
			for (Field field : current.getDeclaredFields()) {
				String fieldName = field.getName();
				Class<?> type = field.getType();
				if (Collection.class.isAssignableFrom(field.getType())) {
					ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
					type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
				}
				if (field.isSynthetic()) continue;
				if (!field.isAnnotationPresent(Ignored.class)) {
					paramMap.put(fieldName,
							new QueryParameterDescriptor(fieldName, fieldName, type, Evaluation.EQUALS, false, true));
				}
				if (field.isAnnotationPresent(ForeignKey.class)) {
					if (!recursive)
						continue;
					ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
					String relField = !"".equals(foreignKey.rel()) ? foreignKey.rel() : fieldName;
					Map<String, QueryParameterDescriptor> foreignModelMap =
							getAvailableQueryParameters(foreignKey.model(), false);
					for (QueryParameterDescriptor descriptor : foreignModelMap.values()) {
						String newParamName = relField + "." + descriptor.getParamName();
						descriptor.setParamName(newParamName);
						paramMap.put(newParamName, descriptor);
					}
				}
				if (field.isAnnotationPresent(Aliases.class)) {
					Aliases aliases = field.getAnnotation(Aliases.class);
					for (Alias alias : aliases.value()) {
						paramMap.put(alias.value(), getDescriptorFromAlias(alias, type, fieldName));
					}
				} else if (field.isAnnotationPresent(Alias.class)) {
					Alias alias = field.getAnnotation(Alias.class);
					paramMap.put(alias.value(), getDescriptorFromAlias(alias, type, fieldName));
				}
			}
			current = current.getSuperclass();
		}
		return paramMap;
	}

	/**
	 * Inspects a {@link Model} class and returns all of the available and acceptable query parameter
	 *   definitions, as a map of parameter names and {@link QueryParameterDescriptor} objects.
	 *
	 * @param model model to inspect
	 * @return map of parameter names and their descriptors
	 */
	public static Map<String,QueryParameterDescriptor> getAvailableQueryParameters(Class<? extends Model<?>> model) {
		return getAvailableQueryParameters(model, true);
	}


	/**
	 * Constructs a {@link QueryParameterDescriptor} from an instance of an {@link Alias}, reference 
	 *   to a field type, and its name.
	 *
	 * @param alias alias annotation
	 * @param type type of the field to be queried
	 * @param fieldName name of the field
	 * @return description of the acceptable query parameter.
	 */
	public static QueryParameterDescriptor getDescriptorFromAlias(Alias alias, Class<?> type, String fieldName){
		QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
		descriptor.setParamName(alias.value());
		descriptor.setFieldName(alias.fieldName().equals("") ? fieldName : alias.fieldName());
		descriptor.setRegexMatch(alias.regex());
		descriptor.setType(type);
		descriptor.setEvaluation(alias.evaluation());
		descriptor.setDynaimicParameters(alias.dynamic());
		return descriptor;
	}
	
}
