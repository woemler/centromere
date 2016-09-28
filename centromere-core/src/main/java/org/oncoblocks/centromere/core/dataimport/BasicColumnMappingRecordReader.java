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
import org.oncoblocks.centromere.core.model.ModelSupport;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;

/**
 * Generic {@link RecordReader} implementation that will use data file column headers to try to map
 *   column values to {@link Model} fields.  Uses Spring's {@link org.springframework.beans.BeanWrapper}
 *   and {@link ConversionService} to handle the text string-to-object conversion.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class BasicColumnMappingRecordReader<T extends Model<?>> extends AbstractRecordFileReader<T>
		implements ImportOptionsAware, ModelSupport<T> {
	
	private BasicImportOptions options = new BasicImportOptions();
	private Class<T> model;
	private Map<String,Class<?>> columnMap = new LinkedHashMap<>();
	private boolean headerFlag = true;
	private String delimiter = "\\t";
	private ConversionService conversionService = new DefaultConversionService();
	
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
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Extracts the column headers from a file and attempts to match them to {@link Model} fields.  If 
	 *   a header cannot be matched to a field, this column will be ignored.
	 * 
	 * @param line header line from file
	 * @throws DataImportException
	 */
	private void parseHeader(String line) throws DataImportException {
		BeanWrapperImpl wrapper = new BeanWrapperImpl(model);
		for (String head: line.trim().split(delimiter)){
			Class<?> type = null;
			String name = head;
			for (PropertyDescriptor descriptor: wrapper.getPropertyDescriptors()){
				if (headerMatchesField(head, descriptor.getName())){
					type = descriptor.getPropertyType();
					name = descriptor.getName();
				}
			}
			columnMap.put(name, type);
		}
	}

	/**
	 * Tests to see if the header name corresponds to a {@link Model} field name.  Strips out non-standard
	 *   field name characters and ignores case when testing.
	 * 
	 * @param headerName name of header column in file
	 * @param fieldName name of feild in model
	 * @return true if header matches field
	 * @throws DataImportException
	 */
	private boolean headerMatchesField(String headerName, String fieldName) throws DataImportException {
		if (headerName == null || headerName.equals("") || fieldName == null || fieldName.equals("")){
			throw new DataImportException("Header name and field name values cannot be null or empty.");
		}
 		return headerName.toLowerCase().replaceAll("[\\W\\s_-]", "").equals(fieldName.toLowerCase());	
	}

	/**
	 * Parses a single line of text and maps the values to the appropriate {@link Model} field values.
	 *   Empty column values will be mapped as null values.  Throws an exception if the string value
	 *   cannot be converted to the appropriate type.
	 * 
	 * @param line line from the file
	 * @return
	 * @throws DataImportException
	 */
	@SuppressWarnings("unchecked")
	private T getRecordFromLine(String line) throws DataImportException{
		List<Map.Entry<String, Class<?>>> headerList = new ArrayList<>(columnMap.entrySet());
		BeanWrapperImpl wrapper = new BeanWrapperImpl(model);
		String[] bits = line.split(delimiter);
		for (int i = 0; i < bits.length; i++){
			Map.Entry<String, Class<?>> entry = headerList.get(i);
			if (entry.getValue() != null){
				if (bits[i].trim().equals("")){
					wrapper.setPropertyValue(entry.getKey(), null);
				} else {
					wrapper.setPropertyValue(entry.getKey(),
							convertFieldValue(bits[i].trim(), entry.getValue()));
				}
			}
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
	 * @throws DataImportException
	 */
	private Object convertFieldValue(String s, Class<?> type) throws DataImportException {
		if (type.equals(String.class)) return s;
		if (conversionService.canConvert(String.class, type)){
			return conversionService.convert(s, type);
		} else {
			throw new DataImportException(String.format("Cannot convert String type to %s.", type.getName()));
		}
	}

	@Override 
	public void doBefore(Object... args) throws DataImportException {
		super.doBefore(args);
		headerFlag = true;
	}

	public BasicImportOptions getImportOptions() {
		return options;
	}

	public void setImportOptions(ImportOptions options) {
		this.options = new BasicImportOptions(options);
	}

	public void setImportOptions(BasicImportOptions options) {
		this.options = options;
	}

	@Override 
	public Class<T> getModel() {
		return model;
	}

	public void setModel(Class<T> model) {
		this.model = model;
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
}
