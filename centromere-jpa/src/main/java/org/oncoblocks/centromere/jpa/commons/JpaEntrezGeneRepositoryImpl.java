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

package org.oncoblocks.centromere.jpa.commons;

import org.oncoblocks.centromere.core.commons.EntrezGeneOperations;
import org.oncoblocks.centromere.jpa.JpaQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class JpaEntrezGeneRepositoryImpl implements EntrezGeneOperations<JpaEntrezGene, Long> {
	
	private final EntityManager entityManager;
	private final JpaQueryBuilder<JpaEntrezGene> queryBuilder;
	private static final Logger logger = LoggerFactory.getLogger(JpaEntrezGeneRepositoryImpl.class);
	
	@Autowired
	public JpaEntrezGeneRepositoryImpl(EntityManager entityManager){
		this.entityManager = entityManager;
		this.queryBuilder = new JpaQueryBuilder<>(entityManager);
	}

	@Override 
	public List<JpaEntrezGene> guessGene(String keyword) {
		
		List<JpaEntrezGene> genes = new ArrayList<>();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JpaEntrezGene> query = builder.createQuery(JpaEntrezGene.class);
		Root<JpaEntrezGene> root = query.from(JpaEntrezGene.class);
		query.select(root);
		List<JpaEntrezGene> result;
		
		try {
			Long entrezGeneId = Long.parseLong(keyword);
			query.where(builder.equal(root.get("entrezGeneId"), entrezGeneId));
			result = entityManager.createQuery(query).getResultList();
			logger.warn(String.format("Found %d w/ entrezGeneId %d", result.size(), entrezGeneId));
			genes.addAll(result);
		} catch (NumberFormatException e){
			// pass
		}
		
		query = builder.createQuery(JpaEntrezGene.class);
		root = query.from(JpaEntrezGene.class);
		query.select(root);
		query.where(builder.equal(root.get("primaryGeneSymbol"), keyword));
		result = entityManager.createQuery(query).getResultList();
		logger.warn(String.format("Found %d w/ primaryGeneSymbol %s", result.size(), keyword));
		genes.addAll(result);
		
		query = builder.createQuery(JpaEntrezGene.class);
		root = query.from(JpaEntrezGene.class);
		
		Path join = root.join("jpaEntrezGeneSymbolAliases");
		query.select(root);
		query.where(builder.equal(join.get("symbol"), keyword));
		result = entityManager.createQuery(query).getResultList(); // TODO: this still does not work properly
		logger.warn(String.format("Found %d w/ alias %s", result.size(), keyword));
		genes.addAll(result);
		return genes;
	}
}
