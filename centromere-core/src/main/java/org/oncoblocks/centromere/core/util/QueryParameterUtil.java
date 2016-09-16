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
import org.oncoblocks.centromere.core.repository.QueryParameterException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * TODO
 * @author woemler
 * @since 0.4.datatables-buttons
 */
public class QueryParameterUtil {

	/**
	 * Accepts an {@link Alias} annotation and a {@link Field} reference and returns a single 
	 *   {@link QueryParameterDescriptor} instance describing an acceptable query parameter.
	 * 
	 * @param alias annotation instance.
	 * @param field field reflection to which the annotation belongs.
	 * @return single query parameter descriptor object.
	 */
	public static QueryParameterDescriptor getDescriptorFromAlias(Alias alias, Field field){
		QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
		String fieldName = null;
		if (!alias.fieldName().equals("")){
			fieldName = alias.fieldName();
		} else if (field != null){
			fieldName = field.getName();
		} else {
			throw new QueryParameterException(String.format("No applicable parameter field name declared " 
					+ "for alias: %s", alias.toString()));
		}
		descriptor.setParamName(alias.value());
		descriptor.setFieldName(fieldName);
		descriptor.setType(alias.type() == Object.class ? getQueryableFieldType(field) : alias.type());
		descriptor.setEvaluation(alias.evaluation());
		return descriptor;
	}

	/**
	 * Accepts an {@link Aliases} annotation and a {@link Field} reference and returns all of the 
	 *   {@link QueryParameterDescriptor} instances associated with the embedded {@link Alias} 
	 *   annotations.  
	 * 
	 * @param aliases
	 * @param field
	 * @return
	 */
	public static List<QueryParameterDescriptor> getDescriptorsFromAliases(Aliases aliases, Field field){
		List<QueryParameterDescriptor> descriptors = new ArrayList<>();
		for (Alias alias: aliases.value()){
			QueryParameterDescriptor descriptor = getDescriptorFromAlias(alias, field);
			if (descriptor != null) descriptors.add(descriptor);
		}
		return descriptors;
	}

	public static List<QueryParameterDescriptor> getDescriptorsFromAliases(Aliases aliases){
		return getDescriptorsFromAliases(aliases, null);
	}

	/**
	 * Returns a map of {@link QueryParameterDescriptor} instances for models related to the given
	 *   {@link Model}, inferred by the presence of {@link ForeignKey} annotations.
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, QueryParameterDescriptor> getForeignKeyModelParameters(
			Class<? extends Model<?>> model){
		Map<String, QueryParameterDescriptor> map = new HashMap<>();
		for (Field field: model.getDeclaredFields()){
			if (field.isAnnotationPresent(ForeignKey.class)){
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				String relField = !"".equals(foreignKey.rel()) ? foreignKey.rel() : field.getName();
				Map<String,QueryParameterDescriptor> foreignModelMap = getModelQueryParameters(foreignKey.model());
				for(QueryParameterDescriptor descriptor: foreignModelMap.values()){
					String newParamName = relField + "." + descriptor.getParamName();
					descriptor.setParamName(newParamName);
					map.put(newParamName, descriptor);
				}
			}
		}
		return map;
	}

	/**
	 * Returns all valid query parameter descriptors from a given field, as defined by the default
	 *   field name and available {@link Alias} and {@link Aliases} annotations.  Ignores synthetic
	 *   fields.  Fields annotated with {@link Ignored} will not have the default parameter created,
	 *   but may have alias parameters created.
	 * 
	 * @param field
	 * @return
	 */
	public static List<QueryParameterDescriptor> getFieldDescriptors(Field field){
		List<QueryParameterDescriptor> descriptors = new ArrayList<>();
		if (field.isSynthetic()){
			return descriptors;
		}
		if (!field.isAnnotationPresent(Ignored.class)) {
			descriptors.add(new QueryParameterDescriptor(field.getName(), field.getName(),
					getQueryableFieldType(field), Evaluation.EQUALS, false));
		}
		if (field.isAnnotationPresent(Aliases.class)){
			descriptors.addAll(getDescriptorsFromAliases(field.getAnnotation(Aliases.class), field));
		} else if (field.isAnnotationPresent(Alias.class)){
			descriptors.add(getDescriptorFromAlias(field.getAnnotation(Alias.class), field));
		}
		return descriptors;
	}

	/**
	 * Returns the type of the model field that the parameter argument will be converted to from a
	 *   string.  If the model field is a collection, the parameterized type is used.
	 * 
	 * @param field
	 * @return
	 */
	public static Class<?> getQueryableFieldType(Field field){
		Class<?> type = field.getType();
		if (Collection.class.isAssignableFrom(field.getType())){
			ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
			type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		}
		return type;
	}

	/**
	 * Returns a mapping of all of a given {@link Model} class's available query parameters.
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, QueryParameterDescriptor> getModelQueryParameters(
			Class<? extends Model<?>> model){
		Map<String,QueryParameterDescriptor> paramMap = new HashMap<>();
		if (model.getClass().isAnnotationPresent(Aliases.class)){
			for (QueryParameterDescriptor descriptor: getDescriptorsFromAliases(model.getClass().getAnnotation(Aliases.class))){
				paramMap.put(descriptor.getParamName(), descriptor);
			}
		}
		for (Field field: model.getDeclaredFields()){
			for (QueryParameterDescriptor descriptor: getFieldDescriptors(field)){
				paramMap.put(descriptor.getParamName(), descriptor);
			}
		}
		return paramMap;
	}

	/**
	 * Returns all available query parameters for a given model, optionally including parameters of 
	 *   any related models, as inferred by the presence of {@link ForeignKey} annotations.
	 * 
	 * @param model
	 * @param useForeignKeyParams
	 * @return
	 */
	public static Map<String, QueryParameterDescriptor> getAvailableQueryParameters(
			Class<? extends Model<?>> model, boolean useForeignKeyParams){
		Map<String, QueryParameterDescriptor> descriptorMap = new HashMap<>();
		if (useForeignKeyParams){
			descriptorMap.putAll(getForeignKeyModelParameters(model));
		}
		descriptorMap.putAll(getModelQueryParameters(model));
		return descriptorMap;
	}

	public static Map<String, QueryParameterDescriptor> getAvailableQueryParameters(
			Class<? extends Model<?>> model){
		return getAvailableQueryParameters(model, false);
	}
	
}
