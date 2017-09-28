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

package com.blueprint.centromere.core.dataimport.processor;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataOperations;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetAware;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.dataimport.DataImportComponent;
import com.blueprint.centromere.core.dataimport.DataTypeSupport;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.Option;
import com.blueprint.centromere.core.dataimport.Options;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidDataFileException;
import com.blueprint.centromere.core.dataimport.exception.InvalidGeneException;
import com.blueprint.centromere.core.dataimport.exception.InvalidSampleException;
import com.blueprint.centromere.core.dataimport.filter.Filter;
import com.blueprint.centromere.core.dataimport.importer.RecordImporter;
import com.blueprint.centromere.core.dataimport.reader.RecordReader;
import com.blueprint.centromere.core.dataimport.writer.RecordWriter;
import com.blueprint.centromere.core.dataimport.writer.TempFileWriter;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

/**
 * Basic {@link RecordProcessor} implementation, which can be used to handle most file import jobs.
 *   The {@code doBefore} and {@code doAfter} methods can be overridden to handle data set or data
 *   file metadata persistence, pre/post-processing, or other maintenance tasks. 
 * 
 * @author woemler
 */
public class GenericRecordProcessor<T extends Model<?>> 
		implements RecordProcessor<T>, DataTypeSupport, DataFileAware, DataSetAware {

  private static final Logger logger = LoggerFactory.getLogger(GenericRecordProcessor.class);

  private SubjectRepository subjectRepository;
  private DataSetRepository dataSetRepository;
  private DataFileRepository dataFileRepository;
  private ModelRepositoryRegistry registry;
  private DataImportProperties dataImportProperties;
  
	private Class<T> model;
	
	private RecordReader<T> reader;
	private Validator validator;
	private Filter<T> filter;
	private RecordWriter<T> writer;
	private RecordImporter importer;
	
	private DataFile dataFile;
	private DataSet dataSet;
	
	private List<String> supportedDataTypes = new ArrayList<>();
	
	private boolean isConfigured = false;
	private boolean isInFailedState = false;
	private Integer recordCount = 0;

	/**
	 * Empty default implementation.  The purpose of extending {@link org.springframework.beans.factory.InitializingBean}
   * is to trigger bean post-processing by a {@link BeanPostProcessor}.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(model, "Model must not be null");
		if (this.getClass().isAnnotationPresent(DataTypes.class)){
      DataTypes dataTypes = this.getClass().getAnnotation(DataTypes.class);
      if (dataTypes.value().length > 0){
        this.supportedDataTypes = Arrays.asList(dataTypes.value());
      }
    }
	}

  /**
   * Performs configuration steps prior to each execution of {@link #run()}.  Assigns
   *   dataImportProperties and metadata objects to the individual processing components that are expecting them.
   */
	@SuppressWarnings("unchecked")
	@Override
	public void doBefore() throws DataImportException {
	  
	  // Checks that DataSet, DataFile, and ImportOptions are set
	  try {
      afterPropertiesSet();
      Assert.notNull(dataSet, "DataSet record is not set.");
      Assert.notNull(dataSet.getId(), "DataSet record has not been persisted to the database.");
      Assert.notNull(dataFile, "DataFile record is not set");
      Assert.notNull(dataFile.getId(), "DataFile record has not been persisted to the database.");
      Assert.notNull(dataFile.getFilePath(), "No DataFile file path has been set");
      Assert.notNull(dataImportProperties, "Import DataImportProperties has not been set.");
    } catch (Exception e){
	    throw new DataImportException(e);
    }
    
    // Passes along DataSet, DataFile, and ImportOptions to components
		if (writer != null) {
    	if (writer instanceof DataSetAware) ((DataSetAware) writer).setDataSet(dataSet);
    	if (writer instanceof DataFileAware) ((DataFileAware) writer).setDataFile(dataFile);
		}
		if (reader != null) {
      if (reader instanceof DataSetAware) ((DataSetAware) reader).setDataSet(dataSet);
      if (reader instanceof DataFileAware) ((DataFileAware) reader).setDataFile(dataFile);
    }
		if (importer != null) {
      if (importer instanceof DataSetAware) ((DataSetAware) importer).setDataSet(dataSet);
      if (importer instanceof DataFileAware) ((DataFileAware) importer).setDataFile(dataFile);
    }
    if (filter != null) {
      if (filter instanceof DataSetAware) ((DataSetAware) filter).setDataSet(dataSet);
      if (filter instanceof DataFileAware) ((DataFileAware) filter).setDataFile(dataFile);
    }
		
		isConfigured = true;
    
	}

  /**
   * To be executed after the main component method is called for the last time.  Handles job cleanup
   *   and association of metadata records.
   */
  @Override
  public void doAfter() throws DataImportException {
    
    // If DataFile contained Sample records, associate them with the proper Subject and DataSet record
    if (reader instanceof SampleAware) {
      List<Sample> samples = ((SampleAware) reader).getSamples();
      for (Sample sample : samples) {
        Subject subject = subjectRepository.findOne(sample.getSubjectId());
        List<String> sampleIds = subject.getSampleIds();
        if (!sampleIds.contains(sample.getId())) {
          sampleIds.add(sample.getId());
          subject.setSampleIds(new ArrayList<>(sampleIds));
          subjectRepository.update(subject);
        }
        sampleIds = dataSet.getSampleIds();
        if (!sampleIds.contains(sample.getId())) {
          sampleIds.add(sample.getId());
          dataSet.setSampleIds(new ArrayList<>(sampleIds));
          dataSetRepository.update(dataSet);
        }
      }
    }
    
    // Associate the DataFile with the appopriate DataSet record
    List<String> dataFileIds = dataSet.getDataFileIds();
    if (!dataFileIds.contains(dataFile.getId())) {
      dataFileIds.add(dataFile.getId());
      dataSet.setDataFileIds(new ArrayList<>(dataFileIds));
      dataSetRepository.update(dataSet);
    }
    
  }

  /**
   * Executes if the {@link #run()} method fails to execute properly, in place of the
   * {@link #doAfter()} method.
   */
  @Override
  public void doOnFailure() {

    // Roll back data records
    if (registry.isRegisteredModel(this.getModel())){
      ModelRepository repository = registry.getRepositoryByModel(this.getModel());
      if (repository instanceof DataOperations){
        DataOperations dataOperations = (DataOperations) repository;
        if (dataFile.getId() != null) {
          logger.warn(String.format("Rolling back inserted records for data file: %s", dataFile.getFilePath()));
          dataOperations.deleteByDataFileId(dataFile.getId());
        }
      }
    }
    
    // Delete dataFile records
    if (dataFile.getId() != null){
      logger.warn(String.format("Rolling back DataFile record for file: %s", dataFile.getFilePath()));
      dataFileRepository.delete(dataFile);
    }
    
  }

  /**
	 * {@link RecordProcessor#run()}
	 */
  @Override
	public void run() throws DataImportException {
    
    try {

      recordCount = 0;
      if (!isConfigured)
        logger.warn(
            "Processor configuration method has not run!"); // TODO: Should this return or throw exception?

      if (isInFailedState) {
        logger.warn("Record processor is in failed state and is aborting run.");
        return;
      }

      runComponentDoBefore();

      if (isInFailedState) {
        logger.warn("Record processor is in failed state and is aborting run.");
        return;
      }
    
      processRecords();

      if (isInFailedState) {
        logger.warn("Record processor is in failed state and is aborting run.");
        return;
      }

      runComponentDoAfter();

      logger.info(
          String.format("Successfully processed %d records from file: %s", recordCount, dataFile.getFilePath()));

    } catch (Exception ex){
      isInFailedState = true;
      throw ex;
    }
		
	}

  /**
   * Runs all of the {@link DataImportComponent#doBefore()} methods, and throws appropriate exceptions
   *   if problems are encountered.
   * 
   * @throws DataImportException
   */
	protected void runComponentDoBefore() throws DataImportException {
    logger.info("Running doBefore method for processor components.");
    try {
      reader.doBefore();
      writer.doBefore();
      if (importer != null) {
        importer.doBefore();
      }
      if (filter != null){
        filter.doBefore();
      }
    } catch (InvalidSampleException e){
      if (dataImportProperties.isSkipInvalidSamples()) isInFailedState = true;
      else throw e;
    } catch (InvalidDataFileException e){
      if (dataImportProperties.isSkipInvalidFiles()) isInFailedState = true;
      else throw e;
    } catch (InvalidGeneException e){
      if (dataImportProperties.isSkipInvalidGenes()) isInFailedState = true;
      else throw e;
    }
  }

  /**
   * Runs all of the {@link DataImportComponent#doAfter()} methods, and throws appropriate exceptions
   *   if problems are encountered.
   *
   * @throws DataImportException
   */
  protected void runComponentDoAfter() throws DataImportException {
    logger.info("Running doAfter methods for processor components.");
    try {
      writer.doAfter();
      reader.doAfter();
      if (filter != null){
        filter.doAfter();
      }
      if (importer != null) {
        importer.doAfter();
      }
    } catch (InvalidSampleException e){
      if (dataImportProperties.isSkipInvalidSamples()) isInFailedState = true;
      else throw e;
    } catch (InvalidDataFileException e){
      if (dataImportProperties.isSkipInvalidFiles()) isInFailedState = true;
      else throw e;
    } catch (InvalidGeneException e){
      if (dataImportProperties.isSkipInvalidGenes()) isInFailedState = true;
      else throw e;
    }
  }

  /**
   * Processes all of the incoming records.  Filters and validates records, if the appropriate
   *   components are set.
   * 
   * @throws DataImportException
   */
  protected void processRecords() throws DataImportException {
    
    logger.info("Processing records.");
    
    T record = reader.readRecord();
    
    // Process each record
    while (record != null) {
      
      if (filter != null && filter.isFilterable(record)){
        logger.info(String.format("Filtering record: %s", record.toString()));
      } else {
        if (validator != null) {
          DataBinder dataBinder = new DataBinder(record);
          dataBinder.setValidator(validator);
          dataBinder.validate();
          BindingResult bindingResult = dataBinder.getBindingResult();
          if (bindingResult.hasErrors()) {
            logger.warn(String.format("Record failed validation: %s", record.toString()));
            if (dataImportProperties.isSkipInvalidRecords()) {
              record = reader.readRecord();
              continue;
            } else {
              isInFailedState = true;
              throw new DataImportException(bindingResult.toString());
            }
          }
        }
        writer.writeRecord(record);
      }
      
      record = reader.readRecord();
      recordCount++;
      
    }
    
    // Import records when done
    if (importer != null) {
      if (TempFileWriter.class.isAssignableFrom(writer.getClass())) {
        logger.info("Running RecordImporter file import");
        String tempFilePath = ((TempFileWriter) writer).getTempFilePath(dataFile.getFilePath());
        importer.importFile(tempFilePath);
      } else {
        logger.warn(
            "RecordWriter instance does not implement TempFileWriter interface, cannot get"
                + " temp file path from component.");
      }
    }
    
  }

  /**
   * Tests whether the input dataType string value is processable by the component.
   * 
   * @param dataType
   * @return
   */
	public boolean isSupportedDataType(String dataType) {
		return supportedDataTypes.contains(dataType);
	}

  /**
   * Sets the supported data type strings.
   * 
   * @param dataTypes
   */
	public void setSupportedDataTypes(Iterable<String> dataTypes) {
		List<String> types = new ArrayList<>();
		for (String type: dataTypes){
			types.add(type);
		}
		this.supportedDataTypes = types;
	}

  /**
   * Returns all of the {@link Option} annotations associated with the processor and its components.
   */
  @Override
  public Collection<Option> getDataImportOptions() {
    Set<Option> options = new HashSet<>();
    options.addAll(getOptionsFromClass(this.getClass()));
    if (reader != null) options.addAll(getOptionsFromClass(reader.getClass()));
    if (writer != null) options.addAll(getOptionsFromClass(writer.getClass()));
    if (importer != null) options.addAll(getOptionsFromClass(importer.getClass()));
    return options;
  }
  
  private Collection<Option> getOptionsFromClass(Class<?> type){
    Set<Option> set = new HashSet<>();
    if (type.isAnnotationPresent(Options.class)){
      Options options = type.getAnnotation(Options.class);
      Collections.addAll(set, options.value());
    } 
    if (type.isAnnotationPresent(Option.class)){
      set.add(type.getAnnotation(Option.class));
    }
    return set;
  }

  public List<String> getSupportedDataTypes() {
		return supportedDataTypes;
	}

  @Autowired
  public void setSubjectRepository(SubjectRepository subjectRepository) {
    this.subjectRepository = subjectRepository;
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
  public void setRegistry(ModelRepositoryRegistry registry) {
    this.registry = registry;
  }

  @Autowired
  public void setDataImportProperties(DataImportProperties dataImportProperties) {
    this.dataImportProperties = dataImportProperties;
  }
  
  protected ModelRepositoryRegistry getRegistry(){
	  return registry;
  }

  public Class<T> getModel() {
		return model;
	}

	public void setModel(Class<T> model) {
		this.model = model;
	}

  public DataFile getDataFile() {
    return dataFile;
  }

  @Override
  public void setDataFile(DataFile dataFile) {
    this.dataFile = dataFile;
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  @Override
  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  public RecordReader<T> getReader() {
		return reader;
	}

	public void setReader(RecordReader<T> reader) {
		this.reader = reader;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

  @Override
  public Filter<T> getFilter() {
    return filter;
  }

  @Override
  public void setFilter(Filter<T> filter) {
    this.filter = filter;
  }

  public RecordWriter<T> getWriter() {
		return writer;
	}

	public void setWriter(RecordWriter<T> writer) {
		this.writer = writer;
	}

	public RecordImporter getImporter() {
		return importer;
	}

	public void setImporter(RecordImporter importer) {
		this.importer = importer;
	}

  @Override
  public boolean isInFailedState() {
		return isInFailedState;
	}

	public void setInFailedState(boolean inFailedState) {
		isInFailedState = inFailedState;
	}
}
