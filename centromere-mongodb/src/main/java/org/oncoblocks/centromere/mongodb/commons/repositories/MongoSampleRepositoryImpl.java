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

package org.oncoblocks.centromere.mongodb.commons.repositories;

import org.oncoblocks.centromere.core.commons.models.Subject;
import org.oncoblocks.centromere.core.commons.repositories.SampleOperations;
import org.oncoblocks.centromere.mongodb.commons.models.MongoSample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class MongoSampleRepositoryImpl implements SampleOperations<MongoSample, String> {
	
	private final MongoTemplate mongoTemplate;

	@SuppressWarnings("SpringJavaAutowiredMembersInspection")
	@Autowired
	public MongoSampleRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override 
	public List<MongoSample> guessSample(String keyword) {
		List<MongoSample> samples = new ArrayList<>();
		Query query = new Query(Criteria.where("name").is(keyword));
		samples.addAll(mongoTemplate.find(query, MongoSample.class));
		query = new Query(Criteria.where("aliases").is(keyword));
		samples.addAll(mongoTemplate.find(query, MongoSample.class));
		return samples;
	}

	@Override 
	public <S extends Subject<I>, I extends Serializable> List<MongoSample> findBySubjectId(I subjectId) {
		return mongoTemplate.find(new Query(Criteria.where("subjectId").is(subjectId)), MongoSample.class);
	}

	@Override 
	public List<MongoSample> findByAttribute(String name, Object value) {
		return mongoTemplate.find(new Query(Criteria.where("attributes."+name).is(value)), MongoSample.class);
	}

	@Override public List<MongoSample> findRecordsWithAttribute(String name) {
		return mongoTemplate.find(new Query(Criteria.where("attributes."+name).exists(true)), MongoSample.class);
	}

	@Override public List<MongoSample> findByAlias(String alias) {
		return mongoTemplate.find(new Query(Criteria.where("aliases").is(alias)), MongoSample.class);
	}
}
