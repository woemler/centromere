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

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author woemler
 */
@RepositoryRestResource(path = "genes", collectionResourceRel = "genes")
public interface GeneRepository extends
		ModelRepository<Gene, String>,
		MetadataOperations<Gene>,
		AttributeOperations<Gene> {

	List<Gene> findByPrimaryReferenceId(@Param("refId") String refId);
	List<Gene> findByPrimaryGeneSymbol(@Param("symbol") String symbol);
	List<Gene> findByAliases(@Param("alias") String alias);

	default List<Gene> findByExternalReference(@Param("source") String source, @Param("value") String value){
    Query query = new Query(Criteria.where("externalReferences." + source).is(value));
    return getMongoOperations().find(query, getModel());
	}

	@Override
	default List<Gene> guess(@Param("keyword") String keyword){

		List<Gene> genes = new ArrayList<>();
		
		Query query = new Query(Criteria.where("primaryReferenceId").is(keyword));
		genes.addAll(getMongoOperations().find(query, getModel()));

    query = new Query(Criteria.where("primaryGeneSymbol").is(keyword));
    genes.addAll(getMongoOperations().find(query, getModel()));

    query = new Query(Criteria.where("aliases").is(keyword));
    genes.addAll(getMongoOperations().find(query, getModel()));

		return genes;

	}

}
