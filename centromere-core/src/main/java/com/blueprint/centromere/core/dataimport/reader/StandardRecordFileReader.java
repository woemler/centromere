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

import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.model.Model;
import java.io.IOException;

/**
 * Standard text file reader that assumes that a single record is extractable from a single line.  
 *   Some lines may be skipped, if they pass the test implemented in {@code isSkippableLine}.  The
 *   code for extracting a single record from a line should be implemented in {@code getRecordFromLine},
 *   and should return null if an invalid record is present.
 * 
 * @author woemler
 * @since 0.4.3
 */
public abstract class StandardRecordFileReader<T extends Model<?>> 
		extends AbstractRecordFileReader<T> {

	/**
	 * {@link RecordReader#readRecord()}
	 */
	@Override
	public T readRecord()  {
		try {
			String line = this.getReader().readLine();
			while (line != null) {
				if (!isSkippableLine(line)) {
				  if (isHeaderLine(line)){
            parseHeader(line);
          } else {
            T record = getRecordFromLine(line);
            if (record != null) {
              return record;
            }
          }
				}
				line = this.getReader().readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void parseHeader(String line){

  }

	/**
	 * Parses a line of text and returns a single model record.  Should return null if the line does 
	 *   not contain a valid record.
	 * 
	 * @param line
	 * @return
	 */
	abstract protected T getRecordFromLine(String line) ;

	/**
	 * Performs a test to see if the line should be skipped.
	 * 
	 * @param line
	 * @return
	 */
	abstract protected boolean isSkippableLine(String line);

	protected boolean isHeaderLine(String line){
		return false;
	}
	
}
