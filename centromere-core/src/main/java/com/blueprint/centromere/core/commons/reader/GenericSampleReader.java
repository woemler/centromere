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

package com.blueprint.centromere.core.commons.reader;

import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.support.SampleAware;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.AbstractRecordFileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
public class GenericSampleReader<T extends Sample<?>> extends AbstractRecordFileReader<T> 
    implements SampleAware {
  
  private final Class<T> model;
  private final DataImportProperties dataImportProperties;
  
  private Map<String, Integer> headerMap = new HashMap<>();
  private String defaultEmptyValue = "n/a";
  private String delimiter = "\t";

  public GenericSampleReader(Class<T> model, DataImportProperties dataImportProperties) {
    this.model = model;
    this.dataImportProperties = dataImportProperties;
  }

  @Override
  public T readRecord() throws DataImportException {
    try {
      String line  = this.getReader().readLine();
      while (line != null){
        if (!line.toLowerCase().startsWith("sample")){
          T sample = getRecordFromLine(line);
          if (sample != null) return sample;
        } else {
          headerMap = new HashMap<>();
          String[] bits = line.split("\\t");
          for (int i = 0; i < bits.length; i++){
            if (!"sample".equals(bits[i].trim().toLowerCase())){
              headerMap.put(bits[i], i);
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
   * Parses a line of text and returns a single model record.  Should return null if the line does
   * not contain a valid record.
   */
  protected T getRecordFromLine(String line) throws DataImportException {
    
    String[] bits = line.split(delimiter);
    T sample;
    
    try {
      sample = model.newInstance();
    } catch (Exception e){
      throw new DataImportException(e);
    }
    //BeanUtils.copyProperties(dataImportProperties.getSample(), sample);
    sample.setName(bits[0].trim());
    sample.setSampleId(sample.getName());

    for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("tissue")) {
        sample.setTissue(getColumnValue(entry.getKey(), bits));
      } else if (entry.getKey().equalsIgnoreCase("histology")) {
        sample.setHistology(getColumnValue(entry.getKey(), bits));
      } else if (entry.getKey().equalsIgnoreCase("type")
          || entry.getKey().replaceAll("[_.-]+", "").equalsIgnoreCase("sampletype")) {
        sample.setSampleType(getColumnValue(entry.getKey(), bits));
      } else if (entry.getKey().equalsIgnoreCase("notes")) {
        sample.setNotes(getColumnValue(entry.getKey(), bits));
      } else if (entry.getKey().equalsIgnoreCase("gender")) {
        sample.setGender(getColumnValue(entry.getKey(), bits));
      } else if (entry.getKey().equalsIgnoreCase("species")) {
        sample.setSpecies(getColumnValue(entry.getKey(), bits));
      } else {
        sample.addAttribute(entry.getKey(), getColumnValue(entry.getKey(), bits));
      }
    }
    return sample;
  }
  
  private String getColumnValue(String column, String[] bits){
    String val = null;
    if (headerMap.containsKey(column) && headerMap.get(column) < bits.length) {
      val = bits[headerMap.get(column)].trim();
      if ("".equals(val) || "--".equals(val)) {
        val = defaultEmptyValue;
      }
    } else {
      val = defaultEmptyValue;
    }
    return val;
  }

  /**
   * Performs a test to see if the line should be skipped.
   */
  protected boolean isSkippableLine(String line) {
    return false;
  }
  
  public void setDefaultEmptyValue(String defaultEmptyValue) {
    this.defaultEmptyValue = defaultEmptyValue;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public List<Sample> getSamples() {
    return null;
  }
}
