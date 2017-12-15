package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;

/**
 * @since 0.5.0
 * @author woemler
 */
@Data
@Parameters(commandDescription = "Deletes one or more records from the data warehouse.")
public class DeleteCommandParameters {
  
  public static final String COMMAND = "delete";
  public static final String HELP = "Deletes one or more records from the data warehouse.";

  @Parameter(names = { "-m", "--model" }, required = true,
      description = "Model of data to be created.")
  private String model;

  @Parameter(names = { "-i", "--id" }, required = true,
      description = "Primary key identifier used to look up the existing record in the data warehouse.")
  private String id;

  @Parameter(names = { "-h", "--help" }, description = "Display usage information.")
  private boolean help = false;

}
