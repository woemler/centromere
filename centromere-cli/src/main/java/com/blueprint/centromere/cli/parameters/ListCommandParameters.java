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
@Parameters(commandDescription = "Lists out available records or properties of the data warehouse.")
public class ListCommandParameters extends GenericCommandParameters {
  
  public static final String COMMAND = "list";
  public static final String HELP = "Lists out available records or properties of the data warehouse.";

  @Parameter(description = "Listable item")
  private List<String> args = new ArrayList<>();

  @Parameter(names = { "-d", "--details" }, description = "Boolean flag, shows more details when true")
  private boolean showDetails = false;
  
}
