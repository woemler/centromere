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

package org.oncoblocks.centromere.mongodb.test.config;

import com.mongodb.Mongo;
import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;
import org.oncoblocks.centromere.mongodb.CentromereMongoRepository;
import org.oncoblocks.centromere.mongodb.CentromereMongoRepositoryFactoryBean;
import org.oncoblocks.centromere.mongodb.test.EntrezGene;
import org.oncoblocks.centromere.mongodb.test.basic.EntrezGeneRepository;
import org.oncoblocks.centromere.mongodb.test.custom.CustomEntrezGeneRepository;
import org.oncoblocks.centromere.mongodb.test.custom.CustomEntrezGeneRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;

import java.io.IOException;

/**
 * @author woemler
 */

public class CustomCentromereMongoConfig {
	
	@Configuration
	@EnableMongoRepositories(basePackageClasses = { EntrezGeneRepository.class },
			repositoryFactoryBeanClass = CentromereMongoRepositoryFactoryBean.class)
	public static class DatabaseOneConfig {

		@Bean(destroyMethod = "close", name = "mongoOne")
		public Mongo mongo() throws IOException {
			return new EmbeddedMongoBuilder().build();
		}

		@Bean(name = "mongoTemplateOne")
		public MongoTemplate mongoTemplate() throws IOException{
			return new MongoTemplate(mongo(), "db-one");
		}
		
	}

	@Configuration
	@EnableMongoRepositories(basePackageClasses = { EntrezGeneRepository.class },
			repositoryFactoryBeanClass = CentromereMongoRepositoryFactoryBean.class)
	public static class DatabaseTwoConfig {

		@Bean(destroyMethod = "close", name = "mongoTwo")
		public Mongo mongo() throws IOException {
			return new EmbeddedMongoBuilder().build();
		}

		@Bean(name = "mongoTemplateTwo")
		public MongoTemplate mongoTemplate() throws IOException{
			return new MongoTemplate(mongo(), "db-two");
		}

	}
	
	@Configuration
	@ComponentScan(basePackages = { "org.oncoblocks.centromere.mongodb.test.custom" })
	public static class RepositoryConfig{
		
		@Bean(name = "geneRepositoryOne")
		@Autowired
		public CustomEntrezGeneRepository customGeneRepositoryOne(
				@Qualifier("mongoTemplateOne") MongoTemplate mongoTemplate){
			MongoRepositoryFactoryBean<CustomEntrezGeneRepository, EntrezGene, Long> factoryBean 
					= new MongoRepositoryFactoryBean<>();
			factoryBean.setRepositoryInterface(CustomEntrezGeneRepository.class);
			factoryBean.setMongoOperations(mongoTemplate);
			factoryBean.setCustomImplementation(new CustomEntrezGeneRepositoryImpl(mongoTemplate));
			factoryBean.setRepositoryBaseClass(CentromereMongoRepository.class);
			factoryBean.afterPropertiesSet();
			return factoryBean.getObject();
		}

		@Bean(name = "geneRepositoryTwo")
		@Autowired
		public CustomEntrezGeneRepository customGeneRepositoryTwo(
				@Qualifier("mongoTemplateTwo") MongoTemplate mongoTemplate){
			MongoRepositoryFactoryBean<CustomEntrezGeneRepository, EntrezGene, Long> factoryBean
					= new MongoRepositoryFactoryBean<>();
			factoryBean.setRepositoryInterface(CustomEntrezGeneRepository.class);
			factoryBean.setMongoOperations(mongoTemplate);
			factoryBean.setCustomImplementation(new CustomEntrezGeneRepositoryImpl(mongoTemplate));
			factoryBean.setRepositoryBaseClass(CentromereMongoRepository.class);
			factoryBean.afterPropertiesSet();
			return factoryBean.getObject();
		}
		
	}
	
}
