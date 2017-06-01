package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.repository.QueryParameterUtil;
import com.blueprint.centromere.ws.config.ApiMediaTypes;
import com.blueprint.centromere.ws.exception.RestError;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;

/**
 * @author woemler
 * @since 0.4.1
 */
public class SwaggerPluginUtil {
	
	/* API Descriptions */

  public static List<ApiDescription> getModelApiDescriptions(
      Class<? extends Model> model,
      TypeResolver typeResolver,
      String rootUrl
  ){
    List<ApiDescription> descriptions = new ArrayList<>();
    descriptions.add(
        new ApiDescription(
            rootUrl + "/{id}",
            model.getSimpleName() + " single resource operations",
            getFindSingleModelOperations(model, typeResolver),
            false));
    descriptions.add(
        new ApiDescription(
            rootUrl,
            model.getSimpleName() + " collection resource operations",
            getFindCollectionModelOperations(model, typeResolver),
            false));
    descriptions.add(
        new ApiDescription(
            rootUrl + "/search/distinct",
            model.getSimpleName() + " collection resource operations",
            getFindDistinctOperations(model, typeResolver),
            false));
    //TODO: add other search endpoints programatically
    return descriptions;
  }
	
	/* API Operations */

  private static List<Operation> getFindSingleModelOperations(
      Class<? extends Model> model, TypeResolver typeResolver){
    List<Operation> operations = new ArrayList<>();
    operations.add(getFindByIdOperation(model, typeResolver));
    operations.add(getPutOperation(model, typeResolver));
    operations.add(getDeleteOperation(model, typeResolver));
    operations.add(getOptionsOperation(model));
    operations.add(getHeadOperation(model));
    return operations;
  }

  private static List<Operation> getFindCollectionModelOperations(
      Class<? extends Model> model, TypeResolver typeResolver){
    List<Operation> operations = new ArrayList<>();
    operations.add(getFindAllOperation(model, typeResolver));
    operations.add(getPostOperation(model, typeResolver));
    operations.add(getOptionsOperation(model));
    operations.add(getHeadOperation(model));
    return operations;
  }

  private static List<Operation> getFindDistinctOperations(
      Class<? extends Model> model, TypeResolver typeResolver){
    List<Operation> operations = new ArrayList<>();
    operations.add(getFindDistinctOperation(model, typeResolver));
    return operations;
  }

