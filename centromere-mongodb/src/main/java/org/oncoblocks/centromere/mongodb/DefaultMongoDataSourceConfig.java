/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.mongodb;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple, pre-defined MongoDB data source connection configuration, for use with auto-configured
 *   setups.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class DefaultMongoDataSourceConfig {
	
	@Configuration
	public static class BasicMongoDb3Config extends AbstractMongoConfiguration {
		
		@Autowired private Environment env;

		@Override
		public String getDatabaseName(){
			return env.getRequiredProperty("mongo.meta.name");
		}

		@Override
		public Mongo mongo() throws Exception {
			ServerAddress serverAddress = new ServerAddress(env.getRequiredProperty("centromere.db.host"));
			List<MongoCredential> credentials = new ArrayList<>();
			credentials.add(MongoCredential.createScramSha1Credential(
					env.getRequiredProperty("centromere.db.user"),
					env.getRequiredProperty("centromere.db.name"),
					env.getRequiredProperty("centromere.db.password").toCharArray()
			));
			MongoClientOptions options = new MongoClientOptions.Builder().build();
			return new MongoClient(serverAddress, credentials, options);
		}

		@Override
		public MongoTemplate mongoTemplate() throws Exception {
			return new MongoTemplate(mongo(), env.getRequiredProperty("centromere.db.name"));
		}
		
	}
	
}
