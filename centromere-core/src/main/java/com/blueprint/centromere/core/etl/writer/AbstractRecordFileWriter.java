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

package com.blueprint.centromere.core.etl.writer;

import com.blueprint.centromere.core.etl.DataImportException;
import com.blueprint.centromere.core.etl.reader.InvalidDataSourceException;
import com.blueprint.centromere.core.model.Model;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic abstract implementation of {@link RecordWriter}, for writing records to temp files.  Handles the 
 *   file object opening and closing in the {@code doBefore} and {@code doAfter} methods, respectively.
 * 
 * @author woemler
 */
public abstract class AbstractRecordFileWriter<T extends Model<?>> 
		implements RecordWriter<T>, TempFileWriter {
  
  private static final Logger logger = LoggerFactory.getLogger(AbstractRecordFileWriter.class);
  
	private FileWriter writer = null;

  /**
	 * Opens a new output file for writing.
	 */
	@Override
	public void doBefore(File file, Map<String, String> args) throws DataImportException {
    File tempFile = this.getTempFile(file); 
    this.open(tempFile);
		logger.info(String.format("Writing records to file: %s", tempFile.getAbsolutePath()));
	}

	/**
	 * Closes the open file writer.
	 */
	@Override
	public void doOnSuccess(File file, Map<String, String> args) throws DataImportException  {
		this.close();
	}

  @Override
  public void doOnFailure(File file, Map<String, String> args) throws DataImportException {
    this.close();
  }

  /**
	 * Creates or overwrites an output file, creates a {@link FileWriter} for writing records to the file.
	 * 
	 * @param tempFile
	 */
	protected void open(File tempFile) throws DataImportException {
		this.close();
		try {
			writer = new FileWriter(tempFile);
		} catch (IOException e){
			throw new InvalidDataSourceException(String.format("Cannot open output file: %s", tempFile.getAbsolutePath()), e);
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
   * @param inputFile
   * @return
   */
  @Override
  public File getTempFile(File inputFile) throws DataImportException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    if (!tempDir.isDirectory() || !tempDir.canWrite()){
      throw new DataImportException(String.format("Unable to read or write to temp directory: %s",
          tempDir.getAbsolutePath()));
    }
    String fileName = "centromere.import.tmp";
    File tempFile = new File(tempDir, fileName);
    return tempFile;
  }

	protected FileWriter getWriter() {
		return writer;
	}
	
}
