package com.blueprint.centromere.cli.commands;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.parameters.UpdateCommandParameters;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

/**
 * Attempts to update or replace existing records in the data warehouse, using parsed command line input.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class UpdateCommandExecutor {
  
  private static final Logger logger = LoggerFactory.getLogger(UpdateCommandExecutor.class);
  
  private ModelRepositoryRegistry repositoryRegistry;
  private ModelResourceRegistry resourceRegistry;
  private ObjectMapper objectMapper;
  private ConversionService conversionService;
  
  @SuppressWarnings("unchecked")
  public <ID extends Serializable, T extends Model<ID>> void run(UpdateCommandParameters parameters) 
      throws CommandLineRunnerException {
    
    // If help flag is present, display usage and return
    if (parameters.isHelp()){
      JCommander.newBuilder()
          .addCommand(UpdateCommandParameters.COMMAND, new UpdateCommandParameters())
          .build()
          .usage();
      System.out.println(String.format("Available models: %s", getAvailableModels()));
      return;
    }
    
    // Get the requested model and repository
    Class<T> model;
    ModelRepository<T, ID> repository;
    try {
      if (!resourceRegistry.isRegisteredResource(parameters.getModel())){
        throw new CommandLineRunnerException(String.format("%s is not a valid model. Available models: %s",
            parameters.getModel(), getAvailableModels()));
      }
      model = (Class<T>) resourceRegistry.getModelByUri(parameters.getModel());
      repository = (ModelRepository<T, ID>) repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      throw new CommandLineRunnerException(e);
    }
    
    // Get the existing record
    
    Optional<T> existingOptional = repository.findById((ID) parameters.getId());
    if (!existingOptional.isPresent()){
      throw new CommandLineRunnerException(String.format("Cannot find %s record for ID: %s", 
          model.getSimpleName(), parameters.getId()));
    }
    T existing = existingOptional.get();
    
    // Convert the model
    T object = null;
    try {
      object = objectMapper.readValue(parameters.getData(), model);
    } catch (IOException e){
      Printer.print(String.format("Cannot convert data to type %s: %s", 
          model.getName(), parameters.getData()), logger, Level.ERROR);
      throw new CommandLineRunnerException(e);
    }
    
    if (parameters.isReplace()){
      BeanWrapper wrapper = new BeanWrapperImpl(object);
      wrapper.setPropertyValue("id", existing.getId());
      object = (T) wrapper.getWrappedInstance();
      existing = object;
    } else {
      BeanUtils.copyProperties(object, existing, getNullProperties(object));
    }
    
    // Insert the object
    try {
      repository.update(existing);
    } catch (Exception e){
      throw new CommandLineRunnerException(e);
    }

    Printer.print(String.format("Successfully created new %s record: %s", model.getName(), object.toString()));
    
  }

  /**
   * Inspects the object to see what attributes are null or empty, for the sake of excluding them
   *   from a {@link BeanUtils#copyProperties(Object, Object)} call.
   * 
   * @param source object to inspect
   * @return
   */
  private String[] getNullProperties (Object source) {
    BeanWrapper wrapper = new BeanWrapperImpl(source);
    Set<String> properties = new HashSet<>();
    properties.add("id");
    for(PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
      Object value = wrapper.getPropertyValue(descriptor.getName());
      if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
        if (((Collection) value).isEmpty()) {
          properties.add(descriptor.getName());
        }
      } else if (value == null) {
        properties.add(descriptor.getName());
      }
    }
    String[] result = new String[properties.size()];
    return properties.toArray(result);
  }

  /**
   * Returns a list of lower-case model names, which server as available arguments for creatable models.
   * 
   * @return
   */
  private List<String> getAvailableModels(){
    return new ArrayList<>(resourceRegistry.getRegisteredModelUris());
  }

  @Autowired
  public void setRepositoryRegistry(ModelRepositoryRegistry repositoryRegistry) {
    this.repositoryRegistry = repositoryRegistry;
  }

  @Autowired
  public void setResourceRegistry(ModelResourceRegistry resourceRegistry) {
    this.resourceRegistry = resourceRegistry;
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
