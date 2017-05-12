package com.blueprint.centromere.cli;

import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataOperations;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.dataimport.DataImportException;
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
  private Repositories repositories;
  private Environment environment;
  
  public void run(String category, List<String> toDelete){
    
    category = category.toLowerCase().replaceAll("-", "");
    if (!deleteable.contains(category)){
      throw new DataImportException(String.format("The selected category is not supported for deletion:, %s", category));
    }
    if (toDelete.isEmpty()){
      throw new DataImportException(String.format("No items selected for deletion for category: %s", category));
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

          Printer.print(String.format("Deleting DataSet %s and all associated records",
              dataSet.getShortName()), logger, Level.INFO);
          for (String dataFileId: dataSet.getDataFileIds()){
            DataFile dataFile = dataFileRepository.findOne(dataFileId);
            deleteDataFile(dataFile);
          }
          
          dataSetRepository.delete(dataSet);

        }
        
        return;
      
    }
    
  }
  
  private void deleteDataFile(DataFile dataFile){
    Class<?> model = null;
    try {
      model = dataFile.getModelType();
    } catch (ClassNotFoundException e){
      throw new DataImportException(e);
    }
    ModelRepository repository = (ModelRepository) repositories.getRepositoryFor(model);
    if (repository instanceof DataOperations){
      DataOperations operations = (DataOperations) repository;
      operations.deleteByDataFileId(dataFile.getId());
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
  public void setRepositories(Repositories repositories) {
    this.repositories = repositories;
  }
}
