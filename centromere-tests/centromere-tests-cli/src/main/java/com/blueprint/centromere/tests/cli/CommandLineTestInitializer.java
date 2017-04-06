package com.blueprint.centromere.tests.cli;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.core.config.AutoConfigureCentromere;
import com.blueprint.centromere.core.config.Schema;
import com.blueprint.centromere.tests.core.MongoDataSourceConfig;
import org.springframework.context.annotation.Import;

/**
 * @author woemler
 */
@Import({MongoDataSourceConfig.class})
@AutoConfigureCentromere
public class CommandLineTestInitializer extends CentromereCommandLineInitializer {

  public static void main(String[] args) {
    CentromereCommandLineInitializer.run(CommandLineTestInitializer.class, args);
  }
}
