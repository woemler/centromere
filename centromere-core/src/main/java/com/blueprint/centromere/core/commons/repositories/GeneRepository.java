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
import com.blueprint.centromere.core.repository.BaseRepository;
import com.blueprint.centromere.core.repository.MetadataOperations;
import com.blueprint.centromere.core.repository.RepositoryOperations;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.PathBuilder;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface GeneRepository extends
		BaseRepository<Gene, Long>,
		MetadataOperations<Gene, Long>,
		AttributeOperations<Gene> {

	List<Gene> findByPrimaryRefereneId(@Param("refId") String refId);
	List<Gene> findByPrimaryGeneSymbol(@Param("symbol") String symbol);
	List<Gene> findByAliases(@Param("alias") String alias);

	default List<Gene> findByExternalReference(@Param("source") String source, @Param("value") String value){
		PathBuilder<Gene> pathBuilder = new PathBuilder<>(Gene.class, "gene");
		MapPath<String, String, PathBuilder<String>> mapPath
				= pathBuilder.getMap("externalReferences", String.class, String.class);
		Expression<String> constant = Expressions.constant(value);
		Predicate predicate = Expressions.predicate(Ops.EQ_IGNORE_CASE, mapPath.get(source), constant);
		return (List<Gene>) this.findAll(predicate);
	}

	@Override
	default Iterable<Gene> guess(String keyword){
		List<Gene> genes = new ArrayList<>();
		genes.addAll(findByPrimaryRefereneId(keyword));
		genes.addAll(findByPrimaryGeneSymbol(keyword));
		genes.addAll(findByAliases(keyword));
		return genes;
	}

}
