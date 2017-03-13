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

import com.blueprint.centromere.core.config.ApplicationProperties;
import com.blueprint.centromere.core.model.Model;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
public class MongoImportTempFileImporter<T extends Model<?>> implements RecordImporter {

  private final Class<T> model;
  private final Environment env;
  private boolean stopOnError = true;
  private boolean upsertRecords = false;
  private boolean dropCollection = false;

  private final static Logger logger = LoggerFactory.getLogger(MongoImportTempFileImporter.class);

  public MongoImportTempFileImporter(Class<T> model, Environment environment) {
    this.model = model;
    this.env = environment;
  }

  /**
   * Performs the temporary file import and captures all output.
   *
   * @param filePath
   * @throws DataImportException
   */
  public void importFile(String filePath) throws DataImportException {

    Process process;
    String[] commands = new String[]{ "/bin/bash", "-c", buildImportCommand(filePath) }; // TODO: Support for Windows and other shells
    try {

      logger.debug(String.format("[CENTROMERE] Importing file to MongoDB: %s", filePath));
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
      e.printStackTrace();
      throw new DataImportException(String.format("Unable to import temp file: %s", filePath));
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
    if (env.containsProperty(ApplicationProperties.DB_USER)) {
      sb.append(String.format(" --username %s ", env.getRequiredProperty(ApplicationProperties.DB_USER)));
    }
    if (env.containsProperty(ApplicationProperties.DB_PASSWORD)){
      sb.append(String.format(" --password %s ", env.getRequiredProperty(ApplicationProperties.DB_PASSWORD)));
    }
    if (env.containsProperty(ApplicationProperties.DB_HOST)) {
      if (env.getRequiredProperty(ApplicationProperties.DB_HOST).contains(":")) {
        sb.append(String.format(" --host %s ", env.getRequiredProperty(ApplicationProperties.DB_HOST)));
      } else if (env.containsProperty(ApplicationProperties.DB_PORT)) {
        sb.append(String.format(" --host %s:%s ", env.getRequiredProperty(ApplicationProperties.DB_HOST), 
            env.getRequiredProperty(ApplicationProperties.DB_PORT)));
      } else {
        sb.append(String.format(" --host %s:27017 ", env.getRequiredProperty(ApplicationProperties.DB_HOST)));
      }
    }
    sb.append(String.format(" --db %s ", env.getRequiredProperty(ApplicationProperties.DB_NAME)));
    sb.append(String.format(" --collection %s ", getCollectionName()));
    sb.append(String.format(" --file %s ", filePath));
    return sb.toString();
  }
  
  private String getCollectionName(){
    String name = model.getSimpleName();
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

  @Override
  public Environment getEnvironment() {
    return env;
  }

  @Override
  public void setEnvironment(Environment environment) {

  }
}
