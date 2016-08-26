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

import org.oncoblocks.centromere.core.commons.models.Sample;
import org.oncoblocks.centromere.core.commons.repositories.SubjectOperations;
import org.oncoblocks.centromere.mongodb.commons.models.MongoSubject;
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
public class MongoSubjectRepositoryImpl implements SubjectOperations<MongoSubject, String> {
	
	private final MongoTemplate mongoTemplate;
	
	@SuppressWarnings("SpringJavaAutowiredMembersInspection") 
	@Autowired
	public MongoSubjectRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override 
	public List<MongoSubject> guessSubject(String keyword) {
		List<MongoSubject> samples = new ArrayList<>();
		Query query = new Query(Criteria.where("name").is(keyword));
		samples.addAll(mongoTemplate.find(query, MongoSubject.class));
		query = new Query(Criteria.where("aliases").is(keyword));
		samples.addAll(mongoTemplate.find(query, MongoSubject.class));
		return samples;
	}

	@Override
	public <S extends Sample<I>, I extends Serializable> MongoSubject findBySampleId(I sampleId) {
		return mongoTemplate.findOne(new Query(Criteria.where("sampleIds").is(sampleId)), MongoSubject.class);
	}

	@Override
	public List<MongoSubject> findByAttribute(String name, Object value) {
		return mongoTemplate.find(new Query(Criteria.where("attributes."+name).is(value)), MongoSubject.class);
	}

	@Override public List<MongoSubject> findRecordsWithAttribute(String name) {
		return mongoTemplate.find(new Query(Criteria.where("attributes."+name).exists(true)), MongoSubject.class);
	}

	@Override public List<MongoSubject> findByAlias(String alias) {
		return mongoTemplate.find(new Query(Criteria.where("aliases").is(alias)), MongoSubject.class);
	}
}
