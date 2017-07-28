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

package com.blueprint.centromere.core.dataimport.importer;

import com.blueprint.centromere.core.config.DatabaseProperties;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

/**
 * @author woemler
 * @since 0.5.0
 */
public class MongoImportTempFileImporter<T extends Model<?>> extends AbstractFileImporter<T> 
    implements ModelSupport<T> {

  private final static Logger logger = LoggerFactory.getLogger(MongoImportTempFileImporter.class);
  
  private final Class<T> model;
  private final DatabaseProperties databaseProperties;
  
  private boolean stopOnError = true;
  private boolean upsertRecords = false;
  private boolean dropCollection = false;

  public MongoImportTempFileImporter(Class<T> model,
      DatabaseProperties databaseProperties) {
    this.model = model;
    this.databaseProperties = databaseProperties;
  }

  @Override
  public void doBefore() throws DataImportException {
    super.doBefore();
    try {
      Assert.notNull(databaseProperties.getHost(), "Database host not set.");
      Assert.notNull(databaseProperties.getName(), "Database name not set.");
      Assert.notNull(databaseProperties.getUser(), "Database username not set.");
      Assert.notNull(databaseProperties.getPassword(), "Database password not set.");
    } catch (Exception e){
      throw new DataImportException(e);
    }
  }

  /**
   * Performs the temporary file import and captures all output.
   *
   * @param filePath
   */
  @Override
  public void importFile(String filePath) throws DataImportException {

    Process process;
    String[] commands = new String[]{ "/bin/bash", "-c", buildImportCommand(filePath) }; // TODO: Support for Windows and other shells
    try {

      logger.debug(String.format("Importing file to MongoDB: %s", filePath));
      for (String cmd: commands) {
        logger.debug(cmd);
      }
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
      throw new DataImportException(String.format("Unable to import temp file: %s", filePath), e);
    }
    logger.debug(String.format("CENTROMERE: MongoImport complete: %s", filePath));
  }

  /**
   * Creates the command string for the import, based upon parametrization.
   *
   * @param filePath
   * @return
   */
  private String buildImportCommand(String filePath){
    StringBuilder sb = new StringBuilder("mongoimport ");
    if (stopOnError) sb.append(" --stopOnError ");
    if (dropCollection) sb.append(" --drop ");
    if (upsertRecords) sb.append(" --upsert ");
    if (databaseProperties.getUser() != null) {
      sb.append(String.format(" --username %s ", databaseProperties.getUser()));
    }
    if (databaseProperties.getPassword() != null){
      sb.append(String.format(" --password %s ", databaseProperties.getPassword()));
    }
    if (databaseProperties.getHost() != null) {
      if (databaseProperties.getHost().contains(":")) {
        sb.append(String.format(" --host %s ", databaseProperties.getHost()));
      } else if (databaseProperties.getPort() != null) {
        sb.append(String.format(" --host %s:%s ", databaseProperties.getHost(), databaseProperties.getPort()));
      } else {
        sb.append(String.format(" --host %s:27017 ", databaseProperties.getHost()));
      }
    }
    sb.append(String.format(" --db %s ", databaseProperties.getName()));
    sb.append(String.format(" --collection %s ", getCollectionName()));
    sb.append(String.format(" --file %s ", filePath));
    return sb.toString();
  }
  
  private String getCollectionName(){
    String name = model.getSimpleName();
    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
    if (model.isAnnotationPresent(Document.class)){
      Document document = model.getAnnotation(Document.class);
      if (!document.collection().equals("")) name = document.collection();
    }
    return name;
  }

  public MongoImportTempFileImporter setStopOnError(boolean stopOnError) {
    this.stopOnError = stopOnError;
    return this;
  }

  public MongoImportTempFileImporter setUpsertRecords(boolean upsertRecords) {
    this.upsertRecords = upsertRecords;
    return this;
  }

  public MongoImportTempFileImporter setDropCollection(boolean dropCollection) {
    this.dropCollection = dropCollection;
    return this;
  }

  public DatabaseProperties getDatabaseProperties() {
    return databaseProperties;
  }

  @Override
  public Class<T> getModel() {
    return model;
  }

  @Override
  public void setModel(Class<T> model) { }
}
