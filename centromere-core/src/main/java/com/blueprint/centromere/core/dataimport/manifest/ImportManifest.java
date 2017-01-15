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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

/**
 * @author woemler
 * @since 0.5.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportManifest {

    @XmlAnyElement(lax = true)
    private LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

    private List<ManifestFile> files = new ArrayList<>();

    @JsonAnySetter
    public void setAttribute(String key, String value){
        attributes.put(key, value);
    }

    @JsonAnyGetter
    public LinkedHashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedHashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<ManifestFile> getFiles() {
        return files;
    }

    public void setFiles(
        List<ManifestFile> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "ImportManifest{" +
                "attributes=" + attributes +
                ", files=" + files +
                '}';
    }

}
