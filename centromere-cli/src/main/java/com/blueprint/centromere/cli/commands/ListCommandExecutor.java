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
import com.blueprint.centromere.cli.ModelProcessorBeanRegistry;
import com.blueprint.centromere.cli.Printer;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.model.Model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author woemler
 */
public class ListCommandExecutor implements EnvironmentAware {

  private static final Logger logger = LoggerFactory.getLogger(ListCommandExecutor.class);
  private static final List<String> listable = Arrays.asList("datatype", "dataset", "model", 
      "datafile");

  private ModelProcessorBeanRegistry processorRegistry;
  private DataSetRepository dataSetRepository;
  private DataFileRepository dataFileRepository;
  private Environment environment;

  public void run(String arg, boolean showDetails) throws CommandLineRunnerException {

    arg = arg.trim().toLowerCase().replaceAll("-", "");
    if (!listable.contains(arg)){
      throw new CommandLineRunnerException("Unknown listable option: " + arg);
    }

    switch (arg){

      case "datatype":
        List<String> dataTypes = new ArrayList<>();
        for (Map.Entry<String, String> entry: processorRegistry.getDataTypeDescriptionMap().entrySet()){
          dataTypes.add(entry.getKey() + (showDetails ? ": " + entry.getValue() : ""));
        }
        if (!dataTypes.isEmpty()){
          Collections.sort(dataTypes);
          System.out.println("Registered data types:\n");
          for (String type: dataTypes){
            System.out.println("  " + type);
          }
        } else {
          System.out.println("No data types are currently registered!");
        }
        System.out.println();
        return;

      case "model":
        List<String> models = new ArrayList<>();
        for (Class<? extends Model> model: processorRegistry.getRegisteredModels()){
          models.add(showDetails ? model.getName() : model.getSimpleName());
        }
        if (!models.isEmpty()){
          Collections.sort(models);
          System.out.println("Registered models:\n");
          for (String model: models){
            System.out.println("  " + model);
          }
        } else {
          System.out.println("No models are currently registered!");
        }
        System.out.println();
        return;

      case "dataset":
        
        List<String> dataSets = new ArrayList<>();
        for (DataSet dataSet: dataSetRepository.findAll()){
          dataSets.add(showDetails ? dataSet.toString() : dataSet.getDisplayName());
        }
        if (!dataSets.isEmpty()){
          Collections.sort(dataSets);
          System.out.println("Registered data sets:\n");
          for (String dataSet: dataSets){
            System.out.println("  " + dataSet);
          }
        } else {
          System.out.println("No data sets registered!");
        }
        System.out.println();
        return;

      case "datafile":
        List<String> dataFiles = new ArrayList<>();
        for (DataFile dataFile: dataFileRepository.findAll()){
          dataFiles.add(showDetails ? dataFile.toString() : dataFile.getFilePath());
        }
        if (!dataFiles.isEmpty()){
          Collections.sort(dataFiles);
          System.out.println("Registered data files:\n");
          for (String dataFile: dataFiles){
            System.out.println("  " + dataFile);
          }
        } else {
          System.out.println("No data files registered!");
        }
        System.out.println();
        return;

      default:
        Printer.print("Unknown listable option: " + arg, logger, Level.WARN);
        System.out.println(String.format("Available list commands: %s", listable));

    }

  }

  public void run(String arg) throws CommandLineRunnerException{
    run(arg, false);
  }

  @Autowired
  public void setProcessorRegistry(ModelProcessorBeanRegistry processorRegistry) {
    this.processorRegistry = processorRegistry;
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public void setDataSetRepository(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Autowired
  public void setDataFileRepository(DataFileRepository dataFileRepository) {
    this.dataFileRepository = dataFileRepository;
  }

  @Autowired
  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
