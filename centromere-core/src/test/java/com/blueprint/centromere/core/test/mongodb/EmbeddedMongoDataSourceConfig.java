/*
 * Copyright 2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.test.mongodb;

import com.mongodb.Mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;

import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;

/**
 * @author woemler
 */
@Configuration
@EnableMongoRepositories(basePackages = { "com.blueprint.centromere.core.commons.repositories" })
public class EmbeddedMongoDataSourceConfig {

    @Bean(destroyMethod = "close")
    public Mongo mongo() throws IOException {
        return new EmbeddedMongoBuilder().build();
    }

    @Bean
    public MongoTemplate mongoTemplate(Mongo mongo){
        return new MongoTemplate(mongo, "centromere-test");
    }

}
