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

import org.oncoblocks.centromere.core.commons.models.DataSet;
import org.oncoblocks.centromere.core.commons.repositories.DataFileOperations;
import org.oncoblocks.centromere.mongodb.commons.models.MongoDataFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.List;

/**
 * @author woemler
 */
public class MongoDataFileRepositoryImpl implements DataFileOperations<MongoDataFile, String> {
	
	private final MongoTemplate mongoTemplate;

	@SuppressWarnings("SpringJavaAutowiredMembersInspection") 
	@Autowired
	public MongoDataFileRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public <S extends DataSet<I>, I extends Serializable> List<MongoDataFile> findByDataSetId(I dataSetId) {
		return mongoTemplate.find(new Query(Criteria.where("dataSetId").is(dataSetId)), MongoDataFile.class);
	}
}
