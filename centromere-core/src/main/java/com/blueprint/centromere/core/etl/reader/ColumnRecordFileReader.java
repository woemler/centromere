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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads plain text files and maps a single column to a single record, to be emitted when the file's
 *   line reading is exhausted.  Assumes that the the first column represents field identifiers.
 *
 * @author woemler
 */
public abstract class ColumnRecordFileReader<T extends Model<?>> 
    extends DelimitedTextFileRecordReader<T> {

  private List<T> records = new ArrayList<>();

  public ColumnRecordFileReader(Class<T> model, String delimiter) {
    super(model, delimiter);
  }

  public ColumnRecordFileReader(Class<T> model) {
    super(model);
  }

  @Override
  public void doBefore(File file, Map<String, String> args) throws DataProcessingException {

    super.doBefore(file, args);
    
    Map<Integer, T> recordMap = new LinkedHashMap<>();
    records = new ArrayList<>();

    try {
      List<String> bits = this.getNextLine();
      while (bits != null){
        if (bits.size() > 1){
          String field = bits.get(0);
          for (int i = 1; i < bits.size(); i++){
            T record = recordMap.containsKey(i) ? recordMap.get(i) : this.getModel().newInstance();
            setModelAttribute(record, field, bits.get(i));
            recordMap.put(i, record);
          }
        }
        bits = this.getNextLine();
      }
    } catch (Exception e){
      throw new DataProcessingException(e);
    }

    records = new ArrayList<>(recordMap.values());

  }

  protected abstract void setModelAttribute(T record, String attribute, String value);

  @Override
  public T readRecord() throws DataProcessingException {
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

}
