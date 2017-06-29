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

package com.blueprint.centromere.core.dataimport.reader;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetAware;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Simple abstract implementation of {@link RecordReader}, for reading input files.  Handles the 
 *   file object opening and closing in the {@code doBefore} and {@code doAfter} methods, respectively.
 * 
 * @author woemler
 */
public abstract class AbstractRecordFileReader<T extends Model<?>> 
    implements RecordReader<T>, DataSetAware, DataFileAware, ModelSupport<T> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractRecordFileReader.class);
  
	private BufferedReader reader;
	private DataFile dataFile;
	private DataSet dataSet;
	private ImportOptions options;
	private Class<T> model;

  /**
	 * Closes any open reader and opens the new target file.  Assigns local variables, if available.
	 */
	@Override
	public void doBefore() {
		
	  this.close();

    try {
      afterPropertiesSet();
      Assert.notNull(dataSet, "DataSet record is not set.");
      Assert.notNull(dataSet.getId(), "DataSet record has not been persisted to the database.");
      Assert.notNull(dataFile, "DataFile record is not set");
      Assert.notNull(dataFile.getId(), "DataFile record has not been persisted to the database.");
      Assert.notNull(dataFile.getFilePath(), "No DataFile file path has been set");
      Assert.notNull(options, "Import options has not been set.");
    } catch (Exception e){
      throw new DataImportException(e);
    }

    String path = dataFile.getFilePath();
		this.open(path);
		
	}

	/**
	 * Calls the close method on the reader.
	 */
	@Override
	public void doAfter() {
		this.close();
	}

	/**
	 * Opens the target file and creates a {@link BufferedReader}, which can be referenced via its
	 *   getter method.
	 * 
	 * @param inputFilePath
	 */
	protected void open(String inputFilePath) {
		File file = new File(inputFilePath);
		if (!file.canRead() || !file.isFile()){
			try {
				file = new File(ClassLoader.getSystemClassLoader().getResource(inputFilePath).getPath());
			} catch (NullPointerException e){
				throw new DataImportException(String.format("Cannot locate file: %s", inputFilePath), e);
			}
		}
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (IOException e){
			throw new DataImportException(String.format("Cannot read file: %s", inputFilePath), e);
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

  public DataFile getDataFile() {
    return dataFile;
  }

  public void setDataFile(DataFile dataFile) {
    this.dataFile = dataFile;
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  @Override
  public ImportOptions getImportOptions() {
    return options;
  }

  @Override
  public void setImportOptions(ImportOptions importOptions) {
    this.options = importOptions;
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
