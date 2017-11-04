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

import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataOperations;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.support.Repositories;

/**
 * @author woemler
 */
public class DeleteCommandExecutor implements EnvironmentAware {
  
  private static final Logger logger = LoggerFactory.getLogger(DeleteCommandExecutor.class);
  private static final List<String> deleteable = Arrays.asList("dataset", "datafile");
  
  private DataSetRepository dataSetRepository;
  private DataFileRepository dataFileRepository;
  private SampleRepository sampleRepository;
  private Repositories repositories;
  private Environment environment;
  
  public void run(String category, List<String> toDelete) throws CommandLineRunnerException {
    
    category = category.toLowerCase().replaceAll("-", "");
    if (!deleteable.contains(category)){
      throw new CommandLineRunnerException(String.format("The selected category is not supported for deletion:, %s", category));
    }
    if (toDelete.isEmpty()){
      throw new CommandLineRunnerException(String.format("No items selected for deletion for category: %s", category));
    }
    
    switch (category){
      
      case "datafile":
        
        for (String item: toDelete){
          
          DataFile dataFile = null;
          Optional<DataFile> optional = dataFileRepository.findByFilePath(item);
          if (optional.isPresent()){
            dataFile = optional.get();
          } else {
            dataFile = dataFileRepository.findOne(item);
          }
          
          if (dataFile == null){
            Printer.print(String.format("Unable to identify item for deletion: category=%s  item=%s",
                category, item), logger, Level.WARN);
            continue;
          }

          Printer.print(String.format("Deleting DataFile %s and all associated records",
              dataFile.getFilePath()), logger, Level.INFO);
          deleteDataFile(dataFile);
          
        }
        
        return;
        
      case "dataset":

        for (String item: toDelete){

          DataSet dataSet = null;
          Optional<DataSet> optional = dataSetRepository.findByShortName(item);
          if (optional.isPresent()){
            dataSet = optional.get();
          } else {
            dataSet = dataSetRepository.findOne(item);
          }

          if (dataSet == null){
            Printer.print(String.format("Unable to identify item for deletion: category=%s  item=%s",
                category, item), logger, Level.WARN);
            continue;
          }

          Printer.print(String.format("Deleting DataSet %s and all associated records and samples",
              dataSet.getShortName()), logger, Level.INFO);
          for (String dataFileId: dataSet.getDataFileIds()){
            DataFile dataFile = dataFileRepository.findOne(dataFileId);
            deleteDataFile(dataFile);
          }
          for (String sampleId: dataSet.getSampleIds()){
            sampleRepository.delete(sampleId);
          }
          
          dataSetRepository.delete(dataSet);

        }
        
        return;
      
    }
    
  }
  
  private void deleteDataFile(DataFile dataFile) throws CommandLineRunnerException{
    
    Class<?> model = null;
    
    try {
      model = dataFile.getModelType();
    } catch (ClassNotFoundException e){
      throw new CommandLineRunnerException(e);
    }
    
    ModelRepository repository = (ModelRepository) repositories.getRepositoryFor(model);
    
    if (repository instanceof DataOperations){
      
      DataOperations operations = (DataOperations) repository;
      operations.deleteByDataFileId(dataFile.getId());
      DataSet dataSet = dataSetRepository.findOne(dataFile.getDataSetId());
      List<String> dataFileIds = dataSet.getDataFileIds();
      dataFileIds.remove(dataFile.getId());
      dataSet.setDataFileIds(dataFileIds);
      dataSetRepository.update(dataSet);
      dataFileRepository.delete(dataFile);
    } else {
      Printer.print("The selected data type does not support record deletion: DataFile", logger, Level.WARN);
    }
    
  }

  @Override
  @Autowired
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Autowired
  public void setDataSetRepository(
      DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Autowired
  public void setDataFileRepository(
      DataFileRepository dataFileRepository) {
    this.dataFileRepository = dataFileRepository;
  }

  @Autowired
  public void setSampleRepository(
      SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }

  @Autowired
  public void setRepositories(Repositories repositories) {
    this.repositories = repositories;
  }
}
