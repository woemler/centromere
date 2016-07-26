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

package org.oncoblocks.centromere.mongodb.commons;

import org.oncoblocks.centromere.core.commons.EntrezGeneOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class MongoEntrezGeneRepositoryImpl implements
		EntrezGeneOperations<MongoEntrezGene, String> {
	
	private MongoOperations mongoOperations;

	@Autowired
	public MongoEntrezGeneRepositoryImpl(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<MongoEntrezGene> guessGene(String keyword) {
		List<MongoEntrezGene> genes = new ArrayList<>();
		try {
			Double entrezGeneId = Double.parseDouble(keyword);
			genes.addAll(mongoOperations.find(new Query(Criteria.where("entrezGeneId").is(entrezGeneId)), 
					MongoEntrezGene.class));
		} catch (NumberFormatException e){
			// pass
		}
		genes.addAll(mongoOperations.find(new Query(Criteria.where("primaryGeneSymbol").is(keyword)), 
				MongoEntrezGene.class));
		genes.addAll(mongoOperations.find(new Query(Criteria.where("geneSymbolAliases").is(keyword)),
				MongoEntrezGene.class));
		return genes;
	}
}
