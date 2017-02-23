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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * Runs {@code mysqlimport} and imports the supplied file through using the command line tool.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class MySqlImportFileImporter implements RecordImporter {
  
  private static final Logger logger = LoggerFactory.getLogger(MySqlImportFileImporter.class);
  
  private boolean stopOnError = true;
  private boolean dropTable = false;
  private String delimiter = "\t";
  private Integer skipLines = 1;
  private List<String> columns;
  private Environment environment;
  private final String host;
  private final String username;
  private final String password;
  private final String database;

  public MySqlImportFileImporter(String host, String username, String password,
      String database) {
    this.host = host;
    this.username = username;
    this.password = password;
    this.database = database;
  }

  public MySqlImportFileImporter(String host, String username,
      String password, String database, List<String> columns) {
    this.columns = columns;
    this.host = host;
    this.username = username;
    this.password = password;
    this.database = database;
  }

  @Override
  public void importFile(String filePath) throws DataImportException {

    Process process;
    
    String columnString = getColumnString(filePath);
    StringBuilder sb = new StringBuilder("mysqlimport --local ");
    if (!stopOnError) sb.append(" --force ");
    if (dropTable) sb.append(" --delete ");
    if (columns != null) sb.append(String.format(" -c %s ", columnString));
    if (skipLines != null && skipLines > 0) sb.append(String.format(" --ignore-lines=%d ", skipLines));
    sb.append(String.format(" -u %s ", username));
    sb.append(String.format(" -p%s ", password));
    sb.append(String.format(" -h %s ", host));
    sb.append(String.format(" %s %s ", database, filePath));
    String command = sb.toString();
    logger.info(String.format("Executing mysqlimport with command: %s", command));

    String[] commands = new String[]{ "/bin/bash", "-c", command }; // TODO: Support for Windows and other shells
    try {

      logger.debug(String.format("Importing file to MySQL: %s", filePath));
      process = Runtime.getRuntime().exec(commands);


      BufferedReader stdIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder outputBuilder = new StringBuilder();
      String line = stdIn.readLine();
      while (line != null){
        logger.debug(line);
        outputBuilder.append(line);
        line = stdIn.readLine();
      }
      stdIn.close();

      BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errorBuilder = new StringBuilder();
      line = stdErr.readLine();
      while (line != null){
        logger.debug(line);
        errorBuilder.append(line);
        line = stdErr.readLine();
      }
      stdErr.close();

      process.waitFor();

      Integer exitValue = process.exitValue();
      if (exitValue != 0){
        throw new DataImportException(String.format("MySQLImport failure for temp file: %s \n%s",
            filePath, errorBuilder.toString()));
      }

    } catch (Exception e){
      e.printStackTrace();
      throw new DataImportException(String.format("Unable to import temp file: %s", filePath));
    }
    logger.debug(String.format("CENTROMERE: MongoImport complete: %s", filePath));
  }
  
  private String getColumnString(String filePath) throws DataImportException{
    
    if (columns == null || columns.isEmpty()){
      columns = new ArrayList<>();
      File file = new File(filePath);
      Assert.isTrue(file.isFile(), "Target is not a file: " + filePath);
      Assert.isTrue(file.canRead(), "File is not readable: " + filePath);
      String line;
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new FileReader(file));
        line = reader.readLine();
      } catch (IOException e){
        e.printStackTrace();
        throw new DataImportException(e.getMessage());
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ex){
            ex.printStackTrace();
            throw new DataImportException(ex.getMessage());
          }
        }
      }
      Assert.notNull(line, "File is empty.");
      for (String bit: line.trim().split(delimiter)){
        columns.add(bit);
      }
    }
    
    StringBuilder s = new StringBuilder();
    boolean flag = false;
    for (String column: columns){
      if (flag) s.append(",");
      s.append(column);
      flag = true;
    }
    return s.toString();
  }
  
  private List<String> getModelColumns(Class<?> model){
    Assert.notNull(model, "Model must not be null if columns are not set.");
    return null;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public void setColumns(List<String> columns) {
    this.columns = columns;
  }

  public void setSkipLines(Integer skipLines) {
    this.skipLines = skipLines;
  }

  public void setStopOnError(boolean stopOnError) {
    this.stopOnError = stopOnError;
  }

  public void setDropTable(boolean dropTable) {
    this.dropTable = dropTable;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }
}
