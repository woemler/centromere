package com.blueprint.centromere.cli;

import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.repository.DataFileRepository;
import com.blueprint.centromere.core.commons.repository.DataSetRepository;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.model.Model;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author woemler
 */
public class ListCommandExecutor implements EnvironmentAware {

  private static final List<String> listable = Arrays.asList("datatype", "dataset", "model");

  private ModelProcessorBeanRegistry processorRegistry;
  private DataSetRepository dataSetRepository;
  private DataFileRepository dataFileRepository;
  private Environment environment;

  public void run(String arg){

    arg = arg.trim().toLowerCase().replaceAll("-", "");
    if (!listable.contains(arg)){
      throw new DataImportException("Unknown listable option: " + arg);
    }

    switch (arg){

      case "datatype":
        List<String> dataTypes = processorRegistry.getRegisteredDataTypes();
        if (!dataTypes.isEmpty()){
          System.out.println("Registered data types:");
          for (String type: dataTypes){
            System.out.println("  " + type);
          }
        } else {
          System.out.println("No data types are currently registered!");
        }
        return;

      case "model":
        List<Class<? extends Model>> models = processorRegistry.getRegisteredModels();
        if (!models.isEmpty()){
          System.out.println("Registered models:");
          for (Class<? extends Model> model: models){
            System.out.println("  " + model.getName());
          }
        } else {
          System.out.println("No models are currently registered!");
        }
        return;

      case "dataset":
        List<DataSet> dataSets = (List<DataSet>) dataSetRepository.findAll();
        if (!dataSets.isEmpty()){
          System.out.println("Registered data sets:");
          for (DataSet dataSet: dataSets){
            System.out.println("  " + dataSet.getDisplayName());
          }
        } else {
          System.out.println("No data sets registered!");
        }
        return;

      default:
        throw new DataImportException("Unknown listable option: " + arg);

    }

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
