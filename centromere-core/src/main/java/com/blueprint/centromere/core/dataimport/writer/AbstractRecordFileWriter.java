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
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.blueprint.centromere.core.model.Model;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
	
	private FileWriter writer;
	private ImportOptions options = new ImportOptionsImpl();
	private DataFile dataFile;
	private DataSet dataSet;
	private Class<T> model;

  public AbstractRecordFileWriter() {
  }

  public AbstractRecordFileWriter(Class<T> model) {
    this.model = model;
  }

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
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			throw new DataImportException(e.getMessage());
		}

    String filePath;
		if (args[0] instanceof File){
		  filePath = ((File) args[0]).getAbsolutePath();
    } else if (args[0] instanceof String) {
		  filePath = getTempFilePath((String) args[0]);
    } else {
		  throw new DataImportException(String.format("Invalid output file path: %s", args[0].toString()));
    }
		
    if (args.length > 1 && args[1] instanceof DataFile){
		  this.dataFile = (DataFile) args[1];
    }

    if (args.length > 2 && args[2] instanceof DataSet){
      this.dataSet = (DataSet) args[2];
    }
		
		this.open(filePath);
		logger.info(String.format("Writing records to file: %s", filePath));
	}

  /**
   * {@link #doBefore(Object...)}
   *
   * @param outputFile
   * @param args
   * @throws DataImportException
   */
  public void doBefore(File outputFile, Object... args) throws DataImportException {
    List<Object> objects = Collections.singletonList(outputFile);
    objects.addAll(Arrays.asList(args));
    Object[] arguments = new Object[objects.size()];
    arguments = objects.toArray(arguments);
    doBefore(arguments);
  }

  /**
   * {@link #doBefore(Object...)}
   *
   * @param outputFile
   * @param dataFile
   * @param dataSet
   * @param args
   * @throws DataImportException
   */
  public void doBefore(File outputFile, DataFile dataFile, DataSet dataSet, Object... args)
      throws DataImportException {
    List<Object> objects = Arrays.asList(outputFile, dataFile, dataSet);
    objects.addAll(Arrays.asList(args));
    Object[] arguments = new Object[objects.size()];
    arguments = objects.toArray(arguments);
    doBefore(arguments);
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
	protected void open(String outputFilePath) throws DataImportException{
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
    File tempDir = new File(options.getTempFilePath());
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

  public Class<T> getModel() {
    return model;
  }

  public void setModel(Class<T> model) {
    this.model = model;
  }

  @Override
  public ImportOptions getImportOptions() {
    return options;
  }

  @Override
  public void setImportOptions(ImportOptions options) {
    this.options = options;
  }

  protected String cleanFilePath(String path){
		return path.replaceAll("\\s+", "_");
	}

}
