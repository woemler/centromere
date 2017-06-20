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

package com.blueprint.centromere.cli;

import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.manifest.ImportManifest;
import com.blueprint.centromere.cli.manifest.ManifestFile;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.ImportOptions;
import com.blueprint.centromere.core.dataimport.ImportOptionsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * @author woemler
 * @since 0.5.0
 */
public class ManifestImportExecutor {
	
	private FileImportExecutor fileImportExecutor;
	private DataSetRepository dataSetRepository;
	private DataFileRepository dataFileRepository;
	private Environment environment;
	
	private static final Logger logger = LoggerFactory.getLogger(ManifestImportExecutor.class);
	private static final String PROPERTY_PREFIX = "centromere.import.";
	private static final String MANIFEST_PROPERTY_PREFIX = PROPERTY_PREFIX + "manifest.";
	
	public void run(String filePath) {
		
		// Get the manifest file
		File file = null;
		try {
			file = new File(filePath);
			Assert.isTrue(file.isFile(), String.format("File cannot be found: %s", filePath));
			Assert.isTrue(file.canRead(), String.format("File is not readable: %s", filePath));
		} catch (Exception e){
			throw new DataImportException(e);
		}
		File directory = file.getParentFile();
		Printer.print(String.format("Reading manifest file: %s", filePath), logger, Level.INFO);
		
		// Determine the data type and appropriate file parser
		ImportManifest manifest = null;
		ObjectMapper objectMapper = getManifestObjectMapper(filePath);
		try {
			Assert.notNull(objectMapper, "No suitable ObjectMapper found for the target file type.");
			manifest = objectMapper.readValue(file, ImportManifest.class);
			Assert.notNull(manifest, "Could not parse manifest file.");
		} catch (Exception e){
			throw new DataImportException(e);
		}
		
		// Set environemntal properties from manifest options.
		setEnvironmentProperties(manifest.getParameters());
    ImportOptions importOptions = new ImportOptionsImpl(environment);
		
		// Get the data set
    DataSet dataSet;
    Optional<DataSet> dsOptional = dataSetRepository.findByShortName(manifest.getShortName());
    if (dsOptional.isPresent()){
      dataSet = dsOptional.get();
      if (!importOptions.overwriteExistingDataSets()){
        Printer.print(String.format("Skipping existing data set: %s", manifest.getShortName()), 
            logger, Level.WARN);
        return;
      }
    } else {
      dataSet = new DataSet();
      dataSet.setShortName(manifest.getShortName());
    }
    if (manifest.getDisplayName() != null && !"".equalsIgnoreCase(manifest.getDisplayName())) {
      dataSet.setDisplayName(manifest.getDisplayName());
    }
    if (manifest.getDescription() != null && !"".equalsIgnoreCase(manifest.getDescription())) {
      dataSet.setDescription(manifest.getDescription());
    }
    if (manifest.getSource() != null && !"".equalsIgnoreCase(manifest.getSource())) {
      dataSet.setSource(manifest.getSource());
    }
    if (manifest.getVersion() != null && !"".equalsIgnoreCase(manifest.getVersion())) {
      dataSet.setVersion(manifest.getVersion());
    }
    dataSet.addAttributes(manifest.getAttributes());
    dataSet.addParameters(manifest.getParameters());
    if (dataSet.getId() != null){
      dataSet = dataSetRepository.update(dataSet);
    } else {
      dataSet = dataSetRepository.insert(dataSet);
    }
		
		// Import each file
		for (ManifestFile mf: manifest.getFiles()){
		  
		  Printer.print(String.format("Processing manifest file: %s", mf.toString()), logger, Level.INFO);

      DataFile dataFile = new DataFile();
      dataFile.addAttributes(mf.getAttributes());
		  String partialPath = mf.getPath();
			File df = new File(partialPath);
			if (!df.isAbsolute()){
				df = new File(directory, partialPath);
			}
			
			// Update environment properties
			setEnvironmentProperties(mf.getParameters());
      importOptions = new ImportOptionsImpl(environment);
			
      try {
        fileImportExecutor.run(mf.getType(), df.getAbsolutePath(), dataSet, dataFile);
      } catch (Exception e){
        if (importOptions.skipInvalidFiles()){
          logger.warn(String.format("File processing failed, skipping file: %s", 
              df.getAbsolutePath()));
          e.printStackTrace();
        } else {
          throw e;
        }
      }
		}
		
		Printer.print("Manifest import complete.", logger, Level.INFO);
		
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

	@Autowired
  public void setDataSetRepository(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Autowired
  public void setDataFileRepository(DataFileRepository dataFileRepository) {
    this.dataFileRepository = dataFileRepository;
  }
}
