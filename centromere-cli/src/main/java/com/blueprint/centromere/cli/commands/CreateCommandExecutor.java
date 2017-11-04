package com.blueprint.centromere.cli.commands;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    T object = null;
    try {
      object = objectMapper.readValue(parameters.getData(), model);
    } catch (IOException e){
      logger.error(String.format("Cannot convert data to type %s: %s", model.getName(), parameters.getData()));
      throw new CommandLineRunnerException(e);
    }
    
    // Insert the object
    try {
      repository.insert(object);
    } catch (Exception e){
      throw new CommandLineRunnerException(e);
    }

    Printer.print(String.format("Successfully created new %s record: %s", model.getName(), object.toString()));
    
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
}
