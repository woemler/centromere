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

package com.blueprint.centromere.core.dataimport.reader;

import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.model.Model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple text file reader that parses multiple records from a single line.  The identification of the 
 *   the records in each line is assumed to come from the header row, which is parsed and stored when
 *   encountered in the {@code parseHeader} method.  The {@code readRecord} method can extract multiple
 *   records from a single line and return them one-at-a-time, consistent with standard record reader.
 * 
 * @author woemler
 * @since 0.4.3
 */
public abstract class MultiRecordFileReader<T extends Model<?>> 
		extends AbstractRecordFileReader<T> {
	
	private List<T> recordList = new ArrayList<>();
	private List<String> headers;
	private boolean headerFlag = true;
	private String delimiter = "\\t";

	/**
	 * Initializes the header and record list objects.
	 */
	@Override 
	public void doBefore() throws DataImportException {
		super.doBefore();
		recordList = new ArrayList<>();
		headers = new ArrayList<>();
		headerFlag = true;
	}

	/**
	 * {@link RecordReader#readRecord()}
	 */
	@Override 
	public T readRecord() throws DataImportException {
		if (recordList.size() > 0){
			return recordList.remove(0);
		} else {
			try {
				String line = this.getReader().readLine();
				while (line != null){
					if (!isSkippableLine(line)) {
						if (isHeaderLine(line)){
							parseHeader(line);
							headerFlag = false;
						} else {
							recordList = getRecordsFromLine(line);
							if (recordList.size() > 0) return recordList.remove(0);
						}
					} 
					line = this.getReader().readLine();
				}
			} catch (Exception e){
				throw new DataImportException(e);
			}
		}
		return null;
	}

	/**
	 * Extracts the column names from the header line in the file. 
	 * 
	 * @param line
	 */
	protected void parseHeader(String line) throws DataImportException {
		headers = Arrays.asList(line.trim().split(delimiter));
	}

  /**
   * Tests whether the supplied line is a header.
   * 
   * @param line
   * @return
   */
	protected boolean isHeaderLine(String line){
	  return headerFlag;
  }

	/**
	 * Extracts multiple records from a single line of the text file.  If no valid records are found, 
	 *   an empty list should be returned. 
	 * 
	 * @param line
	 * @return
	 */
	abstract protected List<T> getRecordsFromLine(String line) throws DataImportException ;

	/**
	 * Tests whether a given line should be skipped.
	 * 
	 * @param line
	 * @return
	 */
	abstract protected boolean isSkippableLine(String line);

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	protected String getDelimiter() {
		return delimiter;
	}
	
	protected List<String> getHeaders(){
		return headers;
	}
}
