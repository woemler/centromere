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

import com.blueprint.centromere.core.commons.repositories.GeneOperations;
import org.oncoblocks.centromere.mongodb.commons.models.MongoGene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class MongoGeneRepositoryImpl implements
		GeneOperations<MongoGene, String> {
	
	private MongoOperations mongoOperations;

	@Autowired
	public MongoGeneRepositoryImpl(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<MongoGene> guessGene(String keyword) {
		List<MongoGene> genes = new ArrayList<>();
		try {
			Double entrezGeneId = Double.parseDouble(keyword);
			genes.addAll(mongoOperations.find(new Query(Criteria.where("primaryReferenceId").is(entrezGeneId)), 
					MongoGene.class));
		} catch (NumberFormatException e){
			// pass
		}
		genes.addAll(mongoOperations.find(new Query(Criteria.where("primaryGeneSymbol").is(keyword)), 
				MongoGene.class));
		genes.addAll(mongoOperations.find(new Query(Criteria.where("aliases").is(keyword)),
				MongoGene.class));
		return genes;
	}

	public List<MongoGene> findByAlias(String alias) {
		return null;
	}

	public List<MongoGene> findByReferenceId(String referenceId) {
		return null;
	}
}
