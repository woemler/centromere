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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * Writes records to a character-delimited text file.  Can be used for file reformatting or temp file
 *   preperation for utilities, such as MySQL Import.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class DelimtedTextFileWriter<T extends Model<?>> extends AbstractRecordFileWriter<T> {

  private String delimiter = "\t";
  private String enclosedBy = "";
  private String escapedBy = "\\\\";
  private String terminatedBy = "\n";
  private List<String> ignoredFields = new ArrayList<>();
  private List<String> columns = new ArrayList<>();

  @Override
  public void doBefore(Object... args) throws DataImportException {
    super.doBefore(args);
    columns = new ArrayList<>();
  }

  @Override
  public void writeRecord(T record) throws DataImportException {
    FileWriter writer = this.getWriter();
    StringBuilder stringBuilder = new StringBuilder();
    try {
      boolean flag = false;
      Class<?> currentClass = record.getClass();
      while (currentClass.getSuperclass() != null){
        for (Field field : currentClass.getDeclaredFields()) {
          if (
              field.isSynthetic() 
              || ignoredFields.contains(field.getName())
              || field.isAnnotationPresent(Transient.class) 
              || field.isAnnotationPresent(ManyToOne.class)
              || field.isAnnotationPresent(OneToOne.class)
              || field.isAnnotationPresent(OneToMany.class)
          ){
            continue; // skip these fields
          }
          String fieldName;
          if (field.isAnnotationPresent(Column.class)){
            Column column = field.getAnnotation(Column.class);
            fieldName = column.name();
          } else {
            fieldName = field.getName();
          }
          if (!columns.contains(fieldName)) columns.add(fieldName);
          field.setAccessible(true);
          Object value = field.get(record) != null ? field.get(record) : null;
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
        currentClass = currentClass.getSuperclass();
      }
    } catch (IllegalAccessException e){
      e.printStackTrace();
      throw new DataImportException(e.getMessage());
    }
    try {
      writer.write(stringBuilder.toString());
      writer.write(terminatedBy);
    } catch (IOException e){
      e.printStackTrace();
      throw new DataImportException(e.getMessage());
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
}
