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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Standard text file reader that assumes that a single record is extractable from a single line.  
 *   Some lines may be skipped, if they pass the test implemented in {@code isSkippableLine}.  The
 *   code for extracting a single record from a line should be implemented in 
 *   {@code getRecordFromLine}, and should return null if an invalid record is present.
 *
 * @author woemler
 * @since 0.4.3
 */
public abstract class StandardFileRecordReader<T extends Model<?>>
    extends DelimitedTextFileRecordReader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardFileRecordReader.class);

    private Map<String, Integer> headerMap = new LinkedHashMap<>();
    private ConversionService conversionService = new DefaultConversionService();

    public StandardFileRecordReader(Class<T> model, String delimiter) {
        super(model, delimiter);
    }

    public StandardFileRecordReader(Class<T> model) {
        super(model);
    }

    /**
     * See {@link RecordReader#readRecord()}.
     */
    @Override
    public T readRecord() throws DataProcessingException {
        List<String> line = null;
        Integer count = 0;
        try {
            line = getNextLine();
            while (line != null) {
                count++;
                if (!isSkippableLine(line)) {
                    if (isHeaderLine(line)) {
                        parseHeader(line);
                    } else {
                        T record = getRecordFromLine(line);
                        if (record != null) {
                            return record;
                        }
                    }
                }
                line = getNextLine();
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Failed on line %d: %s", count, line));
            throw new DataProcessingException(e);
        }
        return null;
    }

    /**
     * Extracts the column names from the header line of the file.
     *
     * @param line line bits
     */
    protected void parseHeader(List<String> line) {
        headerMap = new LinkedHashMap<>();
        for (int i = 0; i < line.size(); i++) {
            headerMap.put(line.get(i), i);
        }
    }

    /**
     * Gets the value of the current lines specified column, converting the string to the target 
     *   type.
     *
     * @param line line bits
     * @param header column header
     * @param clazz class to convert value to
     * @param <S> target type
     * @return converted column value
     * @throws DataProcessingException thrown if column does not exist or cannot be converted
     */
    protected <S> S getColumnValue(List<String> line, String header, Class<S> clazz)
        throws DataProcessingException {
        return getColumnValue(line, header, clazz, false);
    }

    /**
     * Gets the value of the current lines specified column, converting the string to the target 
     *   type. Specifies case-sensitive header matching.
     *
     * @param line line bits
     * @param header column header
     * @param clazz class to convert value to
     * @param caseSensitive specifies case-sensitive column matching.
     * @param <S> target type
     * @return converted column value
     * @throws DataProcessingException thrown if column does not exist or cannot be converted
     */
    protected <S> S getColumnValue(List<String> line, String header, Class<S> clazz,
        boolean caseSensitive) throws DataProcessingException {
        Integer index = getColumnIndex(header, caseSensitive);
        if (index == -1) {
            throw new DataProcessingException(String.format("Given header does not exist "
                + "in this file: %s", header));
        }
        if (line.size() <= index) {
            throw new DataProcessingException(String.format("Header index is outside bounds of current "
                + "line. Index = %d, line length = %d", index, line.size()));
        }
        if (!conversionService.canConvert(String.class, clazz)) {
            throw new DataProcessingException(String.format("Cannot convert string value to %s",
                clazz.getName()));
        }
        return conversionService.convert(line.get(index).trim(), clazz);
    }

    /**
     * Gets the value of the current lines specified column, converting the string to the target 
     *   type. Specifies case-sensitive header matching.
     *
     *  @param line line bits
     *  @param header column header
     * @return string column value
     * @throws DataProcessingException thrown if column header not found
     */
    protected String getColumnValue(List<String> line, String header)
        throws DataProcessingException {
        return getColumnValue(line, header, String.class);
    }

    /**
     * Given a column header name, will return the index position of the column, or null if the 
     *   column is not present.
     *
     * @param header header name
     * @return column index of header
     */
    protected Integer getColumnIndex(String header) {
        for (Map.Entry<String, Integer> entry: headerMap.entrySet()) {
            if (header.toLowerCase().equals(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return -1;
    }

    /**
     * Given a column header name, will return the index position of the column, or null if the 
     *   column is not present.
     *
     * @param header header name
     * @return column index of header
     */
    protected Integer getColumnIndex(String header, boolean caseSensitive) {
        if (caseSensitive) {
            return headerMap.getOrDefault(header, -1);
        }
        return getColumnIndex(header);
    }

    /**
     * Given an index, corresponding to a column position, returns the header name for the column, 
     *   or null if not present.
     *
     * @param index column index
     * @return header name
     */
    protected String getColumnHeader(Integer index) {
        for (Map.Entry<String, Integer> entry: headerMap.entrySet()) {
            if (index.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Tests to see whether a column exists in the file.
     *
     * @param header header name
     * @param caseSensitive specifies case-sensitive matching
     * @return true if header in file
     */
    protected boolean hasColumn(String header, boolean caseSensitive) {
        return getColumnIndex(header, caseSensitive) > -1;
    }

    /**
     * Tests to see whether a column exists in the file.  Ignores header case.
     *
     * @param header header name
     * @return true if header in file
     */
    protected boolean hasColumn(String header) {
        return hasColumn(header, false);
    }

    /**
     * Parses a line of text and returns a single model record.  Should return null if the line does 
     *   not contain a valid record.
     *
     * @param line line bits
     * @return model object
     */
    protected abstract T getRecordFromLine(List<String> line) throws DataProcessingException;

    /**
     * Performs a test to see if the line should be skipped.
     *
     * @param line line bits
     * @return true if line can be skipped
     */
    protected abstract boolean isSkippableLine(List<String> line);

    /**
     * Tests whether the supplied line is a header.
     *
     * @param line line bits
     * @return true if line is a file header
     */
    protected abstract boolean isHeaderLine(List<String> line);

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public Map<String, Integer> getHeaderMap() {
        return headerMap;
    }
    
}
