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

import com.blueprint.centromere.core.config.Properties;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.blueprint.centromere.core.model.Model;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

/**
 * @author woemler
 * @since 0.5.0
 */
public class MongoImportTempFileImporter<T extends Model<?>> extends AbstractFileImporter<T> {

  private boolean stopOnError = true;
  private boolean upsertRecords = false;
  private boolean dropCollection = false;
  private ImportOptions options = new ImportOptionsImpl();
  private final Environment environment;

  private final static Logger logger = LoggerFactory.getLogger(MongoImportTempFileImporter.class);

  public MongoImportTempFileImporter(Class<T> model, Environment environment) {
    super(model);
    this.environment = environment;
  }

  @Override
  public void doBefore(Object... args)  {
    Assert.isTrue(environment.containsProperty(Properties.DB_HOST),
        String.format("Environment property must not be null: %s", Properties.DB_HOST));
    Assert.isTrue(environment.containsProperty(Properties.DB_NAME),
        String.format("Environment property must not be null: %s", Properties.DB_NAME));
    Assert.isTrue(environment.containsProperty(Properties.DB_USER),
        String.format("Environment property must not be null: %s", Properties.DB_USER));
    Assert.isTrue(environment.containsProperty(Properties.DB_PASSWORD),
        String.format("Environment property must not be null: %s", Properties.DB_PASSWORD));
  }

  /**
   * Performs the temporary file import and captures all output.
   *
   * @param filePath
   */
  public void importFile(String filePath) {

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
    if (environment.containsProperty(Properties.DB_USER)) {
      sb.append(String.format(" --username %s ", environment.getRequiredProperty(Properties.DB_USER)));
    }
    if (environment.containsProperty(Properties.DB_PASSWORD)){
      sb.append(String.format(" --password %s ", environment.getRequiredProperty(Properties.DB_PASSWORD)));
    }
    if (environment.containsProperty(Properties.DB_HOST)) {
      if (environment.getRequiredProperty(Properties.DB_HOST).contains(":")) {
        sb.append(String.format(" --host %s ", environment.getRequiredProperty(Properties.DB_HOST)));
      } else if (environment.containsProperty(Properties.DB_PORT)) {
        sb.append(String.format(" --host %s:%s ", environment.getRequiredProperty(Properties.DB_HOST),
            environment.getRequiredProperty(Properties.DB_PORT)));
      } else {
        sb.append(String.format(" --host %s:27017 ", environment.getRequiredProperty(Properties.DB_HOST)));
      }
    }
    sb.append(String.format(" --db %s ", environment.getRequiredProperty(Properties.DB_NAME)));
    sb.append(String.format(" --collection %s ", getCollectionName()));
    sb.append(String.format(" --file %s ", filePath));
    return sb.toString();
  }
  
  private String getCollectionName(){
    String name = this.getModel().getSimpleName();
    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
    if (this.getModel().isAnnotationPresent(Document.class)){
      Document document = this.getModel().getAnnotation(Document.class);
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

}
