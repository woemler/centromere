package com.blueprint.centromere.tests.ws;

import com.blueprint.centromere.core.config.AutoConfigureCentromere;
import com.blueprint.centromere.core.config.Database;
import com.blueprint.centromere.core.config.Schema;
import com.blueprint.centromere.core.config.Security;
import com.blueprint.centromere.tests.core.config.EmbeddedMongoConfig;
import com.blueprint.centromere.tests.core.config.MongoDataSourceConfig;
import com.blueprint.centromere.ws.CentromereWebInitializer;
import com.blueprint.centromere.ws.config.WebApplicationConfig;
import com.blueprint.centromere.ws.config.WebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author woemler
 */
@SpringBootApplication
@Configuration
@Import({ MongoDataSourceConfig.class, WebApplicationConfig.class, WebSecurityConfig.class})
public class TestInitializer extends CentromereWebInitializer {

  public static void main(String[] args) {
    SpringApplication.run(TestInitializer.class);
  }

}
