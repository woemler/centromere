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

package com.blueprint.centromere.core.dataimport;

import com.google.common.reflect.TypeToken;

import com.blueprint.centromere.core.model.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic {@link RecordProcessor} implementation, which can be used to handle most file import jobs.
 *   The {@code doBefore} and {@code doAfter} methods can be overridden to handle data set or data
 *   file metadata persistence, pre/post-processing, or other maintenance tasks. 
 * 
 * @author woemler
 */
public class GenericRecordProcessor<T extends Model<?>> 
		implements RecordProcessor<T>, DataTypeSupport {

	private Class<T> model;
	private RecordReader<T> reader;
	private Validator validator;
	private RecordWriter<T> writer;
	private RecordImporter importer;
	private Environment environment;
	private List<String> supportedDataTypes = new ArrayList<>();
	private boolean isConfigured = false;
	private boolean isInFailedState = false;
	private static final Logger logger = LoggerFactory.getLogger(GenericRecordProcessor.class);

	/**
	 * Performs configuration steps prior to each execution of {@link #run(Object...)}.  Assigns
	 *   options and metadata objects to the individual processing components that are expecting them.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doBefore(Object... args) throws DataImportException{
		Assert.notNull(environment, "Environment must not be null.");
		if (this.getClass().isAnnotationPresent(DataTypes.class)){
			DataTypes dataTypes = this.getClass().getAnnotation(DataTypes.class);
			if (dataTypes.value() != null && dataTypes.value().length > 0){
				this.supportedDataTypes = Arrays.asList(dataTypes.value());
			}
		}
		if (model == null){
			this.model = (Class<T>) new TypeToken<T>(getClass()) {}.getRawType();
		}
		if (writer != null) {
			writer.setEnvironment(environment);
		}
		if (reader != null) {
			reader.setEnvironment(environment);
		}
		if (importer != null) {
			importer.setEnvironment(environment);
		}
		isConfigured = true;
	}

	/**
	 * {@link RecordProcessor#run(Object...)}
	 * @param args
	 * @throws DataImportException
	 */
	public void run(Object... args) throws DataImportException {
		if (!isConfigured) logger.warn("Processor configuration method has not run!"); // TODO: Should this return or throw exception?
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
		reader.doBefore(args);
		writer.doBefore(args);
        if (importer != null) importer.doBefore(args);
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
					if (Boolean.parseBoolean(environment.getRequiredProperty("skip-invalid-records"))){
						record = reader.readRecord();
						continue;
					} else {
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
		writer.doAfter(args);
		reader.doAfter(args);
		if (importer != null) {
			importer.importFile(this.getTempFilePath(inputFilePath));
            importer.doAfter(args);
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
		File tempDir;
		if (!environment.containsProperty("centromere.import.temp-dir") 
				|| environment.getRequiredProperty("centromere.import.temp-dir") == null 
				|| "".equals(environment.getRequiredProperty("centromere.import.temp-dir"))){
			tempDir = new File(System.getProperty("java.io.tmpdir"));
		} else {
			tempDir = new File(environment.getRequiredProperty("centromere.import.temp-dir"));
		}
		String fileName = "centromere.import.tmp";
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

	@Autowired
	@Override 
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public boolean isInFailedState() {
		return isInFailedState;
	}

	public void setInFailedState(boolean inFailedState) {
		isInFailedState = inFailedState;
	}
}
