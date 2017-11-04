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

import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.AbstractRecordFileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;

/**
 * @author woemler
 */
public class GenericSubjectReader extends AbstractRecordFileReader<Subject> {
  
  private final DataImportProperties dataImportProperties;
  
  private Map<String, Integer> headerMap = new HashMap<>();
  private String defaultEmptyValue = "n/a";
  private String delimiter = "\t";
  private String replacementCharacter = "_"; 

  public GenericSubjectReader(DataImportProperties dataImportProperties) {
    this.dataImportProperties = dataImportProperties;
  }

  @Override
  public Subject readRecord() throws DataImportException {
    try {
      String line  = this.getReader().readLine();
      while (line != null){
        if (!line.toLowerCase().startsWith("sample")){
          Subject subject = getRecordFromLine(line);
          if (subject != null) return subject;
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
  protected Subject getRecordFromLine(String line) throws DataImportException {
    String[] bits = line.split(delimiter);
    Subject subject = new Subject();
    BeanUtils.copyProperties(dataImportProperties.getSubject(), subject);
    subject.setName(bits[0].trim());
    for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
      if ("tissue".equals(entry.getKey().toLowerCase())) {
        subject.addAttribute("sampleTissue", getColumnValue(entry.getKey(), bits));
      } else if ("histology".equals(entry.getKey().toLowerCase())) {
        subject.addAttribute("sampleHistology", getColumnValue(entry.getKey(), bits));
      } else if ("sample_type".equals(entry.getKey().toLowerCase()) 
          || "sampletype".equals(entry.getKey().toLowerCase())) {
        subject.addAttribute("sampleType", getColumnValue(entry.getKey(), bits));
      } else if ("gender".equals(entry.getKey().toLowerCase())) {
        subject.setGender(getColumnValue(entry.getKey(), bits));
      } else if ("species".equals(entry.getKey().toLowerCase())) {
        subject.setSpecies(getColumnValue(entry.getKey(), bits));
      } else if ("sample_name".equals(entry.getKey().toLowerCase()) || 
          "samplename".equals(entry.getKey().toLowerCase())) {
        subject.addAlias(getColumnValue(entry.getKey(), bits));
      } else if ("notes".equalsIgnoreCase(entry.getKey())){
        subject.setNotes(getColumnValue(entry.getKey(), bits));
      } else {
        subject.addAttribute(entry.getKey().replaceAll("\\.", replacementCharacter), 
            getColumnValue(entry.getKey(), bits)); // Maps cannot have periods in their keys
      }
    }
    return subject;
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

  public String getReplacementCharacter() {
    return replacementCharacter;
  }

  public void setReplacementCharacter(String replacementCharacter) {
    this.replacementCharacter = replacementCharacter;
  }
}
