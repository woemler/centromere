/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.etl.reader;

import com.blueprint.centromere.core.exceptions.DataProcessingException;
import com.blueprint.centromere.core.model.Model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple abstract implementation of {@link RecordReader}, for reading input files.  Handles the 
 *   file object opening and closing in the {@code doBefore} and {@code doAfter} methods, respectively.
 * 
 * @author woemler
 */
public abstract class AbstractRecordFileReader<T extends Model<?>> implements RecordReader<T>{

  private static final Logger logger = LoggerFactory.getLogger(AbstractRecordFileReader.class);

	private BufferedReader reader = null;

  /**
	 * Closes any open reader and opens the new target file.  Assigns local variables, if available.
	 */
	@Override
	public void doBefore(File file, Map<String, String> args) throws DataProcessingException {
	  this.close();
		this.open(file);
	}

	/**
	 * Calls the close method on the reader.
	 */
	@Override
	public void doOnSuccess(File file, Map<String, String> args) throws DataProcessingException {
		this.close();
	}

  @Override
  public void doOnFailure(File file, Map<String, String> args) throws DataProcessingException {
    this.close();
  }

  /**
	 * Opens the target file and creates a {@link BufferedReader}, which can be referenced via its
	 *   getter method.
	 * 
	 * @param file
	 */
	protected void open(File file) throws DataProcessingException {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (IOException e){
			throw new InvalidDataSourceException(String.format("Cannot read file: %s", file.getAbsolutePath()), e);
		}
	}

	/**
	 * Closes the target file, if a reader exists.
	 */
	protected void close(){
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

}
