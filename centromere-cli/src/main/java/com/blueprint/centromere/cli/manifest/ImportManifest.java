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

import com.blueprint.centromere.core.commons.model.DataSet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author woemler
 * @since 0.5.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportManifest {
    
  private String name;
  private String shortName;
  private String displayName;
  private String source;
  private String version;
  private String description;
  private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
  private LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
  private List<ManifestFile> files = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public List<ManifestFile> getFiles() {
    return files;
  }

  public void setFiles(List<ManifestFile> files) {
    this.files = files;
  }
  
  public DataSet createDataSet(){
    DataSet dataSet = new DataSet();
    if (name != null){
      dataSet.setDisplayName(name);
      dataSet.setShortName(name.toLowerCase().replaceAll("[^a-z0-9\\s]]", "").replaceAll("\\s+", "-"));
    }
    if (displayName != null) dataSet.setDisplayName(displayName);
    if (shortName != null) dataSet.setShortName(shortName);
    dataSet.setDescription(description);
    dataSet.setSource(source);
    dataSet.setVersion(version);
    dataSet.setAttributes(attributes);
    return dataSet;
  }

  @Override
  public String toString() {
    return "ImportManifest{" +
        "name='" + name + '\'' +
        ", shortName='" + shortName + '\'' +
        ", displayName='" + displayName + '\'' +
        ", source='" + source + '\'' +
        ", version='" + version + '\'' +
        ", description='" + description + '\'' +
        ", parameters=" + parameters +
        ", attributes=" + attributes +
        ", files=" + files +
        '}';
  }
}
