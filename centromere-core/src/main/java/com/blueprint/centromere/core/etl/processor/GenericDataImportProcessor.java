/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.core.etl.processor;

import com.blueprint.centromere.core.etl.DataImportException;
import com.blueprint.centromere.core.etl.PipelineComponent;
import com.blueprint.centromere.core.etl.reader.RecordReader;
import com.blueprint.centromere.core.etl.writer.RecordWriter;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

/**
 * Basic {@link DataProcessor} implementation, which can be used to handle most file import jobs.
 *   The {@link #doBefore(File, Map)}, {@link #doOnSuccess(File, Map)}, and {@link #doOnFailure(File, Map)} 
 *   methods can be overridden to handle data set or data file metadata persistence, pre/post-processing, 
 *   or other maintenance tasks. 
 * 
 * @author woemler
 */
public class GenericDataImportProcessor<T extends Model<?>> 
		implements DataProcessor<T>, ModelSupport<T> {

  private static final Logger logger = LoggerFactory.getLogger(GenericDataImportProcessor.class);
  
	private final Class<T> model;
	private final RecordReader<T> reader;
	private final RecordWriter<T> writer;

  private Validator validator = null;
	private boolean isConfigured = false;
	private boolean isInFailedState = false;
	private Integer recordCount = 0;

  public GenericDataImportProcessor(Class<T> model,
      RecordReader<T> reader, RecordWriter<T> writer) {
    this.model = model;
    this.reader = reader;
    this.writer = writer;
  }

  public GenericDataImportProcessor(Class<T> model,
      RecordReader<T> reader, RecordWriter<T> writer,
      Validator validator) {
    this.model = model;
    this.reader = reader;
    this.writer = writer;
    this.validator = validator;
  }

  /**
   * Performs configuration steps prior to each execution of {@link #processFile(File, Map)}.  Assigns
   *   dataImportProperties and metadata objects to the individual processing components that are expecting them.
   */
	@SuppressWarnings("unchecked")
	@Override
	public void doBefore(File file, Map<String, String> args) throws DataImportException {
	  isInFailedState = false;
		isConfigured = true;
    recordCount = 0;
	}

  /**
   * To be executed after the main component method is called for the last time.  Handles job cleanup
   *   and association of metadata records.
   */
  @Override
  public void doOnSuccess(File file, Map<String, String> args) throws DataImportException {
    
  }

  /**
   * Executes if the {@link #processFile(File, Map)} method fails to execute properly, in place of the
   * {@link #doOnSuccess(File, Map)} method.
   */
  @Override
  public void doOnFailure(File file, Map<String, String> args) throws DataImportException {
    
  }

  /**
	 * {@link DataProcessor#processFile(File, Map)}
	 */
  @Override
	public void processFile(File file, Map<String, String> args) throws DataImportException {
    
    try {

      recordCount = 0;
      
      if (!isConfigured)
        throw new DataImportException("Processor configuration method has not run!"); 

      if (isInFailedState) {
        throw new DataImportException("Record processor is in failed state and is aborting run.");
      }

      runComponentDoBefore(file, args);

      if (isInFailedState) {
        logger.warn("Record processor is in failed state and is aborting run.");
        return;
      }
    
      processRecords(file, args);

      if (isInFailedState) {
        logger.warn("Record processor is in failed state and is aborting run.");
        return;
      }

      runComponentDoOnSuccess(file, args);

      logger.info(
          String.format("Successfully processed %d records from data source: %s", recordCount, file.getAbsolutePath()));

    } catch (Exception ex){
      isInFailedState = true;
      runComponentDoOnFailure(file, args);
      throw ex;
    }
		
	}

  /**
   * Runs all of the {@link PipelineComponent#doBefore(File, Map)} methods, 
   *   and throws appropriate exceptions if problems are encountered.
   * 
   * @throws DataImportException
   */
	protected void runComponentDoBefore(File file, Map<String, String> args) throws DataImportException {
    logger.debug("Running doBefore method for processor components.");
    reader.doBefore(file, args);
    writer.doBefore(file, args);
  }

  /**
   * Runs all of the {@link PipelineComponent#doOnSuccess(File, Map)} 
   *   methods, and throws appropriate exceptions if problems are encountered.
   *
   * @throws DataImportException
   */
  protected void runComponentDoOnSuccess(File file, Map<String,String> args) throws DataImportException {
    logger.debug("Running doOnSuccess methods for processor components.");
    writer.doOnSuccess(file, args);
    reader.doOnSuccess(file, args);
  }

  /**
   * Runs all of the {@link PipelineComponent#doOnFailure(File, Map)}
   *    methods, and throws appropriate exceptions if problems are encountered.
   * 
   * @param file
   * @param args
   * @throws DataImportException
   */
  protected void runComponentDoOnFailure(File file, Map<String,String> args) throws DataImportException {
    logger.info("Running doAfter methods for processor components.");
    writer.doOnFailure(file, args);
    reader.doOnFailure(file, args);
  }

  /**
   * Processes all of the incoming records.  Filters and validates records, if the appropriate
   *   components are set.
   * 
   * @throws DataImportException
   */
  protected void processRecords(File file, Map<String, String> args) throws DataImportException {
    
    logger.debug("Processing records.");
    
    T record = reader.readRecord();
    
    // Process each record
    while (record != null) {

      recordCount++;

      if (validator != null) {
        DataBinder dataBinder = new DataBinder(record);
        dataBinder.setValidator(validator);
        dataBinder.validate();
        BindingResult bindingResult = dataBinder.getBindingResult();
        if (bindingResult.hasErrors()) {
          logger.warn(String.format("Record failed validation: %s", record.toString()));
          if (Boolean.parseBoolean(args.getOrDefault("skip-invalid-records", "false"))) {
            record = reader.readRecord();
            continue;
          } else {
            isInFailedState = true;
            throw new DataImportException(bindingResult.toString());
          }
        }
      }
      writer.writeRecord(record);
      
      record = reader.readRecord();
      
    }
    
  }

  public Class<T> getModel() {
		return model;
	}

  public RecordReader<T> getReader() {
		return reader;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

  public RecordWriter<T> getWriter() {
		return writer;
	}

  public boolean isInFailedState() {
		return isInFailedState;
	}

	public void setInFailedState(boolean inFailedState) {
		isInFailedState = inFailedState;
	}
}
