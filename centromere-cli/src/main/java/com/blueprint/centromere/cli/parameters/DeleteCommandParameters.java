package com.blueprint.centromere.cli.parameters;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @since 0.5.0
 * @author woemler
 */
@Data
public class DeleteCommandParameters extends GenericCommandParameters {

  @Parameter(description = "Deletable items")
  private List<String> args = new ArrayList<>();

}
