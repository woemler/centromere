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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simple text file reader that parses multiple records from a single line.  The identification of 
 *   the records in each line is assumed to come from the header row, which is parsed and stored 
 *   when encountered in the {@code parseHeader} method.  The {@code readRecord} method can extract 
 *   multiple records from a single line and return them one-at-a-time, consistent with standard 
 *   record reader.
 *
 * @author woemler
 * @since 0.4.3
 */
public abstract class MultiRecordFileReader<T extends Model<?>>
    extends DelimitedTextFileRecordReader<T> {

    private List<T> recordList = new ArrayList<>();
    private List<String> headers = new ArrayList<>();
    private boolean headerFlag = true;

    public MultiRecordFileReader(Class<T> model, String delimiter) {
        super(model, delimiter);
    }

    public MultiRecordFileReader(Class<T> model) {
        super(model);
    }

    /**
     * Initializes the header and record list objects.
     */
    @Override
    public void doBefore(File file, Map<String, String> args) throws DataProcessingException {
        super.doBefore(file, args);
        recordList = new ArrayList<>();
        headers = new ArrayList<>();
        headerFlag = true;
    }

    /**
     * See {@link RecordReader#readRecord()}.
     */
    @Override
    public T readRecord() throws DataProcessingException {
        if (recordList.size() > 0) {
            return recordList.remove(0);
        } else {
            try {
                List<String> line = this.getNextLine();
                while (line != null) {
                    if (!isSkippableLine(line)) {
                        if (isHeaderLine(line)) {
                            parseHeader(line);
                            headerFlag = false;
                        } else {
                            recordList = getRecordsFromLine(line);
                            if (recordList.size() > 0) {
                                return recordList.remove(0);
                            }
                        }
                    }
                    line = this.getNextLine();
                }
            } catch (Exception e) {
                throw new DataProcessingException(e);
            }
        }
        return null;
    }

    /**
     * Extracts the column names from the header line in the file. 
     *
     * @param line input line bits.
     */
    protected void parseHeader(List<String> line) throws DataProcessingException {
        headers = line;
    }

    /**
     * Tests whether the supplied line is a header.
     *
     * @param line input line bits
     * @return true if line is the file header
     */
    protected boolean isHeaderLine(List<String> line) {
        return headerFlag;
    }

    /**
     * Extracts multiple records from a single line of the text file.  If no valid records are found, 
     *   an empty list should be returned. 
     *
     * @param line input line bits
     * @return a list of model objects
     */
    protected abstract List<T> getRecordsFromLine(List<String> line) throws DataProcessingException;

    protected List<String> getHeaders() {
        return headers;
    }

}
