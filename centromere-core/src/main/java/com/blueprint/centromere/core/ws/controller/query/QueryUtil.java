/*
 * Copyright 2017 the original author or authors
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

package com.blueprint.centromere.core.ws.controller.query;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.reflect.TypeResolver;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.rest.core.annotation.RestResource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author woemler
 */
public class QueryUtil {
	
	public static QueryPath getQueryPathFromFieldName(String name, Map<String, QueryPath> map){
		if (map.containsKey(name)) return map.get(name);
		Evaluation evaluation = Evaluation.guessEvaluation(name);
		if (!evaluation.equals(Evaluation.EQUALS)){
			return map.get(name.replace(Evaluation.getSuffix(evaluation), ""));
		} else {
			return null;
		}
	}
	
	public static Map<String, QueryPath> getModelQueryPaths(Class<?> model){
		Map<String, QueryPath> pathMap = new HashMap<>();
		for (Field field: model.getDeclaredFields()){

			Path root = Expressions.path(model, model.getSimpleName());
			Class<?> type = field.getType();
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(type);
			String name = field.getName();
			QueryPath queryPath = null;
			
			// Skippable fields
			if (field.isSynthetic()) continue;
			if (field.isAnnotationPresent(RestResource.class)){
				RestResource annotation = field.getAnnotation(RestResource.class);
				if (!annotation.exported()) continue;
			}

			
			
			if (typeDescriptor.isMap()) {
				
			} else if  (typeDescriptor.isCollection()) {
				
			} else {
				if (type.isAssignableFrom(String.class)){
					StringPath path = Expressions.stringPath(root, name);
					queryPath = new QueryPath(name, path, type, model);
					
				}
			}
			
			if (queryPath != null){
				pathMap.put(name, queryPath);
			}
			
		}
		
		
	}
	
	
	
}
