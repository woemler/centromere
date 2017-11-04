package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.Parameter;
import lombok.Data;

/**
 * @author woemler
 */
@Data
public class BaseParameters {
  
  @Parameter(names = { "-h", "--help" }, help = true)
  private boolean help;

}
