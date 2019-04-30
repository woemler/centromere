package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.repository.QueryParameterUtil;
import com.blueprint.centromere.ws.controller.ReservedRequestParameters;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;

/**
 * @author woemler
 */
final class ModelResourceParametersPluginUtil {

    private ModelResourceParametersPluginUtil() {
    }

    private static List<Parameter> getModelParameters(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        return QueryParameterUtil.getAvailableQueryParameters(model, false).values().stream()
            .map(descriptor -> createParameterFromDescriptior(descriptor, typeResolver))
            .collect(Collectors.toList());
    }

    private static Parameter createParameterFromDescriptior(
        QueryParameterDescriptor descriptor, TypeResolver typeResolver) {
        return new ParameterBuilder()
            .name(descriptor.getParamName())
            .type(typeResolver.resolve(typeResolver.resolve(descriptor.getType())))
            .modelRef(new ModelRef("string"))
            .parameterType("query")
            .required(false)
            .description(String.format("Query against the '%s' field.", descriptor.getFieldName()))
            .defaultValue("")
            .allowMultiple(false)
            .parameterAccess("")
            .build();
    }

    private static List<Parameter> formatParameters(TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
            .name(ReservedRequestParameters.FORMAT_PARAMETER)
            .type(typeResolver.resolve(String.class))
            .modelRef(new ModelRef("string"))
            .parameterType("query")
            .required(false)
            .description("Requested response format (eg. json, xml, or txt).")
            .defaultValue("json")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        return parameters;
    }

    private static List<Parameter> paginationParameters(TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
            .name(ReservedRequestParameters.SIZE_PARAMETER)
            .type(typeResolver.resolve(Integer.class))
            .modelRef(new ModelRef("int"))
            .parameterType("query")
            .required(false)
            .description("Number of records to be included in a page.")
            .defaultValue("1000")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.add(new ParameterBuilder()
            .name(ReservedRequestParameters.PAGE_PARAMETER)
            .type(typeResolver.resolve(Integer.class))
            .modelRef(new ModelRef("int"))
            .parameterType("query")
            .required(false)
            .description("Page number to return.  Starts at 0.")
            .defaultValue("0")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.add(new ParameterBuilder()
            .name(ReservedRequestParameters.SORT_PARAMETER)
            .type(typeResolver.resolve(String.class))
            .modelRef(new ModelRef("string"))
            .parameterType("query")
            .required(false)
            .description("Sort order field and direction (eg. 'sort=name,desc')")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        return parameters;
    }

    private static List<Parameter> fieldFilteringParameters(TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
            .name(ReservedRequestParameters.INCLUDED_FIELDS_PARAMETER)
            .type(typeResolver.resolve(String.class))
            .modelRef(new ModelRef("string"))
            .parameterType("query")
            .required(false)
            .description("List of fields to be included in response objects.")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.add(new ParameterBuilder()
            .name(ReservedRequestParameters.EXCLUDED_FIELDS_PARAMETER)
            .type(typeResolver.resolve(String.class))
            .modelRef(new ModelRef("string"))
            .parameterType("query")
            .required(false)
            .description("List of fields to be excluded from response objects.")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        return parameters;
    }

    /**
     * GET /api/search/{model}
     */
    static List<Parameter> findAllParameters(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.addAll(paginationParameters(typeResolver));
        parameters.addAll(fieldFilteringParameters(typeResolver));
        parameters.addAll(formatParameters(typeResolver));
        parameters.addAll(getModelParameters(model, typeResolver));
        return parameters;
    }

    /**
     * GET /api/search/{model}/{id}
     */
    static List<Parameter> findByIdParameters(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        try {
            parameters.add(new ParameterBuilder()
                .name("id")
                .type(typeResolver.resolve(model.getMethod("getId").getReturnType()))
                .modelRef(new ModelRef("string"))
                .parameterType("path")
                .required(true)
                .description("List of fields to be included in response objects.")
                .allowMultiple(false)
                .parameterAccess("")
                .build());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        parameters.addAll(fieldFilteringParameters(typeResolver));
        parameters.addAll(formatParameters(typeResolver));
        return parameters;
    }

    /**
     * GET /api/aggregate/{model}/distinct/{field}
     */
    static List<Parameter> findDistinctParameters(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
            .name("field")
            .type(typeResolver.resolve(String.class))
            .modelRef(new ModelRef("string"))
            .parameterType("path")
            .required(true)
            .description("Field to return distinct values of.")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.addAll(getModelParameters(model, typeResolver));
        parameters.addAll(formatParameters(typeResolver));
        return parameters;
    }

    /**
     * GET /api/aggregate/{model}/group/{field}
     */
    static List<Parameter> findGroupedParameters(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
            .name("field")
            .type(typeResolver.resolve(String.class))
            .modelRef(new ModelRef("string"))
            .parameterType("path")
            .required(true)
            .description("Field to return distinct values of.")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.addAll(getModelParameters(model, typeResolver));
        parameters.addAll(formatParameters(typeResolver));
        return parameters;
    }

    /**
     * GET /api/aggregate/{model}/count
     */
    static List<Parameter> countParameters(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.addAll(formatParameters(typeResolver));
        parameters.addAll(getModelParameters(model, typeResolver));
        return parameters;
    }

    /**
     * POST /api/search/{model}
     */
    static List<Parameter> postParameters(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
            .name("entity")
            .type(typeResolver.resolve(model))
            .modelRef(new ModelRef(model.getName()))
            .parameterType("body")
            .required(true)
            .description("New record body.")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.addAll(formatParameters(typeResolver));
        return parameters;
    }

    /**
     * PUT /api/search/{model}/{id}
     */
    static List<Parameter> putParameters(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        try {
            parameters.add(new ParameterBuilder()
                .name("id")
                .type(typeResolver.resolve(model.getMethod("getId").getReturnType()))
                .modelRef(new ModelRef("string"))
                .parameterType("path")
                .required(true)
                .description("Primary ID")
                .allowMultiple(false)
                .parameterAccess("")
                .build());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        parameters.add(new ParameterBuilder()
            .name("entity")
            .type(typeResolver.resolve(model))
            .modelRef(new ModelRef(model.getName()))
            .parameterType("body")
            .required(true)
            .description("New record body.")
            .allowMultiple(false)
            .parameterAccess("")
            .build());
        parameters.addAll(formatParameters(typeResolver));
        return parameters;
    }

    /**
     * DELETE /api/search/{model}/{id}
     */
    static List<Parameter> deleteParameters(Class<? extends Model> model,
        TypeResolver typeResolver) {
        List<Parameter> parameters = new ArrayList<>();
        try {
            parameters.add(new ParameterBuilder()
                .name("id")
                .type(typeResolver.resolve(model.getMethod("getId").getReturnType()))
                .modelRef(new ModelRef("string"))
                .parameterType("path")
                .required(true)
                .description("Primary ID")
                .allowMultiple(false)
                .parameterAccess("")
                .build());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        parameters.addAll(formatParameters(typeResolver));
        return parameters;
    }

}
