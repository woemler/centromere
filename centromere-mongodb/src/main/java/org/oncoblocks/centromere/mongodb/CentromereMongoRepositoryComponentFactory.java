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

import org.oncoblocks.centromere.core.config.ModelComponentFactory;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.model.ModelAttributes;
import org.oncoblocks.centromere.core.repository.RepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

import java.io.Serializable;

/**
 * Default {@link ModelComponentFactory} for MongoDB repository implementations.  Uses Spring Data's
 *   repository factory components to create a {@link CentromereMongoRepository} instance given a
 *   model class reference and an autowired {@link MongoTemplate}.
 * 
 * @author woemler
 */
public class CentromereMongoRepositoryComponentFactory implements ModelComponentFactory<RepositoryOperations> {

	private MongoTemplate mongoTemplate;

	@Override 
	public <S extends Model<ID>, ID extends Serializable> RepositoryOperations getComponent(Class<S> model) {
		String collection = model.getSimpleName().toLowerCase();
		if (model.isAnnotationPresent(ModelAttributes.class)){
			ModelAttributes modelAttributes = model.getAnnotation(ModelAttributes.class);
			if (!modelAttributes.table().equals("")){
				collection = modelAttributes.table();
			}
		}
		TypeInformation<S> typeInfo = ClassTypeInformation.from(model);
		MongoPersistentEntity<S> entity = new BasicMongoPersistentEntity<>(typeInfo);
		MongoEntityInformation<S, ID> entityInfo = new MappingMongoEntityInformation<>(entity, collection);
		return new CentromereMongoRepository<>(entityInfo, mongoTemplate, model);
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
