/*
 * Copyright 2017 the original author or authors
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

package com.blueprint.centromere.core.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * @author woemler
 */
public class DataSourceConfiguration {

  public static class DefaultMongoDataSourceConfiguration extends AbstractMongoConfiguration {

    private Environment env;

    @Override
    public String getDatabaseName(){
      return env.getRequiredProperty("centromere.db.name");
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
      ServerAddress serverAddress = new ServerAddress(env.getRequiredProperty("centromere.db.host"));
      List<MongoCredential> credentials = new ArrayList<>();
      credentials.add(MongoCredential.createScramSha1Credential(
          env.getRequiredProperty("centromere.db.username"),
          env.getRequiredProperty("centromere.db.name"),
          env.getRequiredProperty("centromere.db.password").toCharArray()
      ));
      MongoClientOptions options = new MongoClientOptions.Builder().build();
      return new MongoClient(serverAddress, credentials, options);
    }

    @Autowired
    public void setEnv(Environment env) {
      this.env = env;
    }
  }


}
