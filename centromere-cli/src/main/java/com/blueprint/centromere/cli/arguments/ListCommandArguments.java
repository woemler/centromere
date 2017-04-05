package com.blueprint.centromere.cli.arguments;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 0.5.0
 * @author woemler
 */
public class ListCommandArguments extends GenericCommandArguments {

  @Parameter(description = "Listable item", required = true)
  private List<String> args = new ArrayList<>();

  public List<String> getArgs() {
    return args;
  }

  public void setArgs(List<String> args) {
    this.args = args;
  }

  @Override
  public String toString() {
    return "ListCommandArguments{" +
        "args=" + args +
        ", parameters=" + getParameters().toString() +
        '}';
  }
}