  /**
   * {@code GET /{id}}
   *
   * @param model
   * @param typeResolver
   * @return
   */
  private static Operation getFindByIdOperation(
      Class<? extends Model> model, TypeResolver typeResolver){
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
        findByIdParameters(model, typeResolver),
        new HashSet<>(getStandardGetResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code GET /}
   *
   * @param model
   * @param typeResolver
   * @return
   */
  private static Operation getFindAllOperation(
      Class<? extends Model> model, TypeResolver typeResolver){
    return new Operation(
        HttpMethod.GET,
        "Fetch all records",
        "Fetches one or more records.  Can be filtered, paged, and sorted.",
        new ModelRef(ArrayList.class.getSimpleName(), new ModelRef(model.getSimpleName())),
        "FindAll",
        1,
        Collections.singleton(model.getSimpleName()),
        new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
        new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
        Collections.emptySet(),
        new ArrayList<>(),
        findAllParameters(model, typeResolver),
        new HashSet<>(getStandardGetResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code GET /distinct}
   *
   * @param model
   * @param typeResolver
   * @return
   */
  private static Operation getFindDistinctOperation(
      Class<? extends Model> model, TypeResolver typeResolver){
    return new Operation(
        HttpMethod.GET,
        "Fetch distinct field values",
        "Fetches all unique values of the requested model field.",
        new ModelRef(ArrayList.class.getSimpleName(), new ModelRef(String.class.getSimpleName())),
        "FindDistinct",
        2,
        Collections.singleton(model.getSimpleName()),
        new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
        new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
        Collections.emptySet(),
        new ArrayList<>(),
        findDistinctParameters(typeResolver),
        new HashSet<>(getStandardGetResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code POST /}
   *
   * @param model
   * @param typeResolver
   * @return
   */
  private static Operation getPostOperation(Class<? extends Model> model, TypeResolver typeResolver){
    return new Operation(
        HttpMethod.POST,
        "Create new record",
        "Attempts to create a new record using the submitted object",
        new ModelRef(model.getSimpleName()),
        "Create",
        3,
        Collections.singleton(model.getSimpleName()),
        new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
        new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
        Collections.emptySet(),
        new ArrayList<>(),
        postParameters(model, typeResolver),
        new HashSet<>(getWriteResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code PUT /{id}}
   *
   * @param model
   * @param typeResolver
   * @return
   */
  private static Operation getPutOperation(Class<? extends Model> model, TypeResolver typeResolver){
    return new Operation(
        HttpMethod.PUT,
        "Update an existing record",
        "Attempts to update an existing record using the submitted object",
        new ModelRef(model.getSimpleName()),
        "Update",
        4,
        Collections.singleton(model.getSimpleName()),
        new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
        new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
        Collections.emptySet(),
        new ArrayList<>(),
        putParameters(model, typeResolver),
        new HashSet<>(getWriteResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code DELETE /{id}}
   *
   * @param model
   * @param typeResolver
   * @return
   */
  private static Operation getDeleteOperation(Class<? extends Model> model, TypeResolver typeResolver){
    return new Operation(
        HttpMethod.DELETE,
        "Delete an existing record",
        "Attempts to delete an existing record, identified by the submitted ID.",
        new ModelRef(String.class.getSimpleName()),
        "Delete",
        5,
        Collections.singleton(model.getSimpleName()),
        new HashSet<>(ApiMediaTypes.getAllResponseTypeValues()),
        new HashSet<>(ApiMediaTypes.getAllAcceptTypeValues()),
        Collections.emptySet(),
        new ArrayList<>(),
        deleteParameters(model, typeResolver),
        new HashSet<>(getWriteResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code HEAD /**}
   *
   * @param model
   * @return
   */
  private static Operation getHeadOperation(Class<? extends Model> model){
    return new Operation(
        HttpMethod.HEAD,
        "Get endpoint headers",
        "Retrieves only endpoint headers.",
        new ModelRef(String.class.getSimpleName()),
        "Head",
        6,
        Collections.singleton(model.getSimpleName()),
        Collections.singleton("*/*"),
        Collections.singleton("*/*"),
        Collections.emptySet(),
        new ArrayList<>(),
        new ArrayList<>(),
        new HashSet<>(getInfoResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  /**
   * {@code OPTIONS /**}
   *
   * @param model
   * @return
   */
  private static Operation getOptionsOperation(Class<? extends Model> model){
    return new Operation(
        HttpMethod.OPTIONS,
        "Get endpoint information",
        "Retrieves endpoint information.",
        new ModelRef(String.class.getSimpleName()),
        "Options",
        7,
        Collections.singleton(model.getSimpleName()),
        Collections.singleton("*/*"),
        Collections.singleton("*/*"),
        Collections.emptySet(),
        new ArrayList<>(),
        new ArrayList<>(),
        new HashSet<>(getInfoResponseMessages(model)),
        null,
        false,
        new ArrayList<>()
    );
  }

  private static List<ResponseMessage> getStandardGetResponseMessages(Class<?> responseModel){
    List<ResponseMessage> messages = new ArrayList<>();
    messages.add(new ResponseMessage(200, "OK", new ModelRef(responseModel.getSimpleName()),
        Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(400, "Invalid parameters",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(401, "Unauthorized",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(404, "Record not found",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    return messages;
  }

  private static List<ResponseMessage> getWriteResponseMessages(Class<?> responseModel){
    List<ResponseMessage> messages = new ArrayList<>();
    messages.add(new ResponseMessage(201, "Created", new ModelRef(responseModel.getSimpleName()),
        Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(401, "Unauthorized",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(403, "Forbidden",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(404, "Record not found",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    return messages;
  }

  private static List<ResponseMessage> getInfoResponseMessages(Class<?> responseModel){
    List<ResponseMessage> messages = new ArrayList<>();
    messages.add(new ResponseMessage(204, "No Content", new ModelRef("String"),
        Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(401, "Unauthorized",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    messages.add(new ResponseMessage(403, "Forbidden",
        new ModelRef(RestError.class.getSimpleName()), Collections.emptyMap(), new ArrayList<>()));
    return messages;
  }
	
	/* Parameters */

  public static List<Parameter> getModelParameters(
      Class<? extends Model> model, TypeResolver typeResolver){
    return QueryParameterUtil.getAvailableQueryParameters(model, false).values().stream()
        .map(descriptor -> createParameterFromDescriptior(descriptor, typeResolver))
        .collect(Collectors.toList());
  }

  private static Parameter createParameterFromDescriptior(
      QueryParameterDescriptor descriptor, TypeResolver typeResolver){
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

  private static List<Parameter> paginationParameters(TypeResolver typeResolver){
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new ParameterBuilder()
        .name("size")
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
        .name("page")
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
        .name("sort")
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

  private static List<Parameter> fieldFilteringParameters(TypeResolver typeResolver){
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new ParameterBuilder()
        .name("fields")
        .type(typeResolver.resolve(String.class))
        .modelRef(new ModelRef("string"))
        .parameterType("query")
        .required(false)
        .description("List of fields to be included in response objects.")
        .allowMultiple(false)
        .parameterAccess("")
        .build());
    parameters.add(new ParameterBuilder()
        .name("exclude")
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

  // Find All

  private static List<Parameter> findAllParameters(
      Class<? extends Model> model, TypeResolver typeResolver){
    List<Parameter> parameters = new ArrayList<>();
    parameters.addAll(paginationParameters(typeResolver));
    parameters.addAll(fieldFilteringParameters(typeResolver));
    parameters.addAll(getModelParameters(model, typeResolver));
    return parameters;
  }

  // Find By ID

  private static List<Parameter> findByIdParameters(
      Class<? extends Model> model, TypeResolver typeResolver){
    List<Parameter> parameters = new ArrayList<>();
    try {
      parameters.add(new ParameterBuilder()
          .name("id")
          .type(typeResolver.resolve(model.getMethod("getId").getReturnType()))
          .modelRef(new ModelRef("string"))
          .parameterType("path")
          .required(false)
          .description("List of fields to be included in response objects.")
          .allowMultiple(false)
          .parameterAccess("")
          .build());
    } catch (NoSuchMethodException e){
      e.printStackTrace();
    }
    parameters.addAll(fieldFilteringParameters(typeResolver));
    return parameters;
  }

  // Find distinct

  private static List<Parameter> findDistinctParameters(TypeResolver typeResolver){
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new ParameterBuilder()
        .name("field")
        .type(typeResolver.resolve(String.class))
        .modelRef(new ModelRef("string"))
        .parameterType("query")
        .required(true)
        .description("Field to return distinct values of.")
        .allowMultiple(false)
        .parameterAccess("")
        .build());
    return parameters;
  }

  // Post

  private static List<Parameter> postParameters(Class<? extends Model> model, TypeResolver typeResolver){
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
    return parameters;
  }

  //Put

  private static List<Parameter> putParameters(Class<? extends Model> model, TypeResolver typeResolver){
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
    } catch (NoSuchMethodException e){
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
    return parameters;
  }

  //Delete

  private static List<Parameter> deleteParameters(Class<? extends Model> model, TypeResolver typeResolver){
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
    } catch (NoSuchMethodException e){
      e.printStackTrace();
    }
    return parameters;
  }

}
