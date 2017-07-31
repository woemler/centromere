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

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.model.Subject;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author woemler
 */
@Configuration
@PropertySource("classpath:data-import-defaults.properties")
@ConfigurationProperties("centromere.import")
public class DataImportProperties {

  private boolean skipInvalidRecords;
  private boolean skipInvalidGenes;
  private boolean skipInvalidSamples;
  private boolean skipInvalidFiles;
  private boolean skipInvalidMetadata;
  private boolean skipExistingFiles;
  private boolean overwriteExistingFiles;
  private boolean overwriteExistingDataSets;
  private String tempDir;
  private Map<String, String> attributes = new HashMap<>();
  
  private DataSet dataSet = new DataSet();
  private DataFile dataFile = new DataFile();
  private Subject subject = new Subject();
  private Sample sample = new Sample();

  public boolean isSkipInvalidRecords() {
    return skipInvalidRecords;
  }

  public void setSkipInvalidRecords(boolean skipInvalidRecords) {
    this.skipInvalidRecords = skipInvalidRecords;
  }

  public boolean isSkipInvalidGenes() {
    return skipInvalidGenes;
  }

  public void setSkipInvalidGenes(boolean skipInvalidGenes) {
    this.skipInvalidGenes = skipInvalidGenes;
  }

  public boolean isSkipInvalidSamples() {
    return skipInvalidSamples;
  }

  public void setSkipInvalidSamples(boolean skipInvalidSamples) {
    this.skipInvalidSamples = skipInvalidSamples;
  }

  public boolean isSkipInvalidFiles() {
    return skipInvalidFiles;
  }

  public void setSkipInvalidFiles(boolean skipInvalidFiles) {
    this.skipInvalidFiles = skipInvalidFiles;
  }

  public boolean isSkipInvalidMetadata() {
    return skipInvalidMetadata;
  }

  public void setSkipInvalidMetadata(boolean skipInvalidMetadata) {
    this.skipInvalidMetadata = skipInvalidMetadata;
  }

  public boolean isOverwriteExistingFiles() {
    return overwriteExistingFiles;
  }

  public void setOverwriteExistingFiles(boolean overwriteExistingFiles) {
    this.overwriteExistingFiles = overwriteExistingFiles;
  }

  public boolean isOverwriteExistingDataSets() {
    return overwriteExistingDataSets;
  }

  public void setOverwriteExistingDataSets(boolean overwriteExistingDataSets) {
    this.overwriteExistingDataSets = overwriteExistingDataSets;
  }

  public boolean isSkipExistingFiles() {
    return skipExistingFiles;
  }

  public void setSkipExistingFiles(boolean skipExistingFiles) {
    this.skipExistingFiles = skipExistingFiles;
  }

  public String getTempDir() {
    return tempDir;
  }

  public void setTempDir(String tempDir) {
    this.tempDir = tempDir;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  public boolean hasAttribute(String attribute){
    return this.attributes.containsKey(attribute);
  }

  public String getAttribute(String attribute){
    return this.attributes.getOrDefault(attribute, null);
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  public DataFile getDataFile() {
    return dataFile;
  }

  public void setDataFile(DataFile dataFile) {
    this.dataFile = dataFile;
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public String toString() {
    return "DataImportProperties{" +
        "skipInvalidRecords=" + skipInvalidRecords +
        ", skipInvalidGenes=" + skipInvalidGenes +
        ", skipInvalidSamples=" + skipInvalidSamples +
        ", skipInvalidFiles=" + skipInvalidFiles +
        ", skipInvalidMetadata=" + skipInvalidMetadata +
        ", skipExistingFiles=" + skipExistingFiles +
        ", overwriteExistingFiles=" + overwriteExistingFiles +
        ", overwriteExistingDataSets=" + overwriteExistingDataSets +
        ", tempDir='" + tempDir + '\'' +
        ", attributes=" + attributes +
        ", dataSet=" + dataSet +
        ", dataFile=" + dataFile +
        ", subject=" + subject +
        ", sample=" + sample +
        '}';
  }
}
