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

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataOperations;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetAware;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.blueprint.centromere.core.dataimport.processor.RecordProcessor;
import com.blueprint.centromere.core.repository.ModelRepository;
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
	
	public void run(String dataType, String filePath, DataSet dataSet, DataFile dataFile){

    ImportOptions importOptions = new ImportOptionsImpl(environment);
		
	  // Check to make sure the target data type is supported
	  if (!processorRegistry.isSupportedDataType(dataType)){
			throw new DataImportException(String.format("Data type %s is not supported by a registered " 
					+ "record processor.", dataType));
		}
    RecordProcessor processor = processorRegistry.getByDataType(dataType);
    logger.info(String.format("Using record processor: %s", processor.getClass().getName()));
		logger.info(String.format("Running file import: data-type=%s  file=%s", dataType, filePath));
		
	  // Get the data set object
    if (dataSet == null){
      dataSet = getDataSetFromEnvironment();
    }
    Optional<DataSet> optional = dataSetRepository.findByShortName(dataSet.getShortName());
    if (!optional.isPresent()){
      dataSet = dataSetRepository.insert(dataSet);
      logger.info(String.format("Creating new DataSet record: %s", dataSet.toString()));
    } else {
      dataSet = optional.get();
      logger.info(String.format("Using existing DataSet record: %s", dataSet.toString()));
    }
    
    // Get the data file object
    if (dataFile == null){
      dataFile = new DataFile();
      dataFile.setFilePath(filePath);
      dataFile.setDataType(dataType);
      dataFile.setModel(processor.getModel());
      dataFile.setDateCreated(new Date());
      dataFile.setDateUpdated(new Date());
      dataFile.setDataSetId(dataSet.getId());
    }
    Optional<DataFile> dfOptional = dataFileRepository.findByFilePath(filePath);
    if (!dfOptional.isPresent()){
      dataFile = dataFileRepository.insert(dataFile);
      logger.info(String.format("Creating new DataFile record: %s", dataFile.toString()));
    } else {
      DataFile df = dfOptional.get();
      if (importOptions.skipExistingFiles()) {
        logger.warn(String.format("DataFile record already exists.  Skipping import: %s",
            df.getFilePath()));
        return;
      } else if (importOptions.overwriteExistingFiles()){
        logger.info(String.format("Overwriting existing data file record: %s", df.getFilePath()));
        ModelRepository r;
        try {
          r = (ModelRepository) repositories.getRepositoryFor(df.getModelType());
        } catch (ClassNotFoundException e){
          throw new DataImportException(e);
        }
        if (r instanceof DataOperations) {
          ((DataOperations) r).deleteByDataFileId(df.getId());
        } else {
          logger.warn(String.format("Data is not overwritable.  Exiting."));
          return;
        }
        dataFileRepository.delete(df);
        dataFile = dataFileRepository.insert(dataFile);
      } else {
        dataFile = df;
        logger.info(String.format("Using existing DataFile record: %s", dataFile.toString()));
      }
    }

		// Configure the processor
    processor.setImportOptions(importOptions);
		if (processor instanceof DataSetAware){
      ((DataSetAware) processor).setDataSet(dataSet);
    }
    if (processor instanceof DataFileAware){
		  ((DataFileAware) processor).setDataFile(dataFile);
    }

    logger.info("Running processor doBefore method");
		processor.doBefore(filePath, dataFile, dataSet);

    logger.info("Processing file");
		processor.run(filePath);

    logger.info("Running processor doAfter method");
		processor.doAfter(filePath);
    logger.info("File processing complete.");
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
    return dataSet;
  }

  public void run(String dataType, String filePath) {
	  run(dataType, filePath, null, null);
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
