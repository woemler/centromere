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

package com.blueprint.centromere.core.commons.repositories;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import com.blueprint.centromere.core.repository.MongoOperationsAware;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author woemler
 * @since 0.5.0
 */
@SuppressWarnings("unchecked")
public interface DataOperations<T extends Model<?>> extends MongoOperationsAware, ModelSupport<T> {

	default List<T> findByDataFileId(String dataFileId){
		return getMongoOperations().find(new Query(Criteria.where("dataFileId").is(dataFileId)), this.getModel());
	}

	default List<T> findBySampleId(String sampleId){
		return getMongoOperations().find(new Query(Criteria.where("sampleId").is(sampleId)), this.getModel());
	}

	default List<T> findByGeneId(String geneId){
		return getMongoOperations().find(new Query(Criteria.where("geneId").is(geneId)), this.getModel());
	}

	default List<T> findByDataSetId(String dataSetId){
		return getMongoOperations().find(new Query(Criteria.where("dataSetId").is(dataSetId)), this.getModel());
	}
	
}
