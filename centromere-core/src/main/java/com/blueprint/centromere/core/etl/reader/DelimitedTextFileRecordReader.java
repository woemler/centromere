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
import com.blueprint.centromere.core.model.ModelSupport;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple abstract implementation of {@link RecordReader}, for reading delimited text files. Handles
 * the file object opening and closing in the {@code doBefore} and {@code doAfter} methods, as well
 * as line parsing for the appropriate delimiter type file readers.
 *
 * @author woemler
 */
public abstract class DelimitedTextFileRecordReader<T extends Model<?>>
    implements RecordReader<T>, ModelSupport<T> {

    private static final Logger LOGGER
        = LoggerFactory.getLogger(DelimitedTextFileRecordReader.class);

    private final Class<T> model;
    private final String delimiter;

    private Closeable reader;

    public DelimitedTextFileRecordReader(Class<T> model, String delimiter) {
        this.model = model;
        this.delimiter = delimiter;
    }

    public DelimitedTextFileRecordReader(Class<T> model) {
        this(model, "\t");
    }

    /**
     * Closes any open reader and opens the new target file.  Assigns local variables, if
     * available.
     */
    @Override
    public void doBefore(File file, Map<String, String> args) throws DataProcessingException {
        this.close();
        this.open(file);
    }

    /**
     * Calls the close method on the reader.
     */
    @Override
    public void doOnSuccess(File file, Map<String, String> args) throws DataProcessingException {
        this.close();
    }

    @Override
    public void doOnFailure(File file, Map<String, String> args) throws DataProcessingException {
        this.close();
    }

    /**
     * Opens the target file and creates a the appropriate file reader object, which can be
     * referenced via its getter method.
     *
     * @param file input file
     */
    protected void open(File file) throws DataProcessingException {
        try {
            if (",".equals(delimiter)) {
                reader = new CSVReader(new BufferedReader(new FileReader(file)));
            } else {
                reader = new BufferedReader(new FileReader(file));
            }
        } catch (IOException e) {
            throw new DataProcessingException(e);
        }
    }

    /**
     * Closes the target file, if a reader exists.
     */
    protected void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                LOGGER.debug(e.getMessage());
            }
        }
    }

    /**
     * Returns the next line from the file.
     */
    protected List<String> getNextLine() throws IOException {

        List<String> bits = new ArrayList<>();

        if (",".equals(delimiter)) {
            String[] line = ((CSVReader) reader).readNext();
            if (line == null) {
                return null;
            }
            for (String bit : line) {
                bits.add(bit.trim());
            }
        } else {
            String line = ((BufferedReader) reader).readLine();
            if (line == null) {
                return null;
            }
            for (String bit : line.split(delimiter)) {
                bits.add(bit.trim());
            }
        }

        return bits;

    }

    /**
     * Tests whether a given line should be skipped.
     *
     * @param line file line bits
     */
    protected abstract boolean isSkippableLine(List<String> line);

    /**
     * Gets the file delimiter.
     */
    protected String getDelimiter() {
        return delimiter;
    }

    /**
     * See {@link ModelSupport#getModel()}.
     */
    @Override
    public Class<T> getModel() {
        return model;
    }

}
