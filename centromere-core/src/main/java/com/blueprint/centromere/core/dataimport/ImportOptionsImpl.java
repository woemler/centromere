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

import com.blueprint.centromere.core.config.ApplicationOptionsImpl;
import com.blueprint.centromere.core.config.Properties;
import org.springframework.core.env.Environment;

/**
 * @author woemler
 */
@Deprecated
public class ImportOptionsImpl extends ApplicationOptionsImpl implements ImportOptions {

  private boolean skipInvalidRecords = false;
  private boolean skipInvalidSamples = false;
  private boolean skipInvalidGenes = false;
  private boolean skipInvalidFiles = false;
  private boolean skipExistingFiles = true;
  private boolean overwriteExistingFiles = true;
  private boolean overwriteExistingDataSets = false;
  private String tempFilePath = System.getProperty("java.io.tmpdir");

  public ImportOptionsImpl(Environment environment) {
    super(environment);
    if (environment.containsProperty(Properties.SKIP_INVALID_RECORDS)){
      skipInvalidRecords = environment.getProperty(Properties.SKIP_INVALID_RECORDS, Boolean.class);
    }
    if (environment.containsProperty(Properties.SKIP_INVALID_SAMPLES)){
      skipInvalidSamples = environment.getProperty(Properties.SKIP_INVALID_SAMPLES, Boolean.class);
    }
    if (environment.containsProperty(Properties.SKIP_INVALID_GENES)){
      skipInvalidGenes = environment.getProperty(Properties.SKIP_INVALID_GENES, Boolean.class);
    }
    if (environment.containsProperty(Properties.SKIP_INVALID_FILES)){
      skipInvalidFiles = environment.getProperty(Properties.SKIP_EXISTING_FILES, Boolean.class);
    }
    if (environment.containsProperty(Properties.SKIP_EXISTING_FILES)){
      skipExistingFiles = environment.getProperty(Properties.SKIP_EXISTING_FILES, Boolean.class);
    }
    if (environment.containsProperty(Properties.OVERWRITE_EXISTING_FILES)){
      overwriteExistingFiles = environment.getProperty(Properties.OVERWRITE_EXISTING_FILES, Boolean.class);
    }
    if (environment.containsProperty(Properties.OVERWRITE_EXISTING_DATA_SETS)){
      overwriteExistingDataSets = environment.getProperty(Properties.OVERWRITE_EXISTING_DATA_SETS, Boolean.class);
    }
    if (environment.containsProperty(Properties.TEMP_DIR)){
      String tempPath = environment.getProperty(Properties.TEMP_DIR);
      if (tempPath != null && !tempPath.trim().equals("")) tempFilePath = tempPath;
    }
  }

  @Override
  public boolean skipInvalidRecords() {
    return skipInvalidRecords;
  }

  @Override
  public boolean skipInvalidSamples() {
    return skipInvalidSamples;
  }

  @Override
  public boolean skipInvalidGenes() {
    return skipInvalidGenes;
  }

  @Override
  public boolean skipInvalidFiles() {
    return skipInvalidFiles;
  }

  @Override
  public boolean skipExistingFiles() {
    return skipExistingFiles;
  }

  @Override
  public boolean overwriteExistingFiles() {
    return overwriteExistingFiles;
  }

  @Override
  public boolean overwriteExistingDataSets() {
    return overwriteExistingDataSets;
  }

  @Override
  public String getTempFilePath() {
    return tempFilePath;
  }

  public void setSkipInvalidRecords(boolean skipInvalidRecords) {
    this.skipInvalidRecords = skipInvalidRecords;
  }

  public void setSkipInvalidSamples(boolean skipInvalidSamples) {
    this.skipInvalidSamples = skipInvalidSamples;
  }

  public void setSkipInvalidGenes(boolean skipInvalidGenes) {
    this.skipInvalidGenes = skipInvalidGenes;
  }

  public void setSkipInvalidFiles(boolean skipInvalidFiles) {
    this.skipInvalidFiles = skipInvalidFiles;
  }

  public void setSkipExistingFiles(boolean skipExistingFiles) {
    this.skipExistingFiles = skipExistingFiles;
  }

  public void setOverwriteExistingFiles(boolean overwriteExistingFiles) {
    this.overwriteExistingFiles = overwriteExistingFiles;
  }

  public void setOverwriteExistingDataSets(boolean overwriteExistingDataSets) {
    this.overwriteExistingDataSets = overwriteExistingDataSets;
  }

  public void setTempFilePath(String tempFilePath) {
    this.tempFilePath = tempFilePath;
  }
}
