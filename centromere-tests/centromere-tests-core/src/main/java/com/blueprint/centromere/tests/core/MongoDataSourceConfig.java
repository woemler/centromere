package com.blueprint.centromere.tests.core;

import com.blueprint.centromere.core.repository.MongoModelRepository;
import com.blueprint.centromere.core.repository.MongoModelRepositoryFactoryBean;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author woemler
 */
@EnableMongoRepositories(basePackages = "com.blueprint.centromere.core.commons.repository",
    repositoryBaseClass = MongoModelRepository.class,
    repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
@PropertySource({ "classpath:data-source.properties" })
@Configuration
public class MongoDataSourceConfig extends AbstractMongoConfiguration {

  @Autowired
  private Environment env;

  @Override
  public String getDatabaseName(){
    return env.getRequiredProperty("mongo.name");
  }

  @Override
  @Bean
  public Mongo mongo() throws Exception {
    ServerAddress serverAddress = new ServerAddress(env.getRequiredProperty("mongo.host"));
    List<MongoCredential> credentials = new ArrayList<>();
    credentials.add(MongoCredential.createScramSha1Credential(
        env.getRequiredProperty("mongo.username"),
        env.getRequiredProperty("mongo.name"),
        env.getRequiredProperty("mongo.password").toCharArray()
    ));
    MongoClientOptions options = new MongoClientOptions.Builder().build();
    return new MongoClient(serverAddress, credentials, options);
  }

}
