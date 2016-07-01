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

import org.oncoblocks.centromere.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Basic abstract implementation of {@link RecordWriter}, for writing records to temp files.  Handles the 
 *   file object opening and closing in the {@code doBefore} and {@code doAfter} methods, respectively.
 * 
 * @author woemler
 */
public abstract class AbstractRecordFileWriter<T extends Model<?>> implements RecordWriter<T> {
	
	private FileWriter writer;
	private static final Logger logger = LoggerFactory.getLogger(AbstractRecordFileWriter.class);

	/**
	 * Opens a new output file for writing.
	 * 
 	 * @param args
	 * @throws DataImportException
	 */
	@Override
	public void doBefore(Object... args) throws DataImportException {
		try {
			Assert.notEmpty(args, "One or more arguments is required.");
			Assert.isTrue(args[0] instanceof String, "The first argument must be a String.");
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			throw new DataImportException(e.getMessage());
		}
		this.open((String) args[0]);
	}

	/**
	 * Closes the open file writer.
	 * 
	 * @param args
 	 * @throws DataImportException
	 */
	@Override
	public void doAfter(Object... args) throws DataImportException {
		this.close();
	}

	/**
	 * Creates or overwrites an output file, creates a {@link FileWriter} for writing records to the file.
	 * 
	 * @param outputFilePath
	 * @throws DataImportException
	 */
	public void open(String outputFilePath) throws DataImportException{
		outputFilePath = cleanFilePath(outputFilePath);
		this.close();
		try {
			writer = new FileWriter(outputFilePath);
		} catch (IOException e){
			e.printStackTrace();
			throw new DataImportException(String.format("Cannot open output file: %s", outputFilePath));
		}
	}

	/**
	 * Flushes outstanding records to the output file and then closes the file and its writer object.
	 */
	public void close(){
		try {
			writer.flush();
			writer.close();
		} catch (Exception e){
			logger.debug(e.getMessage());
		}
	}

	protected FileWriter getWriter() {
		return writer;
	}
	
	protected String cleanFilePath(String path){
		return path.replaceAll("\\s+", "_");
	}
}
