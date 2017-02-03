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

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.rest.core.annotation.RestResource;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;


/**
 * @author woemler
 */
public class QueryUtil {

//    public static QueryCriteria getCriteriaFromParameter(String parameter, Class<?> model){
//
//        PathBuilder<?> pathBuilder = new PathBuilder<>(model, model.getSimpleName());
//        Path root = Expressions.path(model, model.getSimpleName());
//
//        for (Field field: model.getDeclaredFields()){
//
//            String fieldName = field.getName();
//            Evaluation evaluation;
//            Class<?> type = field.getType();
//            Path path;
//            String mapKey = null;
//
//            // Skippable fields
//            if (field.isSynthetic()) continue;
//            if (field.isAnnotationPresent(RestResource.class)){
//                RestResource annotation = field.getAnnotation(RestResource.class);
//                if (!annotation.exported()) continue;
//            }
//
//            // Field name and parameter name match
//            if (parameter.equals(fieldName)){
//                evaluation = Evaluation.EQUALS;
//                path = Expressions.path(type, fieldName);
//            }
//            // Parameter name is field name with suffix
//            else if (parameterMatchesFieldEvaluation(parameter, fieldName)
//                    && isNonStandardEvaluation(parameter)){
//                evaluation = guessEvaluation(parameter);
//                if (parameter.contains(".")){
//                    parameter = parameter.split("\\.")[0];
//                    mapKey = parameter.split("\\.")[1];
//                    path = Expressions.path(type, parameter);
//                    if (Evaluation.getSuffix(evaluation) == null) continue;
//                }
//            } else {
//                continue;
//            }
//
//            // Determine the field type
//            TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(type);
//            System.out.print("type: " + typeDescriptor.getElementTypeDescriptor().getResolvableType().getType());
//
//            if (typeDescriptor.isMap()) {
//                Class<?> keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
//                TypeDescriptor mapValueDescriptor = typeDescriptor.getMapValueTypeDescriptor();
//                Class<?> valueType = mapValueDescriptor.isCollection()
//                        ? mapValueDescriptor.getElementTypeDescriptor().getType()
//                        : mapValueDescriptor.getType();
//                path = pathBuilder.getMap(fieldName, keyType, valueType);
//
//            } else if  (typeDescriptor.isCollection()) {
//                Class<?> valueType = typeDescriptor.getElementTypeDescriptor().getType();
//                path = pathBuilder.getList(fieldName, valueType);
//            } else {
//                if (type.isAssignableFrom(String.class)){
//                    path = pathBuilder.getString(fieldName);
//                } else if (type.isAssignableFrom(Number.class)){
//                    path = pathBuilder.getNumber(fieldName, (Class) type);
//                } else if (type.isAssignableFrom(Date.class)){
//                    path = pathBuilder.getDate(fieldName, (Class<? extends Date>) type);
//                } else if (type.isAssignableFrom(Boolean.class)){
//                    path = pathBuilder.getBoolean(fieldName);
//                } else {
//                    path = pathBuilder.getSimple(fieldName, type);
//                }
//            }
//
//            QueryCriteria queryCriteria = new QueryCriteria();
//            queryCriteria.setName(fieldName);
//            queryCriteria.setPath(path);
//            queryCriteria.setEvaluation(evaluation);
//            queryCriteria.setModel(model);
//            queryCriteria.setType(type);
//
//            return queryCriteria;
//
//        }
//
//        return null;
//    }
//
//
//
//    private static boolean isNonStandardEvaluation(String s){
//        return !guessEvaluation(s).equals(Evaluation.EQUALS);
//    }
//
//    private static boolean parameterMatchesFieldEvaluation(String param, String field){
//        Evaluation evaluation = guessEvaluation(param);
//        String suffix = Evaluation.getSuffix(evaluation);
//        return param.replaceFirst(suffix, "").equals(field);
//    }
//
//	private static Evaluation guessEvaluation(String name){
//		if (name.endsWith(Evaluation.EQUALS_SUFFIX)){
//			return Evaluation.EQUALS;
//		} else if (name.endsWith(Evaluation.NOT_IN_SUFFIX)){
//			return Evaluation.NOT_IN;
//		} else if (name.endsWith(Evaluation.IN_SUFFIX)){
//			return Evaluation.IN;
//		} else if (name.endsWith(Evaluation.NOT_LIKE_SUFFIX)){
//			return Evaluation.NOT_LIKE;
//		} else if (name.endsWith(Evaluation.LIKE_SUFFIX)){
//			return Evaluation.LIKE;
//		} else if (name.endsWith(Evaluation.STARTS_WITH_SUFFIX)){
//			return Evaluation.STARTS_WITH;
//		} else if (name.endsWith(Evaluation.ENDS_WITH_SUFFIX)){
//			return Evaluation.ENDS_WITH;
//		} else if (name.endsWith(Evaluation.GREATER_THAN_EQUALS_SUFFIX)){
//			return Evaluation.GREATER_THAN_EQUALS;
//		} else if (name.endsWith(Evaluation.GREATER_THAN_SUFFIX)){
//			return Evaluation.GREATER_THAN;
//		} else if (name.endsWith(Evaluation.LESS_THAN_EQUALS_SUFFIX)){
//			return Evaluation.LESS_THAN_EQUALS;
//		} else if (name.endsWith(Evaluation.LESS_THAN_SUFFIX)){
//			return Evaluation.LESS_THAN;
//		} else if (name.endsWith(Evaluation.BETWEEN_SUFFIX)){
//			return Evaluation.BETWEEN;
//		} else if (name.endsWith(Evaluation.BETWEEN_INCLUSIVE_SUFFIX)){
//			return Evaluation.BETWEEN_INCLUSIVE;
//		} else if (name.endsWith(Evaluation.OUTSIDE_SUFFIX)){
//			return Evaluation.OUTSIDE;
//		} else if (name.endsWith(Evaluation.OUTSIDE_INCLUSIVE_SUFFIX)){
//			return Evaluation.OUTSIDE_INCLUSIVE;
//		} else if (name.endsWith(Evaluation.IS_NULL_SUFFIX)){
//			return Evaluation.IS_NULL;
//		} else if (name.endsWith(Evaluation.NOT_NULL_SUFFIX)){
//			return Evaluation.NOT_NULL;
//		} else if (name.endsWith(Evaluation.IS_TRUE_SUFFIX)){
//			return Evaluation.IS_TRUE;
//		} else if (name.endsWith(Evaluation.IS_FALSE_SUFFIX)){
//			return Evaluation.IS_FALSE;
//		} else {
//			return Evaluation.EQUALS;
//		}
//	}
	
	
	
}
