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

package com.blueprint.centromere.cli.commands;

import com.beust.jcommander.JCommander;
import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.parameters.DeleteCommandParameters;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataOperations;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author woemler
 */
public class DeleteCommandExecutor {
  
  private static final Logger logger = LoggerFactory.getLogger(DeleteCommandExecutor.class);
  
  private DataSetRepository dataSetRepository;
  private DataFileRepository dataFileRepository;
  private ModelResourceRegistry resourceRegistry;
  private ModelRepositoryRegistry repositoryRegistry;
  private ConversionService conversionService;
  
  @SuppressWarnings("unchecked")
  public <T extends Model<ID>, ID extends Serializable> void run(DeleteCommandParameters parameters) 
      throws CommandLineRunnerException {

    // If help flag is present, display usage and return
    if (parameters.isHelp()){
      JCommander.newBuilder()
          .addCommand(DeleteCommandParameters.COMMAND, new DeleteCommandParameters())
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
    
    // Convert the ID to the correct type
    ID id;
    Class<ID> idType;
    try {
      idType = (Class<ID>) model.getMethod("getId").getReturnType();
    } catch (NoSuchMethodException e){
      throw new CommandLineRunnerException(e);
    }
    if (String.class.isAssignableFrom(idType)){
      id = (ID) parameters.getId();
    } else if (conversionService.canConvert(String.class, idType)) {
      id = (ID) conversionService.convert(parameters.getId(), TypeDescriptor.valueOf(String.class), 
          TypeDescriptor.valueOf(idType));
    } else {
      throw new CommandLineRunnerException(String.format("Cannot convert requested ID value to " 
          + "target type: %s", idType.getName()));
    }
      
    // Get the target record
    Optional<T> optional = repository.findById(id);
    if (!optional.isPresent()){
      Printer.print(String.format("No record for model %s can be found with ID: %s", model.getName(), parameters.getId()));
    }
    T record = optional.get();
    
    // Delete the record(s)
    if (DataFile.class.isAssignableFrom(model)){
      
      DataFile dataFile = (DataFile) record;
      Printer.print(String.format("Deleting DataFile %s and all associated records",
          dataFile.getFilePath()), logger, Level.INFO);
      deleteDataFile(dataFile);
      
    } else if (DataSet.class.isAssignableFrom(model)){
      
      DataSet dataSet = (DataSet) record;
      Printer.print(String.format("Deleting DataSet %s and all associated records and samples",
          dataSet.getDataSetId()), logger, Level.INFO);
      for (String dataFileId: (List<String>) dataSet.getDataFileIds()){
        Optional<DataFile> dfOptional = dataFileRepository.findByDataFileId(dataFileId);
        if (dfOptional.isPresent()) {
          deleteDataFile(dfOptional.get());
        }
      }
      dataSetRepository.delete(dataSet);
      
    } else {
      
      Printer.print(String.format("Deleting requested model %s record: %s", model.getName(), 
          parameters.getId()), logger, Level.INFO);
      repository.delete(id);
      
    }
    
  }
  
  @SuppressWarnings("unchecked")
  private void deleteDataFile(DataFile dataFile) throws CommandLineRunnerException{
    
    Class<? extends Model<?>> model;
    ModelRepository repository;
    
    try {
      model = dataFile.getModelType();
    } catch (ClassNotFoundException e){
      throw new CommandLineRunnerException(e);
    }
    
    try {
      repository = repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      throw new CommandLineRunnerException(e);
    }
    
    if (repository instanceof DataOperations){
      DataOperations operations = (DataOperations) repository;
      operations.deleteByDataFileId(dataFile.getDataFileId());
      DataSet dataSet = (DataSet) dataSetRepository.findOne(dataFile.getDataSetId());
      List<String> dataFileIds = dataSet.getDataFileIds();
      dataFileIds.remove(dataFile.getDataFileId());
      dataSet.setDataFileIds(dataFileIds);
      dataSetRepository.update(dataSet);
      dataFileRepository.delete(dataFile);
    } else {
      Printer.print("The selected data type does not support record deletion: DataFile", logger, Level.WARN);
    }
    
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
  public void setDataSetRepository(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Autowired
  public void setDataFileRepository(DataFileRepository dataFileRepository) {
    this.dataFileRepository = dataFileRepository;
  }

  @Autowired
  public void setResourceRegistry(ModelResourceRegistry resourceRegistry) {
    this.resourceRegistry = resourceRegistry;
  }

  @Autowired
  public void setRepositoryRegistry(ModelRepositoryRegistry repositoryRegistry) {
    this.repositoryRegistry = repositoryRegistry;
  }

  @Autowired
  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }
}
