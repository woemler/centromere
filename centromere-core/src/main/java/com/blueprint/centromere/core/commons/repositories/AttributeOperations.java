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
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
public interface AttributeOperations<T extends Model<?>> 
    extends MongoOperationsAware, ModelSupport<T> {

	@SuppressWarnings("unchecked")
	default List<T> findWithAttribute(@Param("name") String name){
    Query query = new Query(Criteria.where("attributes." + name).exists(true));
		return this.getMongoOperations().find(query, this.getModel());
	}

	@SuppressWarnings("unchecked")
	default List<T> findByAttribute(@Param("name") String name, @Param("value") String value){
    Query query = new Query(Criteria.where("attributes." + name).is(value));
    return this.getMongoOperations().find(query, this.getModel());
	}

}
