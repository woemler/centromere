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

package com.blueprint.centromere.core.dataimport;

import com.blueprint.centromere.core.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads plain text files and maps a single column to a single record, to be emitted when the file's
 *   line reading is exhausted.  Assumes that the the first column represents filed identifiers.
 *
 * @author woemler
 */
public abstract class ColumnRecordFileReader<T extends Model<?>> extends AbstractRecordFileReader<T> {

    private List<T> records = new ArrayList<>();
    private String delimiter = "\\t";

    @Override
    public void doBefore(Object... args) throws DataImportException {

        super.doBefore(args);
        Map<Integer, T> recordMap = new LinkedHashMap<>();
        records = new ArrayList<>();

        try {
            String line = this.getReader().readLine();
            while (line != null){
                String[] bits = line.trim().split(delimiter);
                if (bits.length > 1){
                    String field = bits[0];
                    for (int i = 1; i < bits.length; i++){
                        T record = recordMap.containsKey(i) ? recordMap.get(i) : getModel().newInstance();
                        setModelAttribute(record, field, bits[i]);
                        recordMap.put(i, record);
                    }
                }
                line = this.getReader().readLine();
            }
        } catch (Exception e){
            throw new DataImportException(e.getMessage());
        }

        records = new ArrayList<>(recordMap.values());

    }

    protected abstract void setModelAttribute(T record, String attribute, String value);

    @Override
    public T readRecord() throws DataImportException {
        if (records.size() == 0){
            return null;
        } else {
            return records.remove(0);
        }
    }

    protected List<T> getRecords() {
        return records;
    }

    protected void setRecords(List<T> records) {
        this.records = records;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
