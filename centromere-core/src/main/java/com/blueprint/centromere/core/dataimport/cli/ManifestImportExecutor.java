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

package com.blueprint.centromere.core.dataimport.cli;

import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.manifest.ImportManifest;
import com.blueprint.centromere.core.dataimport.manifest.ManifestFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Map;

/**
 * @author woemler
 * @since 0.5.0
 */
public class ManifestImportExecutor {
	
	private FileImportExecutor fileImportExecutor;
	private Environment environment;
	
	private static final Logger logger = LoggerFactory.getLogger(ManifestImportExecutor.class);
	private static final String PROPERTY_PREFIX = "centromere.import.";
	private static final String MANIFEST_PROPERTY_PREFIX = PROPERTY_PREFIX + "manifest.";
	
	public void run(String filePath) throws DataImportException {
		
		// Get the manifest file
		File file = null;
		try {
			file = new File(filePath);
			Assert.isTrue(file.isFile(), String.format("File cannot be found: %s", filePath));
			Assert.isTrue(file.canRead(), String.format("File is not readable: %s", filePath));
		} catch (Exception e){
			e.printStackTrace();
			throw new DataImportException(e.getMessage());
		}
		File directory = file.getParentFile();
		logger.info(String.format("Reading manifest file: %s", filePath));
		
		// Determine the data type and appropriate file parser
		ImportManifest manifest = null;
		ObjectMapper objectMapper = getManifestObjectMapper(filePath);
		try {
			Assert.notNull(objectMapper, "No suitable ObjectMapper found for the target file type.");
			manifest = objectMapper.readValue(file, ImportManifest.class);
			Assert.notNull(manifest, "Could not parse manifest file.");
		} catch (Exception e){
			e.printStackTrace();
			throw new DataImportException(e.getMessage());
		}
		
		// Set environemntal properties from manifest options.
		setEnvironmentProperties(manifest.getAttributes());
		
		// Import each file
		for (ManifestFile mf: manifest.getFiles()){
			String partialPath = mf.getPath();
			File dataFile = new File(partialPath);
			if (!dataFile.isAbsolute()){
				dataFile = new File(directory, partialPath);
			}
			setEnvironmentProperties(mf.getAttributes());
			fileImportExecutor.run(mf.getType(), dataFile.getAbsolutePath());
		}
	}
	
	private ObjectMapper getManifestObjectMapper(String filePath){
		if (filePath.toLowerCase().endsWith(".yml") || filePath.toLowerCase().endsWith(".yaml")){
			return new ObjectMapper(new YAMLFactory());
		} else if (filePath.toLowerCase().endsWith(".json")){
			return new ObjectMapper();
		} else if (filePath.toLowerCase().endsWith(".xml")){
			return new XmlMapper();
		} else {
			return null;
		}
	}
	
	private void setEnvironmentProperties(Map<String,String> properties, String prefix){
		for (Map.Entry<String, String> entry: properties.entrySet()){
			System.setProperty(prefix + entry.getKey().toLowerCase(), entry.getValue());
			logger.info(String.format("Setting system property: %s = %s", entry.getKey().toLowerCase(), 
					entry.getValue().toLowerCase()));
		}
	}
	
	private void setEnvironmentProperties(Map<String, String> properties){
		this.setEnvironmentProperties(properties, PROPERTY_PREFIX);
	}

	@Autowired
	public void setFileImportExecutor(FileImportExecutor fileImportExecutor) {
		this.fileImportExecutor = fileImportExecutor;
	}

	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
