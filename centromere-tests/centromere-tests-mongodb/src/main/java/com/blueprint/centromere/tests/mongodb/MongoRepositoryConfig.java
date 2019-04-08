package com.blueprint.centromere.tests.mongodb;

import com.blueprint.centromere.core.repository.DefaultModelRepositoryRegistry;
import com.blueprint.centromere.core.repository.ModelRepositoryRegistry;
import com.blueprint.centromere.mongodb.MongoModelRepository;
import com.blueprint.centromere.mongodb.MongoModelRepositoryFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author woemler
 */
@Configuration
@EnableMongoRepositories(
    basePackages = {"com.blueprint.centromere.tests.mongodb.repositories"},
    repositoryBaseClass = MongoModelRepository.class,
    repositoryFactoryBeanClass = MongoModelRepositoryFactoryBean.class)
public class MongoRepositoryConfig {

    @Bean
    public ModelRepositoryRegistry modelRepositoryRegistry(ApplicationContext context) {
        return new DefaultModelRepositoryRegistry(context);
    }

}
