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

import org.oncoblocks.centromere.core.commons.models.DataFile;
import org.oncoblocks.centromere.core.commons.repositories.DataSetOperations;
import org.oncoblocks.centromere.mongodb.commons.models.MongoDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;

/**
 * @author woemler
 */
public class MongoDataSetRepositoryImpl implements DataSetOperations<MongoDataSet, String> {
	
	private final MongoTemplate mongoTemplate;

	@SuppressWarnings("SpringJavaAutowiredMembersInspection") 
	@Autowired
	public MongoDataSetRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override 
	public <S extends DataFile<I>, I extends Serializable> MongoDataSet findByDataFileId(
			I dataFileId) {
		return mongoTemplate.findOne(new Query(Criteria.where("dataFileIds").is(dataFileId)), 
				MongoDataSet.class);
	}
}
