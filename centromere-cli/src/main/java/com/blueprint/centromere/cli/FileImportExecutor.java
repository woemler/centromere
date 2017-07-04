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

package com.blueprint.centromere.cli;

import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataOperations;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.blueprint.centromere.core.dataimport.processor.RecordProcessor;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.support.Repositories;

/**
 * @author woemler
 * @since 0.5.0
 */
public class FileImportExecutor implements EnvironmentAware {
	
	private ModelProcessorBeanRegistry processorRegistry;
	private DataSetRepository dataSetRepository;
	private DataFileRepository dataFileRepository;
	private Environment environment;
	private Repositories repositories;
	
	private static final Logger logger = LoggerFactory.getLogger(FileImportExecutor.class);

  public void run(String dataType, String filePath) {
    run(dataType, filePath, null, null);
  }
	
	public void run(String dataType, String filePath, DataSet dataSet, DataFile dataFile){

    ImportOptions importOptions = new ImportOptionsImpl(environment);
		
	  // Check to make sure the target data type is supported
	  if (!processorRegistry.isSupportedDataType(dataType)){
	    String message = String.format("Data type %s is not supported by a registered "
          + "record processor.", dataType);
	    Printer.print(message, logger, Level.WARN);
			throw new DataImportException(message);
		}
    RecordProcessor processor = processorRegistry.getByDataType(dataType);
    logger.info(String.format("Using record processor: %s", processor.getClass().getName()));
		Printer.print(String.format("Running file import: data-type=%s  file=%s", dataType, filePath), logger, Level.INFO);
		
	  // Get the data set object
    if (dataSet == null){
      dataSet = getDataSetFromEnvironment();
    }
    Optional<DataSet> optional = dataSetRepository.findByShortName(dataSet.getShortName());
    if (!optional.isPresent()){
      dataSet = dataSetRepository.insert(dataSet);
      Printer.print(String.format("Creating new DataSet record: %s", dataSet.toString()), logger, Level.INFO);
    } else {
      dataSet = optional.get();
      Printer.print(String.format("Using existing DataSet record: %s", dataSet.toString()), logger, Level.INFO);
    }
    
    // Get the data file object
    if (dataFile == null){
      dataFile = new DataFile();
    }
    dataFile.setFilePath(filePath);
    dataFile.setDataType(dataType);
    dataFile.setModel(processor.getModel());
    dataFile.setDataSetId(dataSet.getId());
    dataFile.setDateCreated(new Date());
    dataFile.setDateUpdated(new Date());
    try {
      HashCode hashCode = Files.hash(new File(filePath), Hashing.md5());
      dataFile.setChecksum(hashCode.toString());
    } catch (IOException e){
      throw new DataImportException(e);
    }
    
    Optional<DataFile> dfOptional = dataFileRepository.findByFilePath(filePath);
    
    // Does a record with the file path exist already?
    if (!dfOptional.isPresent()){
      
      // If not, create a new record
      dataFile = dataFileRepository.insert(dataFile);
      Printer.print(String.format("Creating new DataFile record: %s", dataFile.toString()), logger, Level.INFO);
      
    } else {
      
      DataFile df = dfOptional.get();
      
      // If file exists and skip-existing-files flag set, skip file and return
      if (importOptions.skipExistingFiles()) {
        
        Printer.print(String.format("DataFile record already exists.  Skipping import: %s",
            df.getFilePath()), logger, Level.WARN);
        return;
        
      } 
      // If overwrite-existing-files flag is set, overwrite the file record and its data
      else if (importOptions.overwriteExistingFiles()){
        
        // If the files have the same checksum, no need to overwrite
        if (df.getChecksum().equalsIgnoreCase(dataFile.getChecksum())){
          Printer.print(String.format("File is identical to original, overwrite will be skipped: %s", df.getFilePath()), logger, Level.INFO);
          return;
        }
        
        Printer.print(String.format("Overwriting existing data file record: %s", df.getFilePath()), logger, Level.INFO);
        
        // Get the repository for the file's data type
        ModelRepository r;
        try {
          r = (ModelRepository) repositories.getRepositoryFor(df.getModelType());
        } catch (ClassNotFoundException e){
          throw new DataImportException(e);
        }
        
        // If possible, delete the associated records for the file
        if (r instanceof DataOperations) {
          ((DataOperations) r).deleteByDataFileId(df.getId());
        } else {
          Printer.print("Data is not over-writable.  Exiting.", logger, Level.WARN);
          return;
        }
        
        // Create a new DataFile record for the file
        dataFile.setDateCreated(df.getDateCreated());
        dataFileRepository.delete(df);
        dataFile = dataFileRepository.insert(dataFile);
        
      } 
      // Otherwise, use the existing file record and append to its existing records. 
      else {
        
        dataFile = df;
        Printer.print(String.format("Using existing DataFile record: %s", dataFile.toString()), logger, Level.INFO);
        
      }
    }

		// Configure the processor
    processor.setImportOptions(importOptions);
		processor.setDataSet(dataSet);
    processor.setDataFile(dataFile);

    logger.info("Running processor doBefore method");
		processor.doBefore();

    logger.info("Processing file");
		processor.run();

    logger.info("Running processor doAfter method");
		processor.doAfter();
    Printer.print("File processing complete.", logger, Level.INFO);
	}

  private DataSet getDataSetFromEnvironment() {
    DataSet dataSet = new DataSet();
    if (environment.containsProperty("dataSet.name")){
      String name = environment.getRequiredProperty("dataSet.name");
      dataSet.setDisplayName(name);
      dataSet.setShortName(name.toLowerCase().replaceAll("[^a-z0-9\\s]]", "").replaceAll("\\s+", "-"));
    }
    if (environment.containsProperty("dataSet.displayName")){
      dataSet.setDisplayName(environment.getRequiredProperty("dataSet.displayName"));
    }
    if (environment.containsProperty("dataSet.shortName")){
      dataSet.setShortName(environment.getRequiredProperty("dataSet.shortName"));
    }
    if (environment.containsProperty("dataSet.source")){
      dataSet.setSource(environment.getRequiredProperty("dataSet.source"));
    }
    if (environment.containsProperty("dataSet.version")){
      dataSet.setVersion(environment.getRequiredProperty("dataSet.version"));
    }
    if (environment.containsProperty("dataSet.description")){
      dataSet.setDescription(environment.getRequiredProperty("dataSet.description"));
    }
    
    if (dataSet.getDisplayName() == null){
      dataSet.setDisplayName(environment.getRequiredProperty("centromere.import.dataset.default-display-name"));
    }
    if (dataSet.getShortName() == null){
      dataSet.setShortName(environment.getRequiredProperty("centromere.import.dataset.default-short-name"));
    }
    if (dataSet.getSource() == null){
      dataSet.setSource(environment.getRequiredProperty("centromere.import.dataset.default-source"));
    }
    if (dataSet.getVersion() == null){
      dataSet.setVersion(environment.getRequiredProperty("centromere.import.dataset.default-version"));
    }
    if (dataSet.getDescription() == null){
      dataSet.setDescription(environment.getRequiredProperty("centromere.import.dataset.default-description"));
    }
    
    return dataSet;
  }

	@Autowired
	public void setProcessorRegistry(ModelProcessorBeanRegistry processorRegistry) {
		this.processorRegistry = processorRegistry;
	}

  @Override
  @Autowired
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public void setDataSetRepository(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public void setDataFileRepository(DataFileRepository dataFileRepository) {
    this.dataFileRepository = dataFileRepository;
  }

  @Autowired
  public void setRepositories(Repositories repositories) {
    this.repositories = repositories;
  }
}
