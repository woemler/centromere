package com.blueprint.centromere.core.dataimport.cli.arguments;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 0.5.0
 * @author woemler
 */
public class ListCommandArguments extends GenericCommandArguments {

  @Parameter(description = "Listable item")
  private List<String> args = new ArrayList<>();

  @Parameter(names = { "-d", "--details" }, description = "Boolean flag, shows more details when true")
  private boolean showDetails = false;

  public List<String> getArgs() {
    return args;
  }

  public void setArgs(List<String> args) {
    this.args = args;
  }

  public boolean getShowDetails() {
    return showDetails;
  }

  public void setShowDetails(boolean showDetails) {
    this.showDetails = showDetails;
  }

  @Override
  public String toString() {
    return "ListCommandArguments{" +
        "args=" + args +
        ", parameters=" + getParameters().toString() +
        '}';
  }
}
