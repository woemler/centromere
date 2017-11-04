package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;

/**
 * @since 0.5.0
 * @author woemler
 */
@Data
@Parameters(commandDescription = "Updates existing data warehouse records, replacing existing model " 
    + "attributes with those supplied by user input.")
public class UpdateCommandParameters extends GenericCommandParameters {
  
  public static final String COMMAND = "update";

  @Parameter(names = { "-m", "--model" }, required = true, 
      description = "Model of data to be created.")
  private String model;
  
  @Parameter(names = { "-d", "--data" }, required = true, 
      description = "JSON data to be converted to target model object.")
  private String data;
  
  @Parameter(names = { "-i", "--id" }, required = true, 
      description = "Primary key identifier used to look up the existing record in the data warehouse.")
  private String id;

  @Parameter(names = { "-r", "--replace" }, description = "Rather than attempting to merge the " 
      + "supplied record with the existing model record, attempts to replace the record entirely.")
  private boolean replace = false;
  
  @Parameter(names = { "-h", "--help" }, description = "Display usage information.")
  private boolean help = false;
  
}
