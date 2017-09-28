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

import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.model.Model;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

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
  
  private LinkedHashMap<String, Integer> headerMap = new LinkedHashMap<>();
  private String delimiter = "\t";
  private ConversionService conversionService = new DefaultConversionService();

	/**
	 * {@link RecordReader#readRecord()}
	 */
	@Override
	public T readRecord() throws DataImportException {
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
			throw new DataImportException(e);
		}
		return null;
	}

  /**
   * 
   * @param line
   */
	protected void parseHeader(String line){
    headerMap = new LinkedHashMap<>();
    String[] bits = line.split(delimiter);
    for (int i = 0; i < bits.length; i++){
      headerMap.put(bits[i], i);
    }
  }

  /**
   * 
   * @param line
   * @param header
   * @param clazz
   * @param <S>
   * @return
   * @throws DataImportException
   */
  protected <S> S getColumnValue(String line, String header, Class<S> clazz) throws DataImportException {
	  if (!headerMap.containsKey(header)) {
	    throw new DataImportException(String.format("Given header does not exist in this file: %s", header));
    }
	  String[] bits = line.split(this.getDelimiter());
	  Integer index = headerMap.get(header);
	  if (bits.length <= index){
      throw new DataImportException(String.format("Header index is outside bounds of current line.  " 
          + "Index = %d, line length = %d", index, bits.length));
    }
    if (!conversionService.canConvert(String.class, clazz)){
	    throw new DataImportException(String.format("Cannot convert string value to %s", clazz.getName()));
    }
    return conversionService.convert(bits[index].trim(), clazz);
  }

  /**
   * 
   * @param line
   * @param header
   * @return
   * @throws DataImportException
   */
  protected String getColumnValue(String line, String header) throws DataImportException {
    return getColumnValue(line, header, String.class);
  }

	/**
	 * Parses a line of text and returns a single model record.  Should return null if the line does 
	 *   not contain a valid record.
	 * 
	 * @param line
	 * @return
	 */
	abstract protected T getRecordFromLine(String line) throws DataImportException ;

	/**
	 * Performs a test to see if the line should be skipped.
	 * 
	 * @param line
	 * @return
	 */
	abstract protected boolean isSkippableLine(String line);

  /**
   * Tests whether the supplied line is a header.
   * 
   * @param line
   * @return
   */
	abstract protected boolean isHeaderLine(String line);

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public ConversionService getConversionService() {
    return conversionService;
  }

  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  public Map<String, Integer> getHeaderMap() {
    return headerMap;
  }

  public void setHeaderMap(LinkedHashMap<String, Integer> headerMap) {
    this.headerMap = headerMap;
  }
}
