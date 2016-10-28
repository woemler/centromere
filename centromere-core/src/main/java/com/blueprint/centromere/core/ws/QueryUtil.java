/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.ws;

import com.blueprint.centromere.core.repository.Evaluation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.rest.core.annotation.RestResource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
public class QueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(QueryUtil.class);

    public static <T> QueryParameterDescriptors getAvailableQueryParameters(Class<T> model) {

        logger.debug(String.format("Determining available query parameters for model: %s", model.getName()));
        PathBuilder<T> pathBuilder = new PathBuilder<>(model, model.getSimpleName().toLowerCase());
        Class<?> current = model;
        QueryParameterDescriptors descriptors = new QueryParameterDescriptors();

        while (current.getSuperclass() != null) {
            for (Field field : current.getDeclaredFields()) {

                String fieldName = field.getName();
                TypeDescriptor type = TypeDescriptor.valueOf(field.getType());
                RestResource restResource = null;

                if (field.isSynthetic()) continue;

                if (AnnotatedElementUtils.hasAnnotation(field, RestResource.class)){
                    restResource = AnnotatedElementUtils.getMergedAnnotation(field, RestResource.class);
                }

                if (restResource != null && !restResource.exported()) continue;

                // String type
                if (type.getObjectType().equals(String.class)){
                    StringPath stringPath = pathBuilder.getString(fieldName);
                    descriptors.add(new QueryParameterDescriptor(fieldName, stringPath, type,
                                    Ops.EQ_IGNORE_CASE));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.ENDS_WITH_SUFFIX,
                                    stringPath, type, Ops.ENDS_WITH_IC));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.STARTS_WITH_SUFFIX,
                                    stringPath, type, Ops.STARTS_WITH_IC));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.LIKE_SUFFIX,
                                    stringPath, type, Ops.LIKE_IC));
                }
                // Numeric type
                else if (type.isAssignableTo(TypeDescriptor.valueOf(Number.class))){
                    Path numberPath = pathBuilder.get(fieldName, type.getObjectType());
                    descriptors.add(new QueryParameterDescriptor(fieldName,
                            numberPath, type, Ops.EQ));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.GREATER_THAN_SUFFIX,
                                    numberPath, type, Ops.GT));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.GREATER_THAN_EQUALS_SUFFIX,
                                    numberPath, type, Ops.GOE));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.LESS_THAN_SUFFIX,
                                    numberPath, type, Ops.LT));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.LESS_THAN_EQUALS,
                                    numberPath, type, Ops.LOE));
                    descriptors.add(new QueryParameterDescriptor(fieldName + Evaluation.BETWEEN_SUFFIX,
                                    numberPath, type, Ops.BETWEEN));
                }
                // List
                else if (type.isCollection()){
                    ListPath listPath = pathBuilder.getList(fieldName, 
                            type.getElementTypeDescriptor().getObjectType());
                    descriptors.add(new QueryParameterDescriptor(fieldName, listPath,
                            type.getElementTypeDescriptor(), Ops.EQ));
                }
                // Map
                else if (type.isMap()){
                    MapPath mapPath = pathBuilder.getMap(fieldName, 
                            type.getMapKeyTypeDescriptor().getObjectType(), 
                            type.getMapValueTypeDescriptor().getObjectType());
                    descriptors.add(new QueryParameterDescriptor(fieldName + "\\.\\w+", mapPath,
                            type, Ops.EQ, true));
                }
                else {
                    Path objPath = pathBuilder.get(fieldName, type.getObjectType());
                    descriptors.add(new QueryParameterDescriptor(fieldName, objPath, type, Ops.EQ));
                }
//
            }
            current = current.getSuperclass();
        }
        logger.debug(String.format("Found %d query parameters for model: %s",
                descriptors.getDescriptors().size(), model.getName()));
        return descriptors;
    }
    
    public static Predicate getParameterPredicate(String name, String value,
                                                  QueryParameterDescriptor descriptor,
                                                  ConversionService conversionService){
        Predicate predicate = null;
        // determine if value is single object or array
        // determine type to convert value to
        // convert value
        // determine type of path in descriptor
        // create predicate based on path and value type
        return predicate;
    }

    public static Predicate getParameterPredicate(String name, String value,
                                                  QueryParameterDescriptor descriptor) {
        return getParameterPredicate(name, value, descriptor, new DefaultConversionService());
    }

    /**
     * Converts an object into the appropriate type defined by the model field being queried.
     *
     * @param param
     * @param type
     * @return
     */
    private static Object convertParameter(Object param, Class<?> type, ConversionService conversionService){
        if (conversionService.canConvert(param.getClass(), type)){
            try {
                return conversionService.convert(param, type);
            } catch (ConversionFailedException e){
                e.printStackTrace();
                throw new RuntimeException("Unable to convert parameter string to " + type.getName());
            }
        } else {
            return param;
        }
    }

    /**
     * {@link QueryUtil#convertParameter(Object, Class, ConversionService)}
     */
    private static Object convertParameter(Object param, Class<?> type){
        ConversionService conversionService = new DefaultConversionService();
        return convertParameter(param, type, conversionService);
    }

    /**
     * Converts an array of objects into the appropriate type defined by the model field being queried
     *
     * @param params
     * @param type
     * @return
     */
    private static List<Object> convertParameterArray(Object[] params, Class<?> type){
        List<Object> objects = new ArrayList<>();
        for (Object param: params){
            objects.add(convertParameter(param, type));
        }
        return objects;
    }

}
