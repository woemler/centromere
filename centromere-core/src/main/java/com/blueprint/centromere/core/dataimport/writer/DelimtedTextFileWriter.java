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

package com.blueprint.centromere.core.dataimport.writer;

import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelReflectionUtils;
import com.blueprint.centromere.core.model.ModelSupport;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Writes records to a character-delimited text file.  Can be used for file reformatting or temp file
 *   preperation for utilities, such as MySQL Import.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class DelimtedTextFileWriter<T extends Model<?>> extends AbstractRecordFileWriter<T> 
    implements ModelSupport<T> {

  private String delimiter = "\t";
  private String enclosedBy = "";
  private String escapedBy = "\\\\";
  private String terminatedBy = "\n";
  private List<String> ignoredFields = new ArrayList<>();
  private List<String> columns = new ArrayList<>();
  private List<String> fields = new ArrayList<>();
  private boolean headerFlag = true;

  public DelimtedTextFileWriter() {
    super();
  }

  public DelimtedTextFileWriter(Class<T> model) {
    super(model);
  }

  @Override
  public void doBefore(Object... args)  {
    super.doBefore(args);
    columns = ModelReflectionUtils.getPersistableNonEntityFieldNames(this.getModel(), ignoredFields);
    fields = columns;
  }

  @Override
  public void writeRecord(T record)  {
    
    FileWriter writer = this.getWriter();
    StringBuilder stringBuilder = new StringBuilder();
    
    if (headerFlag){
      boolean flag = false;
      for (String headerName: columns){
        if (flag) stringBuilder.append(delimiter);
        stringBuilder.append(enclosedBy)
            .append(headerName)
            .append(enclosedBy);
        flag = true;
      }

      try {
        writer.write(stringBuilder.toString());
        writer.write(terminatedBy);
      } catch (IOException e){
        throw new DataImportException(e);
      }
      headerFlag = false;
    }
    
    stringBuilder = new StringBuilder();
    BeanWrapper wrapper = new BeanWrapperImpl(record);
    boolean flag = false;
    for (String column: fields){
      Object value = wrapper.getPropertyValue(column);
      if (value == null){
        value = "null";
      } else if (!"".equals(enclosedBy) && value instanceof String) {
        value = ((String) value).replaceAll(enclosedBy, escapedBy + enclosedBy);
      }
      if (flag) stringBuilder.append(delimiter);
      stringBuilder.append(enclosedBy)
          .append(value)
          .append(enclosedBy);
      flag = true;
    }
    
    try {
      writer.write(stringBuilder.toString());
      writer.write(terminatedBy);
    } catch (IOException e){
      throw new DataImportException(e);
    }
    
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public String getEnclosedBy() {
    return enclosedBy;
  }

  public void setEnclosedBy(String enclosedBy) {
    this.enclosedBy = enclosedBy;
  }

  public String getEscapedBy() {
    return escapedBy;
  }

  public void setEscapedBy(String escapedBy) {
    this.escapedBy = escapedBy;
  }

  public String getTerminatedBy() {
    return terminatedBy;
  }

  public void setTerminatedBy(String terminatedBy) {
    this.terminatedBy = terminatedBy;
  }

  public List<String> getIgnoredFields() {
    return ignoredFields;
  }

  public void setIgnoredFields(List<String> ignoredFields) {
    this.ignoredFields = ignoredFields;
  }

  protected List<String> getColumns() {
    return columns;
  }

  protected void setColumns(List<String> columns) {
    this.columns = columns;
  }

  protected List<String> getFields() {
    return fields;
  }

  protected void setFields(List<String> fields) {
    this.fields = fields;
  }

}
