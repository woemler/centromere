/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.core.dataimport.reader.impl;

import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.StandardRecordFileReader;
import com.blueprint.centromere.core.model.impl.Sample;
import com.blueprint.centromere.core.model.impl.SampleAware;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
public class GenericSampleReader extends StandardRecordFileReader<Sample> 
    implements SampleAware {
  
  private final DataImportProperties dataImportProperties;
  
  private List<Sample> samples = new ArrayList<>();
  private String defaultEmptyValue = "n/a";

  public GenericSampleReader(DataImportProperties dataImportProperties) {
    this.dataImportProperties = dataImportProperties;
  }

  @Override
  protected boolean isHeaderLine(String line) {
    return line.toLowerCase().startsWith("sample");
  }

  @Override
  public void doBefore() throws DataImportException {
    super.doBefore();
    samples = new ArrayList<>();
  }

  /**
   * Parses a line of text and returns a single model record.  Should return null if the line does
   * not contain a valid record.
   */
  @Override
  protected Sample getRecordFromLine(String line) throws DataImportException {
    
    String[] bits = line.split(this.getDelimiter());
    Sample sample = new Sample();
    
    sample.setName(bits[0].trim());
    sample.setSampleId(sample.getName());

    for (Map.Entry<String, Integer> entry : this.getHeaderMap().entrySet()) {
      String value = getColumnValue(entry.getKey(), bits);
      if (entry.getKey().equalsIgnoreCase("tissue")) {
        sample.setTissue(value);
      } else if (entry.getKey().equalsIgnoreCase("histology")) {
        sample.setHistology(value);
      } else if (entry.getKey().equalsIgnoreCase("type")
          || entry.getKey().replaceAll("[_.-]+", "").equalsIgnoreCase("sampletype")) {
        sample.setSampleType(value);
      } else if (entry.getKey().equalsIgnoreCase("notes")) {
        sample.setNotes(value);
      } else if (entry.getKey().equalsIgnoreCase("gender")) {
        sample.setGender(value);
      } else if (entry.getKey().equalsIgnoreCase("species")) {
        sample.setSpecies(value);
      } else if (entry.getKey().equalsIgnoreCase("alias") ||
          entry.getKey().equalsIgnoreCase("aliases")) {
        List<String> values = Arrays.asList(value.split("\\s*,\\s*"));
        sample.addAliases(values);
      } else {
        sample.addAttribute(entry.getKey().replace(".", "_"), value);
      }
    }
    if (sample != null && !samples.contains(sample)) samples.add(sample);
    return sample;
  }
  
  private String getColumnValue(String column, String[] bits){
    String val = null;
    if (this.hasColumn(column) && this.getColumnIndex(column) < bits.length) {
      val = bits[this.getColumnIndex(column)].trim();
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
  
  @Override
  public List<Sample> getSamples() {
    return samples;
  }

  public Class<Sample> getModel() {
    return Sample.class;
  }

  public DataImportProperties getDataImportProperties() {
    return dataImportProperties;
  }
  
}
