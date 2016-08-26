/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.core.dataimport;

import com.google.common.reflect.TypeToken;
import org.oncoblocks.centromere.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic {@link RecordProcessor} implementation, which can be used to handle most file import jobs.
 *   The {@code doBefore} and {@code doAfter} methods can be overridden to handle data set or data
 *   file metadata persistence, pre/post-processing, or other maintenance tasks.  Uses a basic
 *   {@link BasicImportOptions} instance to set import parameters, and identify the directory to store
 *   all temporary files.
 * 
 * @author woemler
 */
public class GenericRecordProcessor<T extends Model<?>> 
		implements RecordProcessor<T>, ImportOptionsAware, DataTypeSupport {

	private Class<T> model;
	private RecordReader<T> reader;
	private Validator validator;
	private RecordWriter<T> writer;
	private RecordImporter importer;
	private BasicImportOptions options = new BasicImportOptions();
	private List<String> supportedDataTypes = new ArrayList<>();
	private boolean isConfigured = false;
	private boolean isInFailedState = false;
	private static final Logger logger = LoggerFactory.getLogger(GenericRecordProcessor.class);

	/**
	 * To be executed before the main component method is first called.  Can be configured to handle
	 * a variety of tasks using flexible input parameters.
	 *
	 * @param args an array of objects of any type.
	 * @throws DataImportException
	 */
	@Override 
	public void doBefore(Object... args) throws DataImportException {
	}

	/**
	 * Performs configuration steps after bean creation.  Assigns options and metadata objects to the 
	 *   individual processing components that are expecting them.
	 */
	@PostConstruct
	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet(){
		if (this.getClass().isAnnotationPresent(DataTypes.class)){
			DataTypes dataTypes = this.getClass().getAnnotation(DataTypes.class);
			if (dataTypes.value() != null && dataTypes.value().length > 0){
				this.supportedDataTypes = Arrays.asList(dataTypes.value());
			}
		}
		if (model == null){
			this.model = (Class<T>) new TypeToken<T>(getClass()) {}.getRawType();
		}
		if (writer != null && writer instanceof ImportOptionsAware) {
			((ImportOptionsAware) writer).setImportOptions(options);
		}
		if (reader != null && reader instanceof ImportOptionsAware) {
			((ImportOptionsAware) reader).setImportOptions(options);
		}
		if (importer != null && importer instanceof ImportOptionsAware) {
			((ImportOptionsAware) importer).setImportOptions(options);
		}
		isConfigured = true;
	}

	/**
	 * To be executed after the main component method is called for the last time.  Can be configured
	 * to handle a variety of tasks using flexible input parameters.
	 *
	 * @param args an array of objects of any type.
	 * @throws DataImportException
	 */
	@Override 
	public void doAfter(Object... args) throws DataImportException {
		
	}

	/**
	 * {@link RecordProcessor#run(Object...)}
	 * @param args
	 * @throws DataImportException
	 */
	public void run(Object... args) throws DataImportException {
		if (!isConfigured) logger.warn("Processor configuration method has not run!");
		if (isInFailedState) {
			logger.warn("Record processor is in failed state and is aborting run.");
			return;
		}
		try {
			Assert.notEmpty(args, "One or more arguments required.");
			Assert.isTrue(args[0] instanceof String, "The first argument must be a String.");
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			throw new DataImportException(e.getMessage());
		}
		String inputFilePath = (String) args[0];
		reader.doBefore(inputFilePath);
		writer.doBefore(this.getTempFilePath(inputFilePath));
		if (isInFailedState) {
			logger.warn("Record processor is in failed state and is aborting run.");
			return;
		}
		T record = reader.readRecord();
		while (record != null) {
			if (validator != null) {
				DataBinder dataBinder = new DataBinder(record);
				dataBinder.setValidator(validator);
				dataBinder.validate();
				BindingResult bindingResult = dataBinder.getBindingResult();
				if (bindingResult.hasErrors()){
					logger.warn(String.format("Record failed validation: %s", record.toString()));
					if (!options.isSkipInvalidRecords()){
						throw new DataImportException(bindingResult.toString());
					}
				}
			}
			writer.writeRecord(record);
			record = reader.readRecord();
		}
		if (isInFailedState) {
			logger.warn("Record processor is in failed state and is aborting run.");
			return;
		}
		writer.doAfter();
		reader.doAfter();
		if (importer != null) {
			importer.importFile(this.getTempFilePath(inputFilePath));
		}
	}


	/**
	 * Returns the path of the temporary file to be written, if necessary.  Uses the input file's name
	 *   and the pre-determined temp file directory to generate the name, so as to overwrite previous
	 *   jobs' temp file.
	 * @param inputFilePath
	 * @return
	 */
	private String getTempFilePath(String inputFilePath){
		File tempDir = new File(options.getTempDirectoryPath());
		String fileName = new File(inputFilePath).getName() + ".tmp";
		File tempFile = new File(tempDir, fileName);
		return tempFile.getPath();
	}

	public boolean isSupportedDataType(String dataType) {
		return supportedDataTypes.contains(dataType);
	}

	public void setSupportedDataTypes(Iterable<String> dataTypes) {
		List<String> types = new ArrayList<>();
		for (String type: dataTypes){
			types.add(type);
		}
		this.supportedDataTypes = types;
	}

	public List<String> getSupportedDataTypes() {
		return supportedDataTypes;
	}

	public Class<T> getModel() {
		return model;
	}

	public void setModel(Class<T> model) {
		this.model = model;
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

	public ImportOptions getImportOptions() {
		return options;
	}

	public void setImportOptions(ImportOptions options) {
		this.options = new BasicImportOptions(options);
	}
	
	public void setImportOptions(BasicImportOptions options){
		this.options = options;
	}

	public boolean isInFailedState() {
		return isInFailedState;
	}

	public void setInFailedState(boolean inFailedState) {
		isInFailedState = inFailedState;
	}
}
