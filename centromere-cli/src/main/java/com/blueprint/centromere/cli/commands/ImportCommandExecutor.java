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
import com.blueprint.centromere.cli.ModelProcessorBeanRegistry;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.dataimport.DataImportException;
import com.blueprint.centromere.cli.dataimport.processor.RecordProcessor;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.model.impl.DataSource.SourceTypes;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.blueprint.centromere.core.repository.impl.MetadataOperations;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.support.Repositories;

/**
 * @author woemler
 * @since 0.5.0
 */
public class ImportCommandExecutor {
	
	private ModelProcessorBeanRegistry processorRegistry;
	private DataSetRepository dataSetRepository;
	private DataSourceRepository dataSourceRepository;
	private Repositories repositories;
	private DataImportProperties dataImportProperties;
	
	private static final Logger logger = LoggerFactory.getLogger(ImportCommandExecutor.class);
	
	@SuppressWarnings("unchecked")
	public void run(ImportCommandParameters parameters) throws CommandLineRunnerException {
	  
	  // If help flag is active, show info and exit.
	  if (parameters.isHelp()){
      showHelp();
	    return;
    }
	  
	  String dataType = parameters.getDataType();
	  String filePath = parameters.getFilePath();
	  String dataSetId = parameters.getDataSetId();
	  Map<String, String> attributes = parameters.getAttributes();
	  updateDataImportProperties(parameters);

    // Check to make sure the target data type is supported and get the processor
	  if (!processorRegistry.isSupportedDataType(dataType)){
	    throw new CommandLineRunnerException(String.format("Data type %s is not supported by a " 
          + "registered record processor.", dataType));
		}
    RecordProcessor processor = processorRegistry.getByDataType(dataType);
    logger.info(String.format("Using record processor: %s", processor.getClass().getName()));
		Printer.print(String.format("Running file import: data-type=%s  file=%s", dataType, filePath), logger, Level.INFO);
		
	  // Get the data set
    Optional<DataSet> dataSetOptional = dataSetRepository.findById(dataSetId);
    if (!dataSetOptional.isPresent()){
      dataSetOptional = dataSetRepository.findByDataSetId(dataSetId);
    }
    if (!dataSetOptional.isPresent()){
      throw new CommandLineRunnerException(String.format("Unable to identify data set using key: %s",
          parameters.getDataSetId()));
    }
    DataSet dataSet = dataSetOptional.get();
    Printer.print(String.format("Using DataSet record: %s", dataSet.toString()), logger, Level.INFO);

    // Get the data file record
    DataSource dataSource;
    Optional<DataSource> dfOptional = dataSourceRepository.findBySource(filePath);
    
    // DataFile record already exists
    if (dfOptional.isPresent()){
      
      dataSource = dfOptional.get();
      
      // No overwrite
      if (!parameters.isOverwrite()){
        Printer.print(String.format("DataFile record already exists.  Skipping import: %s",
            filePath), logger, Level.WARN);
        return;
      } 
      
      // Overwrite
      else {

        // Check to see if the file has a different checksum before overwriting
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
          try {
            HashCode hashCode = Files.hash(new File(filePath), Hashing.md5());
            String checksum = hashCode.toString();
            if (dataSource.getChecksum().equalsIgnoreCase(checksum)) {
              Printer.print(
                  String.format("File is identical to original, overwrite will be skipped: %s",
                      filePath), logger, Level.INFO);
              return;
            }
          } catch (IOException e) {
            throw new CommandLineRunnerException(e);
          }
        }

        Printer.print(String.format("Overwriting existing data file record: %s", filePath), logger, Level.INFO);

        // Get the repository for the file's data type
        ModelRepository r;
        try {
          Optional<Object> optionalRepository = repositories.getRepositoryFor(dataSource.getModelType());
          if (optionalRepository.isPresent()){
            r = (ModelRepository) optionalRepository.get();
          } else {
            throw new ClassNotFoundException(
                String.format("No ModelRepository found for data source type: %s", 
                    dataSource.getModelType())
            );
          }
        } catch (ClassNotFoundException e){
          throw new CommandLineRunnerException(e);
        }

        // If possible, delete the associated records for the file
        if (r instanceof MetadataOperations) {
          ((MetadataOperations) r).deleteByDataSourceId(dataSource.getId());
        } else {
          Printer.print("Data is not over-writable.  Exiting.", logger, Level.WARN);
          return;
        }

        // Update the existing record
        dataSource.setDateCreated(dataSource.getDateCreated());
        dataSource.addAttributes(attributes);
        //dataFileRepository.delete(dataFile);
        if (parameters.getDataSourceId() != null) dataSource.setDataSourceId(parameters.getDataSourceId());
        
        Printer.print(String.format("Updating existing data file record: %s", dataSource.toString()),
            logger, Level.INFO);
        dataSource = dataSourceRepository.update(dataSource);
        
      }
    } 
    
