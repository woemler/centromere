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

package com.blueprint.centromere.core.dataimport.manifest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author woemler
 * @since 0.5.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportManifest {

    private String name;
    private String label;
    private String notes;
    private String source;
    private String version;
    private Date dateCreated;
    private LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
    private LinkedHashMap<String, ManifestFile> files = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LinkedHashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedHashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public LinkedHashMap<String, ManifestFile> getFiles() {
        return files;
    }

    public void setFiles(LinkedHashMap<String, ManifestFile> files) {
        this.files = files;
    }
}
