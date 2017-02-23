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
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Writes records to a character-delimited text file.  Can be used for file reformatting or temp file
 *   preperation for utilities, such as MySQL Import.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class MySQLImportTempFileWriter<T extends Model<?>> extends DelimtedTextFileWriter<T> {

  public MySQLImportTempFileWriter() {
  }

  public MySQLImportTempFileWriter(Class<T> model) {
    super(model);
  }

  @Override
  public void doBefore(Object... args) throws DataImportException {
    super.doBefore(args);
    this.setColumns(getWritableColumnNames(this.getModel(), this.getIgnoredFields()));
  }

  /**
   * Returns a list of database column names, used for writing headers to the temp files. Allows 
   *   specified fields to be skipped.
   * 
   * @param model
   * @param ignoredFields
   * @return
   */
  protected List<String> getWritableColumnNames(Class<?> model, List<String> ignoredFields){
    List<String> columns = new ArrayList<>();
    Class<?> currentClass = model;
    while (currentClass.getSuperclass() != null){
      for (Field field : currentClass.getDeclaredFields()) {
        if (isSkippableField(field, ignoredFields)) continue; // skip these fields
        String name = field.getName();
        if (field.isAnnotationPresent(Column.class)){
          Column column = field.getAnnotation(Column.class);
          if (!column.name().equals("")){
            name = column.name();
          }
        }
        columns.add(name);
      }
      currentClass = currentClass.getSuperclass();
    }
    return columns;
  }

  /**
   * Returns a list of database column names, used for writing headers to the temp files.
   *
   * @param model
   * @return
   */
  protected List<String> getWritableColumnNames(Class<?> model){
    return getWritableColumnNames(model, new ArrayList<>());
  }

  @Override
  public String getTempFilePath(String inputFilePath) {
    File tempDir;
    if (!this.getEnvironment().containsProperty("centromere.import.temp-dir")
        || this.getEnvironment().getRequiredProperty("centromere.import.temp-dir") == null
        || "".equals(this.getEnvironment().getRequiredProperty("centromere.import.temp-dir"))){
      tempDir = new File(System.getProperty("java.io.tmpdir"));
    } else {
      tempDir = new File(this.getEnvironment().getRequiredProperty("centromere.import.temp-dir"));
    }
    String fileName = this.getModel().getSimpleName();
    if (this.getModel().isAnnotationPresent(Table.class)){
      Table table = this.getModel().getAnnotation(Table.class);
      if (!table.name().equals("")) fileName = table.name();
    }
    File tempFile = new File(tempDir, fileName + ".txt");
    return tempFile.getPath();
  }
}
