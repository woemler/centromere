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
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * Runs {@code mysqlimport} and imports the supplied file through using the command line tool.
 * 
 * @author woemler
 * @since 0.5.0
 */
public class MySqlImportFileImporter implements RecordImporter {
  
  private static final Logger logger = LoggerFactory.getLogger(MySqlImportFileImporter.class);
  
  private boolean stopOnError = true;
  private boolean dropCollection = false;
  private String columns;
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
      String password, String database, String columns) {
    this.columns = columns;
    this.host = host;
    this.username = username;
    this.password = password;
    this.database = database;
  }

  @Override
  public void importFile(String filePath) throws DataImportException {

    Process process;

    StringBuilder sb = new StringBuilder("mysqlimport --local ");
    if (!stopOnError) sb.append(" --force ");
    if (dropCollection) sb.append(" --delete ");
    if (columns != null) sb.append(String.format(" -c %s ", columns));
    sb.append(String.format(" -u %s ", username));
    sb.append(String.format(" -p%s ", password));
    sb.append(String.format(" -h %s ", host));
    sb.append(String.format(" %s %s ", database, filePath));
    String command = sb.toString();
    logger.debug(String.format("CENTROMERE: Executing mysqlimport with command: %s", command));

    String[] commands = new String[]{ "/bin/bash", "-c", command }; // TODO: Support for Windows and other shells
    try {

      logger.debug(String.format("CENTROMERE: Importing file to MySQL: %s", filePath));
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
        throw new DataImportException(String.format("MongoImport failure for temp file: %s \n%s",
            filePath, errorBuilder.toString()));
      }

    } catch (Exception e){
      e.printStackTrace();
      throw new DataImportException(String.format("Unable to import temp file: %s", filePath));
    }
    logger.debug(String.format("CENTROMERE: MongoImport complete: %s", filePath));
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public void setColumns(String columns) {
    this.columns = columns;
  }
}
