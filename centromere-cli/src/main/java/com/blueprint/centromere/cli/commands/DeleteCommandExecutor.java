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
import com.blueprint.centromere.core.config.ModelResourceRegistry;
import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.MetadataOperations;
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
  private DataSourceRepository dataSourceRepository;
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
    if (DataSource.class.isAssignableFrom(model)){
      
      DataSource dataSource = (DataSource) record;
      Printer.print(String.format("Deleting DataFile %s and all associated records",
          dataSource.getSource()), logger, Level.INFO);
      deleteDataFile(dataSource);
      
    } else if (DataSet.class.isAssignableFrom(model)){
      
      DataSet dataSet = (DataSet) record;
      Printer.print(String.format("Deleting DataSet %s and all associated records and samples",
          dataSet.getDataSetId()), logger, Level.INFO);
      for (String dataFileId: (List<String>) dataSet.getDataSourceIds()){
        Optional<DataSource> dfOptional = dataSourceRepository.findByDataSourceId(dataFileId);
        if (dfOptional.isPresent()) {
          deleteDataFile(dfOptional.get());
        }
      }
      dataSetRepository.delete(dataSet);
      
    } else {
      
      Printer.print(String.format("Deleting requested model %s record: %s", model.getName(), 
          parameters.getId()), logger, Level.INFO);
      repository.deleteById(id);
      
    }
    
  }
  
  @SuppressWarnings("unchecked")
  private void deleteDataFile(DataSource dataSource) throws CommandLineRunnerException{
    
    Class<? extends Model<?>> model;
    ModelRepository repository;
    
    try {
      model = (Class<? extends Model<?>>) dataSource.getModelType();
    } catch (ClassNotFoundException e){
      throw new CommandLineRunnerException(e);
    }
    
    try {
      repository = repositoryRegistry.getRepositoryByModel(model);
    } catch (ModelRegistryException e){
      throw new CommandLineRunnerException(e);
    }
    
    if (repository instanceof MetadataOperations){
      MetadataOperations operations = (MetadataOperations) repository;
      operations.deleteByDataSourceId(dataSource.getDataSourceId());
      DataSet dataSet = dataSetRepository.findByDataSourceId(dataSource.getDataSetId()).get(0);
      List<String> dataFileIds = dataSet.getDataSourceIds();
      dataFileIds.remove(dataSource.getDataSourceId());
      dataSet.setDataSourceIds(dataFileIds);
      dataSetRepository.update(dataSet);
      dataSourceRepository.delete(dataSource);
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
  public void setDataSourceRepository(DataSourceRepository dataSourceRepository) {
    this.dataSourceRepository = dataSourceRepository;
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
