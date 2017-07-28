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
import com.blueprint.centromere.core.model.ModelSupport;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Generic {@link RecordReader} implementation that will use data file column headers to try to map
 *   column values to {@link Model} fields.  Uses Spring's {@link org.springframework.beans.BeanWrapper}
 *   and {@link ConversionService} to handle the text string-to-object conversion.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class BasicColumnMappingRecordReader<T extends Model<?>> extends AbstractRecordFileReader<T> 
    implements ModelSupport<T> {

	private static final Logger logger = LoggerFactory.getLogger(BasicColumnMappingRecordReader.class);
	
	private final Class<T> model;

  private ConversionService conversionService = new DefaultConversionService();
	private Map<String, Class<?>> fieldTypeMap = new HashMap<>(); // map of actual field name and types
	private Map<String, String> fieldNameMap = new HashMap<>(); // map of aliases and actual field names
	private Map<Integer, String> columnIndexMap = new HashMap<>(); // map of column indexes to mapped model field names
	private Map<String, String> columnMappings = new HashMap<>();
	private boolean headerFlag = true;
	private String delimiter = "\\t";

  public BasicColumnMappingRecordReader(Class<T> model) {
    this.model = model;
  }

  public BasicColumnMappingRecordReader(Class<T> model,
      ConversionService conversionService) {
    this.model = model;
    this.conversionService = conversionService;
  }

  @Override
  public void doBefore() throws DataImportException {
    super.doBefore();
    headerFlag = true;
  }

  /**
   * {@link AbstractRecordFileReader#readRecord()}
   * 
   * @return model record
   */
	@Override 
	public T readRecord() throws DataImportException {
		try {
			String line = this.getReader().readLine();
			while (line != null){
				if (!line.trim().equals("") && !line.startsWith("#")){
					if (headerFlag){
						parseHeader(line);
						headerFlag = false;
					} else {
						T record = getRecordFromLine(line);
						if (record != null){
							return record;
						}
					}
				}
				line = this.getReader().readLine();
			}
		} catch (IOException e){
			throw new DataImportException(e);
		}
		return null;
	}

	/**
	 * Builds two maps by inspecting the target {@link Model class}.  The first map links field and 
	 *   alias names to the actual field name to have data mapped to in the instantiated object.  The 
	 *   second map links the actual field name to the field type parsed data must be converted to.
	 *
	 */
	private void determineMappableModelFields()  {
		fieldNameMap = new HashMap<>();
		fieldTypeMap = new HashMap<>();
		Class<?> current = model;
		while (current.getSuperclass() != null){
			for (Field field: current.getDeclaredFields()){
				String fieldName = field.getName();
				fieldTypeMap.put(fieldName, field.getType());
				fieldNameMap.put(fieldName, fieldName);
			}
			current = current.getSuperclass();
		}
		
	}

	/**
	 * Extracts the column headers from a file and attempts to match them to {@link Model} fields.  If 
	 *   a header cannot be matched to a field, this column will be ignored.
	 *
	 * @param line header line from file
	 */
	protected void parseHeader(String line)  {
		determineMappableModelFields();
		String[] bits = line.split(delimiter);
		for (int i = 0; i < bits.length; i++){
			String name = getMatchedHeaderFieldName(bits[i]);
			if (name != null){
				columnIndexMap.put(i, name);
			}
		}
	}

	/**
	 * Attempts to match a file column header to an available {@link Model} attribute name or alias.
	 *   If no match can be made, or the header is empty, a null value is returned.
	 * 
	 * @param headerName parsed header name
	 * @return field name that header matches, or null.
	 */
	private String getMatchedHeaderFieldName(String headerName)  {
		if (headerName == null || headerName.equals("")){
			logger.warn("Column header has no name.");
			return null;
		}
		for (Map.Entry<String,String> entry: fieldNameMap.entrySet()){
			if (headerName.toLowerCase().replaceAll("[\\W\\s_-]", "").equals(entry.getKey().toLowerCase())){
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * Parses a single line of text and maps the values to the appropriate {@link Model} field values.
	 *   Empty column values will be mapped as null values.  Throws an exception if the string value
	 *   cannot be converted to the appropriate type.
	 * 
	 * @param line line from the file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T getRecordFromLine(String line) throws DataImportException {
		
	  BeanWrapperImpl wrapper = new BeanWrapperImpl(model);
		String[] bits = line.split(delimiter);
		
		for (int i = 0; i < bits.length; i++){
			
		  if (!columnIndexMap.containsKey(i)) continue;
			
		  String fieldName = columnIndexMap.get(i);
			Class<?> type = fieldTypeMap.get(fieldName);
			
			if (bits[i].trim().equals("")){
				wrapper.setPropertyValue(fieldName, null);
			} else {
				wrapper.setPropertyValue(fieldName, convertFieldValue(bits[i].trim(), type));
			}
			
		}
		
		if (this.getDataSet() != null && wrapper.isWritableProperty("dataSet")){
		  wrapper.setPropertyValue("dataSet", this.getDataSet());
    }

    if (this.getDataFile() != null && wrapper.isWritableProperty("dataFile")){
      wrapper.setPropertyValue("dataFile", this.getDataFile());
    }
		
		return (T) wrapper.getWrappedInstance();
	}

	/**
	 * Converts a string value from a parsed file to the appropriate type required for the {@link Model}
	 *   field.  Throws an exception if conversion cannot be applied.
	 * 
	 * @param s string column value
	 * @param type type to convert to
	 * @return converted object
	 */
	private Object convertFieldValue(String s, Class<?> type) throws DataImportException {
		if (type.equals(String.class)) return s;
		if (conversionService.canConvert(String.class, type)){
			return conversionService.convert(s, type);
		} else {
			throw new DataImportException(String.format("Cannot convert String type to %s.", type.getName()));
		}
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(
			ConversionService conversionService) {
		this.conversionService = conversionService;
	}

  public void setColumnMappings(Map<String, String> columnMappings) {
    this.columnMappings = columnMappings;
  }

  @Override
  public Class<T> getModel() {
    return model;
  }

  @Override
  public void setModel(Class<T> model) {}
}
