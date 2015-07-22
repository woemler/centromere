/*
 * Copyright 2015 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.core.test.repository.mongo;

import org.oncoblocks.centromere.core.data.GeneRepository;
import org.oncoblocks.centromere.core.repository.GenericMongoRepository;
import org.oncoblocks.centromere.core.test.models.EntrezGene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * @author woemler
 */

@Repository
public class EntrezGeneRepository extends GenericMongoRepository<EntrezGene, Long> implements
		GeneRepository<EntrezGene> {
	@Autowired
	public EntrezGeneRepository(MongoTemplate mongoTemplate) {
		super(mongoTemplate, EntrezGene.class);
	}

	public <S extends Serializable> EntrezGene findByPrimaryGeneId(S primaryGeneId) {
		Query query = new Query(Criteria.where("entrezGeneId").is(primaryGeneId));
		return mongoOperations.findOne(query, model);
	}

	public List<EntrezGene> findByPrimaryGeneSymbol(String primaryGeneSymbol) {
		Query query = new Query(Criteria.where("primaryGeneSymbol").is(primaryGeneSymbol));
		return mongoOperations.find(query, model);
	}

	public List<EntrezGene> findByAlias(String alias) {
		Query query = new Query(Criteria.where("aliases").is(alias));
		return mongoOperations.find(query, model);
	}

	public EntrezGene guessGene(String keyword) {
		Query query = new Query(Criteria.where("primaryGeneSymbol").is(keyword));
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "entrezGeneId"));
		List<EntrezGene> genes = mongoOperations.find(query.with(sort), model);
		if (genes != null && genes.size() > 0) return genes.get(0);
		query = new Query(Criteria.where("aliases").is(keyword));
		genes = mongoOperations.find(query.with(sort), model);
		if (genes != null && genes.size() > 0) return genes.get(0);
		return null;
	}
}