    // New file
    else {

      try {
        dataSource = (DataSource) dataSourceRepository.getModel().newInstance();
      } catch (Exception e){
        throw new CommandLineRunnerException(e);
      }
      dataSource.setSource(filePath);
      dataSource.setSourceType(SourceTypes.FILE.toString());
      dataSource.setDataType(dataType);
      dataSource.setModel(processor.getModel());
      dataSource.setDataSetId(dataSet.getDataSetId());
      dataSource.setDateCreated(new Date());
      dataSource.setDateUpdated(new Date());
      dataSource.addAttributes(attributes);
      
      File file = new File(filePath);
      if (file.exists() && file.isFile()) {
        try {
          HashCode hashCode = Files.hash(file, Hashing.md5());
          dataSource.setChecksum(hashCode.toString());
        } catch (IOException e) {
          throw new CommandLineRunnerException(e);
        }
      }

      if (parameters.getDataSourceId() != null) dataSource.setDataSourceId(parameters.getDataSourceId());

      Printer.print(String.format("Registering new data file record: %s", dataSource.toString()),
          logger, Level.INFO);
      dataSource = dataSourceRepository.insert(dataSource);
      
    }

		// Configure the processor
		processor.setDataSet(dataSet);
    processor.setDataSource(dataSource);
    runProcessor(processor);
    Printer.print("File processing complete.", logger, Level.INFO);
    
	}

  /**
   * Execute the {@link RecordProcessor} on the supplied file.
   * 
   * @param processor
   * @throws CommandLineRunnerException
   */
	private void runProcessor(RecordProcessor processor) throws CommandLineRunnerException {
    
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
    
  }

  /**
   * Displays usage information for file import, and shows the available data types and {@link DataSet}
   *   identifiers for usage.
   */
	private void showHelp() {
    JCommander.newBuilder()
        .addCommand(ImportCommandParameters.COMMAND, new ImportCommandParameters())
        .build()
        .usage();
    System.out.println("\nAvailable data types:");
    System.out.println("    Name  Description");
    System.out.println("    ----  -----------");
    for (Map.Entry<String, String> entry: new TreeMap<>(processorRegistry.getDataTypeDescriptionMap()).entrySet()){
      System.out.println(String.format("    %s: %s", entry.getKey(), entry.getValue()));
    }
    System.out.println("\nAvailable data sets:");
    System.out.println("    ShortName  ID");
    System.out.println("    ----  -----------");
    for (DataSet dataSet: (List<DataSet>) dataSetRepository.findAll(new Sort(Direction.ASC, "dataSetId"))){
      System.out.println(String.format("    %s: %s", dataSet.getDataSetId(), dataSet.getId()));
    }
  }
	
  private void updateDataImportProperties(ImportCommandParameters parameters){
	  dataImportProperties.setSkipInvalidDataSource(parameters.isSkipInvalidSource());
    dataImportProperties.setSkipInvalidGenes(parameters.isSkipInvalidGenes());
    dataImportProperties.setSkipInvalidRecords(parameters.isSkipInvalidRecords());
    dataImportProperties.setSkipInvalidSamples(parameters.isSkipInvalidSamples());
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
  public void setDataSourceRepository(DataSourceRepository dataSourceRepository) {
    this.dataSourceRepository = dataSourceRepository;
  }

  @Autowired
  public void setRepositories(Repositories repositories) {
    this.repositories = repositories;
  }
}
