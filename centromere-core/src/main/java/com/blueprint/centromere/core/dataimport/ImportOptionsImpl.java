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
import org.springframework.core.env.Environment;

/**
 * @author woemler
 */
public class ImportOptionsImpl implements ImportOptions {

  private boolean skipInvalidRecords = false;
  private boolean skipInvalidSamples = false;
  private boolean skipInvalidGenes = false;
  private boolean skipInvalidFiles = false;
  private boolean skipExistingFiles = false;
  private String tempFilePath = System.getProperty("java.io.tmpdir");

  public ImportOptionsImpl() {
  }

  public ImportOptionsImpl(Environment environment) {
    if (environment.containsProperty(ApplicationProperties.SKIP_INVALID_RECORDS)){
      skipInvalidRecords = environment.getProperty(ApplicationProperties.SKIP_INVALID_RECORDS, Boolean.class);
    }
    if (environment.containsProperty(ApplicationProperties.SKIP_INVALID_SAMPLES)){
      skipInvalidSamples = environment.getProperty(ApplicationProperties.SKIP_INVALID_SAMPLES, Boolean.class);
    }
    if (environment.containsProperty(ApplicationProperties.SKIP_INVALID_GENES)){
      skipInvalidGenes = environment.getProperty(ApplicationProperties.SKIP_INVALID_GENES, Boolean.class);
    }
    if (environment.containsProperty(ApplicationProperties.SKIP_INVALID_FILES)){
      skipInvalidFiles = environment.getProperty(ApplicationProperties.SKIP_EXISTING_FILES, Boolean.class);
    }
    if (environment.containsProperty(ApplicationProperties.SKIP_EXISTING_FILES)){
      skipExistingFiles = environment.getProperty(ApplicationProperties.SKIP_EXISTING_FILES, Boolean.class);
    }
    if (environment.containsProperty(ApplicationProperties.TEMP_DIR)){
      String tempPath = environment.getProperty(ApplicationProperties.TEMP_DIR);
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

  public void setTempFilePath(String tempFilePath) {
    this.tempFilePath = tempFilePath;
  }
}