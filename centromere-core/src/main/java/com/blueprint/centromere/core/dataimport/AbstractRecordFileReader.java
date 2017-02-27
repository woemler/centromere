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

import com.blueprint.centromere.core.model.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Simple abstract implementation of {@link RecordReader}, for reading input files.  Handles the 
 *   file object opening and closing in the {@code doBefore} and {@code doAfter} methods, respectively.
 * 
 * @author woemler
 */
public abstract class AbstractRecordFileReader<T extends Model<?>> implements RecordReader<T> {
	
	private BufferedReader reader;
	private Environment environment;
	private static final Logger logger = LoggerFactory.getLogger(AbstractRecordFileReader.class);
	private Class<T> model;

	/**
	 * Closes any open readers and opens the new target file.
	 * 
	 * @param args
	 * @throws DataImportException
	 */
	@Override
	public void doBefore(Object... args) throws DataImportException{
		this.close();
		Assert.isTrue(args.length > 0, "Must be at least one argument.");
		String path;
		if (args[0] instanceof String){
			path = (String) args[0];
		} else if (args[0] instanceof File){
			path = ((File) args[0]).getAbsolutePath();
		} else {
			throw new DataImportException("No valid file or file path submitted.");
		}
		this.open(path);
	}

	/**
	 * Calls the close method on the reader.
	 */
	@Override
	public void doAfter(Object... args) {
		this.close();
	}

	/**
	 * Opens the target file and creates a {@link BufferedReader}, which can be referenced via its
	 *   getter method.
	 * 
	 * @param inputFilePath
	 * @throws DataImportException
	 */
	public void open(String inputFilePath) throws DataImportException{
		File file = new File(inputFilePath);
		if (!file.canRead() || !file.isFile()){
			try {
				file = new File(ClassLoader.getSystemClassLoader().getResource(inputFilePath).getPath());
			} catch (NullPointerException e){
				throw new DataImportException(String.format("Cannot locate file: %s", inputFilePath));
			}
		}
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (IOException e){
			e.printStackTrace();
			throw new DataImportException(String.format("Cannot read file: %s", inputFilePath));
		}
	}

	/**
	 * Closes the target file, if a reader exists.
	 */
	public void close(){
		if (reader != null){
			try {
				reader.close();
			} catch (IOException e){
				logger.debug(e.getMessage());
			}
		}
	}

	protected BufferedReader getReader() {
		return reader;
	}

	public Environment getEnvironment() {
		return environment;
	}

	@Autowired
	@Override 
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Class<T> getModel() {
			return model;
	}

	@Override
	public void setModel(Class<T> model) {
			this.model = model;
	}
}
