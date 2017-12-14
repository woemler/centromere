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

package com.blueprint.centromere.core.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author woemler
 */
@Configuration
@PropertySource("classpath:data-import-defaults.properties")
@ConfigurationProperties("centromere.import")
@Data
public class DataImportProperties {

  private boolean skipInvalidRecords;
  private boolean skipInvalidGenes;
  private boolean skipInvalidSamples;
  private boolean skipInvalidFiles;
  private boolean skipInvalidMetadata;
  private boolean skipExistingFiles;
  private boolean forceFileOverwrite;
  private boolean overwriteExistingDataSets;
  private String tempDir;
  private Map<String, String> attributes = new HashMap<>();
  
//  private DataSet dataSet = new DataSet();
//  private DataFile dataFile = new DataFile();
//  private Sample sample = new Sample();
  
  public boolean hasAttribute(String attribute){
    return this.attributes.containsKey(attribute);
  }

  public String getAttribute(String attribute){
    return this.attributes.getOrDefault(attribute, null);
  }

}
