package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @since 0.5.0
 * @author woemler
 */
@Data
@Parameters(commandDescription = "Deletes one or more records from the data warehouse.")
public class DeleteCommandParameters extends GenericCommandParameters {
  
  public static final String COMMAND = "delete";
  public static final String HELP = "Deletes one or more records from the data warehouse.";

  @Parameter(description = "Deletable items")
  private List<String> args = new ArrayList<>();

}
