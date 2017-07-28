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
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
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
import org.springframework.data.repository.support.Repositories;

/**
 * @author woemler
 * @since 0.5.0
 */
public class FileImportExecutor {
	
	private ModelProcessorBeanRegistry processorRegistry;
	private DataSetRepository dataSetRepository;
	private DataFileRepository dataFileRepository;
	private Repositories repositories;
	private DataImportProperties dataImportProperties;
	
	private static final Logger logger = LoggerFactory.getLogger(FileImportExecutor.class);

  public void run(String dataType, String filePath) throws CommandLineRunnerException {
    run(dataType, filePath, null, null);
  }
	
	public void run(String dataType, String filePath, DataSet dataSet, DataFile dataFile) 
      throws CommandLineRunnerException {

    // Check to make sure the target data type is supported
	  if (!processorRegistry.isSupportedDataType(dataType)){
	    String message = String.format("Data type %s is not supported by a registered "
          + "record processor.", dataType);
	    Printer.print(message, logger, Level.WARN);
			throw new CommandLineRunnerException(message);
		}
    RecordProcessor processor = processorRegistry.getByDataType(dataType);
    logger.info(String.format("Using record processor: %s", processor.getClass().getName()));
		Printer.print(String.format("Running file import: data-type=%s  file=%s", dataType, filePath), logger, Level.INFO);
		
	  // Get the data set object
    if (dataSet == null){
      dataSet = dataImportProperties.getDataSet();
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
      throw new CommandLineRunnerException(e);
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
      if (dataImportProperties.isSkipExistingFiles()) {
        
        Printer.print(String.format("DataFile record already exists.  Skipping import: %s",
            df.getFilePath()), logger, Level.WARN);
        return;
        
      } 
      // If overwrite-existing-files flag is set, overwrite the file record and its data
      else if (dataImportProperties.isOverwriteExistingFiles()){
        
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
          throw new CommandLineRunnerException(e);
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
		processor.setDataSet(dataSet);
    processor.setDataFile(dataFile);

    logger.info("Running processor doBefore method");
		try {
      processor.doBefore();
    } catch (DataImportException e){
		  throw new CommandLineRunnerException(e);
    }

		Exception exception = null;
		try {
      logger.info("Processing file");
      processor.run();
    } catch (Exception e){
		  exception = e;
    }

    try {
      if (processor.isInFailedState()) {
        logger.error("Processor execution failed.  Triggering 'doOnFailure' method.");
        processor.doOnFailure();
        if (exception != null)
          throw new CommandLineRunnerException(exception);
      } else {
        logger.info("Running processor doAfter method");
        processor.doAfter();
      }
    } catch (DataImportException e){
      throw new CommandLineRunnerException(e);
    }
    Printer.print("File processing complete.", logger, Level.INFO);
	}

	@Autowired
	public void setProcessorRegistry(ModelProcessorBeanRegistry processorRegistry) {
		this.processorRegistry = processorRegistry;
	}

	@Autowired
  public void setDataImportProperties(DataImportProperties dataImportProperties) {
    this.dataImportProperties = dataImportProperties;
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
  public void setRepositories(Repositories repositories) {
    this.repositories = repositories;
  }
}
