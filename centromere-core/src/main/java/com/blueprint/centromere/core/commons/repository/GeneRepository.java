/*
 * Copyright 2017 the original author or authors
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

package com.blueprint.centromere.core.commons.repository;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelResource;
import com.blueprint.centromere.core.repository.QueryCriteria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
@ModelResource("genes")
public interface GeneRepository extends
		ModelRepository<Gene, String>,
		MetadataOperations<Gene>,
		AttributeOperations<Gene> {

	Optional<Gene> findByPrimaryReferenceId(@Param("refId") String refId);

	List<Gene> findByPrimaryGeneSymbol(@Param("symbol") String symbol);

	List<Gene> findByAliases(@Param("alias") String alias);

	default List<Gene> findByExternalReference(@Param("source") String source, @Param("value") String value){
    QueryCriteria criteria = new QueryCriteria("externalReferences."+source, value);
	  return (List<Gene>) this.find(Collections.singleton(criteria));
	}

	@Override
	default List<Gene> guess(@Param("keyword") String keyword){
		List<Gene> genes = new ArrayList<>();
		QueryCriteria criteria = new QueryCriteria("primaryReferenceId", keyword);
		genes.addAll((List<Gene>) find(Collections.singleton(criteria)));
    criteria = new QueryCriteria("primaryGeneSymbol", keyword);
    genes.addAll((List<Gene>) find(Collections.singleton(criteria)));
    criteria = new QueryCriteria("aliases", keyword);
    genes.addAll((List<Gene>) find(Collections.singleton(criteria)));
		return genes;

	}

  @Override
  default Optional<Gene> bestGuess(String keyword){

    List<Gene> genes = new ArrayList<>();

    QueryCriteria criteria = new QueryCriteria("primaryReferenceId", keyword);
    genes.addAll((List<Gene>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    criteria = new QueryCriteria("primaryGeneSymbol", keyword);
    genes.addAll((List<Gene>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    criteria = new QueryCriteria("aliases", keyword);
    genes.addAll((List<Gene>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    return Optional.empty();

  }

}
