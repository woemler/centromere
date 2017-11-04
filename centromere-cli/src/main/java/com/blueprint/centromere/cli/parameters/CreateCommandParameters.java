package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;

/**
 * @since 0.5.0
 * @author woemler
 */
@Data
@Parameters(commandDescription = "Create and insert new records into the data warehouse.")
public class CreateCommandParameters extends GenericCommandParameters {
  
  public static final String COMMAND = "create";

  @Parameter(names = { "-m", "--model" }, required = true, 
      description = "Model of data to be created.")
  private String model;
  
  @Parameter(names = { "-d", "--data" }, required = true, 
      description = "JSON data to be converted to target model object.")
  private String data;

  @Parameter(names = { "-h", "--help" }, description = "Display usage information.")
  private boolean help = false;
  
}
