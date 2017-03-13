package com.blueprint.centromere.tests.core.config;

import com.blueprint.centromere.core.repository.MongoModelRepository;
import com.blueprint.centromere.core.repository.MongoModelRepositoryFactoryBean;
import com.mongodb.Mongo;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author woemler
 */

@Configuration
@EnableMongoRepositories(basePackages = "com.blueprint.centromere.core.commons.repositories", 
    repositoryBaseClass = MongoModelRepository.class, 
    repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
public class EmbeddedMongoConfig {

  @Bean(destroyMethod = "close")
  public Mongo mongo() throws IOException {
    return new EmbeddedMongoBuilder().build();
  }

  @Bean
  public MongoTemplate mongoTemplate(Mongo mongo){
    return new MongoTemplate(mongo, "centromere-test");
  }
  
}
