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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.blueprint.centromere.core.repository.Evaluation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
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
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for generating repository queries from web service query requests.
 *
 * @author woemler
 */
@Deprecated
public class QueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(QueryUtil.class);
    private static final List<Ops> collectionParamOps = Arrays.asList(Ops.EQ, Ops.NE,
            Ops.EQ_IGNORE_CASE, Ops.IN, Ops.NOT_IN, Ops.BETWEEN);

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

            }
            current = current.getSuperclass();
        }
        logger.debug(String.format("Found %d query parameters for model: %s",
                descriptors.getDescriptors().size(), model.getName()));
        return descriptors;
    }

    /**
     * Creates a {@link Predicate} from a submitted query string parameter and its associated
     *   {@link QueryParameterDescriptor}. Uses submitted {@link ConversionService}.
     *
     * @param name
     * @param value
     * @param descriptor
     * @param conversionService
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Predicate getParameterPredicate(String name, String value,
                                                  QueryParameterDescriptor descriptor,
                                                  ConversionService conversionService){

        Predicate predicate = null;

        TypeDescriptor targetType = null;
        if (descriptor.getType().isCollection()){
            targetType = descriptor.getType().getElementTypeDescriptor();
        } else if (descriptor.getType().isMap()){
            targetType = descriptor.getType().getMapValueTypeDescriptor();
        } else {
            targetType = descriptor.getType();
        }

        // Multiple values submitted
        if (value.contains(",")){

            List<Object> params = new ArrayList<>();
            for (String s: Splitter.on(",").trimResults().omitEmptyStrings().split(value)){
                params.add(convertParameter(s, targetType, conversionService));
            }

            if (!collectionParamOps.contains(descriptor.getOperation())){
                throw new QueryParameterException(String.format("Query parameter mismatch.  Target" +
                        " query operation %s does not accept multiple values are arguments: %s",
                        descriptor.getOperation().toString(), value));
            }

            Ops operation = descriptor.getOperation();
            if (operation.equals(Ops.EQ) || operation.equals(Ops.EQ_IGNORE_CASE)) operation = Ops.IN;
            if (operation.equals(Ops.NE)) operation = Ops.NOT_IN;

            if (descriptor.getType().isMap() && descriptor.getPath() instanceof MapPath){
                predicate = getMapPathPredicate(name, params, operation, (MapPath) descriptor.getPath());
            } else if (descriptor.getType().isCollection() && descriptor.getPath() instanceof ListPath){
                predicate = Expressions.predicate(operation, ((ListPath) descriptor.getPath()).any(),
                        Expressions.constant(params));
            } else {
                predicate = Expressions.predicate(operation, descriptor.getPath(),
                        Expressions.constant(params));
            }

        }
        // single value
        else {
            Object param = convertParameter(value, targetType, conversionService);
            if (descriptor.getType().isMap() && descriptor.getPath() instanceof MapPath){
                predicate = getMapPathPredicate(name, param, descriptor.getOperation(),
                        (MapPath) descriptor.getPath());
            } else if (descriptor.getType().isCollection() && descriptor.getPath() instanceof ListPath){
                predicate = Expressions.predicate(descriptor.getOperation(),
                        ((ListPath) descriptor.getPath()).any(), Expressions.constant(param));
            } else {
                predicate = Expressions.predicate(descriptor.getOperation(), descriptor.getPath(),
                        Expressions.constant(param));
            }
        }

        return predicate;
    }

    /**
     * Creates a {@link Predicate} from a submitted query string parameter and its associated
     *   {@link QueryParameterDescriptor}.  Uses new {@link DefaultConversionService}.
     *
     * @param name
     * @param value
     * @param descriptor
     * @return
     */
    public static Predicate getParameterPredicate(String name, String value,
                                                  QueryParameterDescriptor descriptor) {
        return getParameterPredicate(name, value, descriptor, new DefaultConversionService());
    }

    private static Predicate getMapPathPredicate(String name, Object value, Ops operation,
                                                 MapPath<String,?,?> path){
        List<String> b = Lists.newArrayList(
                Splitter.on(".").trimResults().omitEmptyStrings().split(name));
        String key = b.get(b.size()-1);
        return Expressions.predicate(operation, path.get(key), Expressions.constant(value));
    }

    /**
     * Converts an object into the appropriate type defined by the model field being queried.
     *
     * @param param
     * @param type
     * @return
     */
    private static Object convertParameter(String param, TypeDescriptor type, ConversionService conversionService){
        if (conversionService.canConvert(TypeDescriptor.valueOf(String.class), type)){
            try {
                return conversionService.convert(param, TypeDescriptor.valueOf(String.class), type);
            } catch (ConversionFailedException e){
                e.printStackTrace();
                throw new RuntimeException("Unable to convert parameter string to " + type.getName());
            }
        } else {
            return param;
        }
    }

    /**
     * {@link #convertParameter(String, TypeDescriptor, ConversionService)}  }
     */
    private static Object convertParameter(String param, TypeDescriptor type){
        ConversionService conversionService = new DefaultConversionService();
        return convertParameter(param, type, conversionService);
    }

}
