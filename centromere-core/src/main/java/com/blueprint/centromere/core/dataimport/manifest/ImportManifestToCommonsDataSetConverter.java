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

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.DataSet;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts a {@link ImportManifest} instance into a {@link DataSet} record and multiple {@link DataFile}
 *   records.
 *
 * @author woemler
 * @since 0.5.0
 */
public class ImportManifestToCommonsDataSetConverter implements ManifestConverter<DataSet> {

    @Override
    public DataSet convert(ImportManifest manifest) {
        Assert.notNull(manifest.getName(), "DataSet name must not be null.");
        Assert.notNull(manifest.getSource(), "DataSet source must not be null.");
        Assert.notEmpty(manifest.getFiles(), "DataSet must contain at least one DataFile instances.");
        DataSet dataSet = new DataSet();
        dataSet.setName(manifest.getName());
        dataSet.setSource(manifest.getSource());
        dataSet.setVersion(manifest.getVersion());
        dataSet.setDescription(manifest.getNotes());
        for (Map.Entry<String, Object> entry: manifest.getAttributes().entrySet()){
            dataSet.addAttribute(entry.getKey(), entry.getValue().toString());
        }
        List<DataFile> dataFiles = new ArrayList<>();
        for (Map.Entry<String, ManifestFile> entry: manifest.getFiles().entrySet()){
            ManifestFile file = entry.getValue();
            DataFile dataFile = new DataFile();
            dataFile.setFilePath(entry.getKey());
            dataFile.setDataType(file.getType());
            for (Map.Entry<String, Object> attribute: file.getAttributes().entrySet()){
                dataFile.addAttribute(attribute.getKey(), attribute.getValue().toString());
            }
            dataFiles.add(dataFile);
        }
        dataSet.setDataFiles(dataFiles);
        return dataSet;
    }
}
