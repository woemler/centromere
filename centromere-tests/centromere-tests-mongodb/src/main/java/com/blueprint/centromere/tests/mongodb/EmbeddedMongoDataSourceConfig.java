package com.blueprint.centromere.tests.mongodb;

import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author woemler
 */
@Configuration
public class EmbeddedMongoDataSourceConfig {

  @Bean(destroyMethod = "close")
  public MongoClient mongo() throws IOException {
    return new EmbeddedMongoBuilder().build();
  }

  @Bean
  public MongoTemplate mongoTemplate() throws IOException{
    return new MongoTemplate(mongo(), "centromere-test");
  }

}
