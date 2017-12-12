package com.blueprint.centromere.cli.commands;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Attempts to create new records in the data warehouse using parsed command line input.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class CreateCommandExecutor {
  
  private static final Logger logger = LoggerFactory.getLogger(CreateCommandExecutor.class);
  
  private ModelRepositoryRegistry repositoryRegistry;
  private ObjectMapper objectMapper;
  private ConversionService conversionService;
  
  @SuppressWarnings("unchecked")
  public <T extends Model<?>> void run(CreateCommandParameters parameters) throws CommandLineRunnerException {
    
    // If help flag is present, display usage and return
    if (parameters.isHelp()){
      JCommander.newBuilder()
          .addCommand(CreateCommandParameters.COMMAND, new CreateCommandParameters())
          .build().usage();
      System.out.println(String.format("Available models: %s", getAvailableModels()));
      return;
    }
    
    // Get the requested model and repository
    if (!repositoryRegistry.isRegisteredModel(parameters.getModel())){
      throw new CommandLineRunnerException(String.format("%s is not a valid model. Available models: %s", 
          parameters.getModel(), getAvailableModels()));
    }
    Class<T> model = (Class<T>) repositoryRegistry.getRegisteredModel(parameters.getModel());
    ModelRepository<T, ?> repository = repositoryRegistry.getRepositoryByModel(model);
    
    // Convert the model
    T object;
    if (parameters.getData() == null && !parameters.getFields().isEmpty()){
      object = createObjectFromDynamicParameters(parameters.getFields(), model);
    } else {
      object = createObjectFromJson(parameters.getData(), model);
    }
    
    // Insert the object
    try {
      repository.insert(object);
    } catch (Exception e){
      throw new CommandLineRunnerException(e);
    }

    Printer.print(String.format("Successfully created new %s record: %s", model.getName(), object.toString()), logger, Level.INFO);
    
  }

  /**
   * Attempts to create a new {@link Model} object by converting JSON to the target model type.
   * 
   * @param json
   * @param model
   * @param <T>
   * @return
   * @throws CommandLineRunnerException
   */
  private <T extends Model<?>> T createObjectFromJson(String json, Class<T> model) 
      throws CommandLineRunnerException {
    try {
      return objectMapper.readValue(json, model);
    } catch (IOException e){
      logger.error(String.format("Cannot convert data to type %s: %s", model.getName(), json));
      throw new CommandLineRunnerException(e);
    }
  }

  /**
   * Attempts to create a new {@link Model} object by converting dynamic parameters passed as a {@link Map}
   *   using {@link BeanWrapper}.
   * 
   * @param fields
   * @param model
   * @param <T>
   * @return
   * @throws CommandLineRunnerException
   */
  private <T extends Model<?>> T createObjectFromDynamicParameters(Map<String, String> fields, Class<T> model) 
      throws CommandLineRunnerException{
    BeanWrapper wrapper = new BeanWrapperImpl(model);
    for (Map.Entry<String, String> entry: fields.entrySet()){
      String field = entry.getKey();
      if (wrapper.isWritableProperty(field)){
        Class<?> type = wrapper.getPropertyType(field);
        if (conversionService.canConvert(String.class, type)){
          wrapper.setPropertyValue(field, conversionService.convert(entry.getValue(),
              TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(type)));
        } else {
          Printer.print(String.format("Cannot convert field %s to type %s", field, type.getName()),
              logger, Level.WARN);
        }
      } else {
        Printer.print(String.format("Invalid or unwritable field: %s", field), logger, Level.WARN);
      }
    }
    return (T) wrapper.getWrappedInstance();
  } 

  /**
   * Returns a list of lower-case model names, which server as available arguments for creatable models.
   * 
   * @return
   */
  private List<String> getAvailableModels(){
    List<String> models = new ArrayList<>();
    for (Class<?> model: repositoryRegistry.getRegisteredModels()){
      models.add(model.getSimpleName().replace(".class", "").toLowerCase());
    }
    Collections.sort(models);
    return models;
  }

  @Autowired
  public void setRepositoryRegistry(
      ModelRepositoryRegistry repositoryRegistry) {
    this.repositoryRegistry = repositoryRegistry;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Autowired
  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }
}