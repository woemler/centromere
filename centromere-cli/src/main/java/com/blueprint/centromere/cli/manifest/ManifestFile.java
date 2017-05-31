/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.cli.manifest;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author woemler
 * @since 0.5.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManifestFile {

  private String path;
  private String type;
  private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
  private LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

  public String getPath() {
      return path;
  }

  public void setPath(String path) {
      this.path = path;
  }

  public String getType() {
      return type;
  }

  public void setType(String type) {
      this.type = type;
  }

  public LinkedHashMap<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(LinkedHashMap<String, String> parameters) {
    this.parameters = parameters;
  }

  public LinkedHashMap<String, String> getAttributes() {
      return attributes;
  }

  public void setAttributes(LinkedHashMap<String, String> attributes) {
      this.attributes = attributes;
  }
  
  public DataFile createDataFile(){
    DataFile dataFile = new DataFile();
    dataFile.setFilePath(path);
    dataFile.setDataType(type);
    dataFile.setDateCreated(new Date());
    dataFile.setDateUpdated(new Date());
    dataFile.setAttributes(attributes);
    return dataFile;
  }

  @Override 
  public String toString() {
    return "ManifestFile{" +
      "path='" + path + '\'' +
      ", type='" + type + '\'' +
      ", attributes=" + attributes +
      '}';
  }
}
