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

package com.blueprint.centromere.core.dataimport.writer;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetAware;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.exception.InvalidDataFileException;
import com.blueprint.centromere.core.model.Model;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Basic abstract implementation of {@link RecordWriter}, for writing records to temp files.  Handles the 
 *   file object opening and closing in the {@code doBefore} and {@code doAfter} methods, respectively.
 * 
 * @author woemler
 */
public abstract class AbstractRecordFileWriter<T extends Model<?>> 
		implements RecordWriter<T>, TempFileWriter, DataSetAware, DataFileAware {
  
  private static final Logger logger = LoggerFactory.getLogger(AbstractRecordFileWriter.class);

  private final DataImportProperties dataImportProperties;
  
	private FileWriter writer;
	private DataFile dataFile;
	private DataSet dataSet;

  public AbstractRecordFileWriter(DataImportProperties dataImportProperties) {
    this.dataImportProperties = dataImportProperties;
  }

  /**
	 * Opens a new output file for writing.
	 */
	@Override
	public void doBefore() throws DataImportException {

    try {
      Assert.notNull(dataSet, "DataSet record is not set.");
      Assert.notNull(dataSet.getId(), "DataSet record has not been persisted to the database.");
      Assert.notNull(dataFile, "DataFile record is not set");
      Assert.notNull(dataFile.getId(), "DataFile record has not been persisted to the database.");
      Assert.notNull(dataFile.getFilePath(), "No DataFile file path has been set");
    } catch (Exception e){
      throw new DataImportException(e);
    }

    String filePath = getTempFilePath(dataFile.getFilePath());
    this.open(filePath);
		logger.info(String.format("Writing records to file: %s", filePath));
	}

	/**
	 * Closes the open file writer.
	 */
	@Override
	public void doAfter() throws DataImportException  {
		this.close();
	}

	/**
	 * Creates or overwrites an output file, creates a {@link FileWriter} for writing records to the file.
	 * 
	 * @param outputFilePath
	 */
	protected void open(String outputFilePath) throws DataImportException {
		outputFilePath = cleanFilePath(outputFilePath);
		this.close();
		try {
			writer = new FileWriter(outputFilePath);
		} catch (IOException e){
			throw new InvalidDataFileException(String.format("Cannot open output file: %s", outputFilePath), e);
		}
	}

	/**
	 * Flushes outstanding records to the output file and then closes the file and its writer object.
	 */
	protected void close(){
		try {
			writer.flush();
			writer.close();
		} catch (Exception e){
			logger.debug(e.getMessage());
		}
	}

  /**
   * Returns the path of the temporary file to be written, if necessary.  Uses the input file's name
   *   and the pre-determined temp file directory to generate the name, so as to overwrite previous
   *   jobs' temp file.
   * @param inputFilePath
   * @return
   */
  @Override
  public String getTempFilePath(String inputFilePath) throws DataImportException {
    File tempDir;
    if (dataImportProperties.getTempDir() != null && !dataImportProperties.getTempDir().trim().equals("")) {
      tempDir = new File(dataImportProperties.getTempDir());
    } else {
      tempDir = new File(System.getProperty("java.io.tmpdir"));
    }
    if (!tempDir.isDirectory() || !tempDir.canWrite()){
      throw new DataImportException(String.format("Unable to read or write to temp directory: %s",
          tempDir.getAbsolutePath()));
    }
    String fileName = "centromere.import.tmp";
    File tempFile = new File(tempDir, fileName);
    return tempFile.getPath();
  }

	protected FileWriter getWriter() {
		return writer;
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

  protected String cleanFilePath(String path){
		return path.replaceAll("\\s+", "_");
	}

  public DataImportProperties getDataImportProperties() {
    return dataImportProperties;
  }
}
