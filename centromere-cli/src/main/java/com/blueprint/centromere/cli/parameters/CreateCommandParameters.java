package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @since 0.5.0
 * @author woemler
 */
@Data
@Parameters(commandDescription = "Create and insert new records into the data warehouse. Records " 
    + "can be supplied as a single JSON object, which represents a single record, or as multiple " 
    + "dynamic parameters, which map to a single model attribute.")
public class CreateCommandParameters {
  
  public static final String COMMAND = "create";
  public static final String HELP = "Create and insert new records into the data warehouse.";

  @Parameter(names = { "-m", "--model" }, required = true, 
      description = "Model of data to be created.")
  private String model;
  
  @Parameter(names = { "-d", "--data" }, description = "JSON data to be converted to target model object.")
  private String data;

  @Parameter(names = { "-h", "--help" }, description = "Display usage information.")
  private boolean help = false;
  
  @DynamicParameter(names = "-D", description = "Dynamic parameters, used to map to specific model fields.")
  private Map<String, String> fields = new HashMap<>();
  
}
