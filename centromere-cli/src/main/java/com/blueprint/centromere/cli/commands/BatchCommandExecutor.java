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

package com.blueprint.centromere.cli.commands;

import com.blueprint.centromere.cli.CommandLineRunnerException;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.manifest.ImportManifest;
import com.blueprint.centromere.cli.manifest.ManifestFile;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.model.impl.DataSet;
import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.repository.impl.DataSetRepository;
import com.blueprint.centromere.core.repository.impl.DataSourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author woemler
 * @since 0.5.0
 */
public class BatchCommandExecutor {
	
	private ImportCommandExecutor importCommandExecutor;
	private DataSetRepository dataSetRepository;
	private DataSourceRepository dataSourceRepository;
	private DataImportProperties dataImportProperties;
	
	private static final Logger logger = LoggerFactory.getLogger(BatchCommandExecutor.class);
	private static final String PROPERTY_PREFIX = "centromere.import.";
	private static final String MANIFEST_PROPERTY_PREFIX = PROPERTY_PREFIX + "manifest.";
	
	public void run(String filePath) throws CommandLineRunnerException {
		
		// Get the manifest file
		File file = null;
		try {
			file = new File(filePath);
			Assert.isTrue(file.isFile(), String.format("File cannot be found: %s", filePath));
			Assert.isTrue(file.canRead(), String.format("File is not readable: %s", filePath));
		} catch (Exception e){
			throw new CommandLineRunnerException(e);
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
			throw new CommandLineRunnerException(e);
		}
		
		// Set environemntal properties from manifest options.
		setEnvironmentProperties(manifest.getParameters());
		
		// Get the data set
    DataSet dataSet;
    Optional<DataSet> dsOptional = dataSetRepository.findByDataSetId(manifest.getDataSetId());
    if (dsOptional.isPresent()){
      dataSet = dsOptional.get();
      if (!dataImportProperties.isOverwriteExistingDataSets()){
        Printer.print(String.format("Skipping existing data set: %s", manifest.getDataSetId()), 
            logger, Level.WARN);
        return;
      }
    } else {
      try {
        dataSet = (DataSet) dataSetRepository.getModel().newInstance();
      } catch (Exception e){
        throw new CommandLineRunnerException(e);
      }
      dataSet.setDataSetId(manifest.getDataSetId());
    }
    if (manifest.getName() != null && !"".equalsIgnoreCase(manifest.getName())) {
      dataSet.setName(manifest.getName());
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
      dataSetRepository.update(dataSet);
    } else {
      dataSetRepository.insert(dataSet);
    }
		
		// Import each file
		for (ManifestFile mf: manifest.getFiles()){
		  
		  Printer.print(String.format("Processing manifest file: %s", mf.toString()), logger, Level.INFO);

      DataSource dataSource;
      try {
        dataSource = (DataSource) dataSourceRepository.getModel().newInstance();
      } catch (Exception e){
        throw new CommandLineRunnerException(e);
      }
      dataSource.addAttributes(mf.getAttributes());
		  String partialPath = mf.getPath();
			File df = new File(partialPath);
			if (!df.isAbsolute()){
				df = new File(directory, partialPath);
			}
			
			// Update environment properties
			setEnvironmentProperties(mf.getParameters());
			
			//TODO: Set data set and data file objects in args
      ImportCommandParameters importCommandParameters = new ImportCommandParameters();
			
      try {
        importCommandExecutor.run(importCommandParameters);
      } catch (Exception e){
        if (dataImportProperties.isSkipInvalidDataSource()){
          logger.warn(String.format("File processing failed, skipping file: %s", 
              df.getAbsolutePath()));
          logger.error(e.toString());
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
	public void setImportCommandExecutor(ImportCommandExecutor importCommandExecutor) {
		this.importCommandExecutor = importCommandExecutor;
	}

	@Autowired
  public void setDataImportProperties(DataImportProperties dataImportProperties) {
    this.dataImportProperties = dataImportProperties;
  }

  @Autowired
  public void setDataSetRepository(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Autowired
  public void setDataSourceRepository(DataSourceRepository dataSourceRepository) {
    this.dataSourceRepository = dataSourceRepository;
  }
}
