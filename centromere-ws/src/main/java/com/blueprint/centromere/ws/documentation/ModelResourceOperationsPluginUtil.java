package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.exception.RestError;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.springframework.http.HttpMethod;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Operation;
import springfox.documentation.service.ResponseMessage;

/**
 * @author woemler
 * @since 0.4.1
 */
final class ModelResourceOperationsPluginUtil {

    private ModelResourceOperationsPluginUtil() {
    }

    /* API Operations */

    /**
     * Collects all of the {@link Operation} objects for each single-record API endpoint.
     */
    static List<Operation> findSingleModelOperations(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Operation> operations = new ArrayList<>();
        operations.add(findByIdOperation(model, typeResolver));
        operations.add(putOperation(model, typeResolver));
        operations.add(deleteOperation(model, typeResolver));
        operations.add(optionsOperation(model));
        operations.add(headOperation(model));
        return operations;
    }

    /**
     * Collects all of the {@link Operation} objects for all of the multiple-record API endpoints.
     */
    static List<Operation> findCollectionModelOperations(
        Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Operation> operations = new ArrayList<>();
        operations.add(findAllOperation(model, typeResolver));
        operations.add(postOperation(model, typeResolver));
        operations.add(optionsOperation(model));
        operations.add(headOperation(model));
        return operations;
    }
    
    static List<Operation> findDistinctOperations(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Operation> operations = new ArrayList<>();
        operations.add(findDistinctOperation(model, typeResolver));
        return operations;
    }

    static List<Operation> countOperations(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Operation> operations = new ArrayList<>();
        operations.add(countOperation(model, typeResolver));
        return operations;
    }

    static List<Operation> findGroupedOperations(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        List<Operation> operations = new ArrayList<>();
        operations.add(findGroupedOperation(model, typeResolver));
        return operations;
    }

    /**
     * {@code GET /{id}}
     */
    private static Operation findByIdOperation(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.GET,
            "Fetch a single record by ID",
            "Fetches a single record by the primary key ID.  If no record is found, a 404 error is returned.",
            new ModelRef(model.getSimpleName()),
            "FindById",
            0,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.findByIdParameters(model, typeResolver),
            new HashSet<>(standardGetResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code GET /}
     */
    private static Operation findAllOperation(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.GET,
            "Fetch all records",
            "Fetches one or more records.  Can be filtered, paged, and sorted.",
            new ModelRef(List.class.getSimpleName(), new ModelRef(model.getSimpleName())),
            "FindAll",
            1,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.findAllParameters(model, typeResolver),
            new HashSet<>(standardGetResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code POST /}
     */
    private static Operation postOperation(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.POST,
            "Create new record",
            "Attempts to create a new record using the submitted object",
            new ModelRef(model.getSimpleName()),
            "Create",
            2,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.postParameters(model, typeResolver),
            new HashSet<>(writeResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code PUT /{id}}
     */
    private static Operation putOperation(Class<? extends Model<?>> model,
        TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.PUT,
            "Update an existing record",
            "Attempts to update an existing record using the submitted object",
            new ModelRef(model.getSimpleName()),
            "Update",
            3,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.putParameters(model, typeResolver),
            new HashSet<>(writeResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code DELETE /{id}}
     */
    private static Operation deleteOperation(Class<? extends Model> model,
        TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.DELETE,
            "Delete an existing record",
            "Attempts to delete an existing record, identified by the submitted ID.",
            new ModelRef(String.class.getSimpleName()),
            "Delete",
            4,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.deleteParameters(model, typeResolver),
            new HashSet<>(writeResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code HEAD /**}
     */
    private static Operation headOperation(Class<? extends Model> model) {
        return new Operation(
            HttpMethod.HEAD,
            "Get endpoint headers",
            "Retrieves only endpoint headers.",
            new ModelRef(String.class.getSimpleName()),
            "Head",
            5,
            Collections.singleton(model.getSimpleName()),
            Collections.singleton("*/*"),
            Collections.singleton("*/*"),
            Collections.emptySet(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>(infoResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code OPTIONS /**}
     */
    private static Operation optionsOperation(Class<? extends Model> model) {
        return new Operation(
            HttpMethod.OPTIONS,
            "Get endpoint information",
            "Retrieves endpoint information.",
            new ModelRef(String.class.getSimpleName()),
            "Options",
            6,
            Collections.singleton(model.getSimpleName()),
            Collections.singleton("*/*"),
            Collections.singleton("*/*"),
            Collections.emptySet(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>(infoResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code GET /distinct/{field}}
     */
    private static Operation findDistinctOperation(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.GET,
            "Fetch distinct field values",
            "Fetches all unique values of the requested model field.",
            new ModelRef(List.class.getSimpleName(), new ModelRef(String.class.getSimpleName())),
            "FindDistinct",
            7,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.findDistinctParameters(model, typeResolver),
            new HashSet<>(standardGetResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code GET /count}
     */
    private static Operation countOperation(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.GET,
            "Counts records",
            "Counts all records of the requested model field that fulfill the query criteria.",
            new ModelRef(List.class.getSimpleName(), new ModelRef(String.class.getSimpleName())),
            "Count",
            8,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.countParameters(model, typeResolver),
            new HashSet<>(standardGetResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    /**
     * {@code GET /group/{field}}
     */
    private static Operation findGroupedOperation(
        Class<? extends Model<?>> model, TypeResolver typeResolver) {
        return new Operation(
            HttpMethod.GET,
            "Groups records",
            "Groups all values of the requested resources and their full resource objects into a single collection",
            new ModelRef(List.class.getSimpleName(), new ModelRef(String.class.getSimpleName())),
            "Count",
            9,
            Collections.singleton(model.getSimpleName()),
            new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
            new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
            Collections.emptySet(),
            new ArrayList<>(),
            ModelResourceParametersPluginUtil.findGroupedParameters(model, typeResolver),
            new HashSet<>(standardGetResponseMessages(model)),
            null,
            false,
            new ArrayList<>()
        );
    }

    //// Response codes

    private static List<ResponseMessage> standardGetResponseMessages(Class<?> responseModel) {
        List<ResponseMessage> messages = new ArrayList<>();
        messages.add(new ResponseMessage(200, "OK", new ModelRef(responseModel.getSimpleName()),
            Collections.emptyMap(), new ArrayList<>()));
        messages.add(new ResponseMessage(400, "Invalid options",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        messages.add(new ResponseMessage(401, "Unauthorized",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        messages.add(new ResponseMessage(404, "Record not found",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        return messages;
    }

    private static List<ResponseMessage> writeResponseMessages(Class<?> responseModel) {
        List<ResponseMessage> messages = new ArrayList<>();
        messages
            .add(new ResponseMessage(201, "Created", new ModelRef(responseModel.getSimpleName()),
                Collections.emptyMap(), new ArrayList<>()));
        messages.add(new ResponseMessage(401, "Unauthorized",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        messages.add(new ResponseMessage(403, "Forbidden",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        messages.add(new ResponseMessage(404, "Record not found",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        return messages;
    }

    private static List<ResponseMessage> infoResponseMessages(Class<?> responseModel) {
        List<ResponseMessage> messages = new ArrayList<>();
        messages
            .add(new ResponseMessage(204, "No Content", new ModelRef(responseModel.getSimpleName()),
                Collections.emptyMap(), new ArrayList<>()));
        messages.add(new ResponseMessage(401, "Unauthorized",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        messages.add(new ResponseMessage(403, "Forbidden",
            new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(),
            new ArrayList<>()));
        return messages;
    }

}
