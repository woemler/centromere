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
import com.blueprint.centromere.core.repository.QueryCriteria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface GeneRepository<T extends Gene<ID>, ID extends Serializable> 
    extends ModelRepository<T, ID>, MetadataOperations<T>, AttributeOperations<T> {

	Optional<T> findByGeneId(@Param("geneId") String geneId);
	List<T> findBySymbol(@Param("symbol") String symbol);
  List<T> findByReferenceId(String referenceId);
	List<T> findByAliases(@Param("alias") String alias);

	default List<T> findByExternalReference(@Param("source") String source, @Param("value") String value){
    QueryCriteria criteria = new QueryCriteria("externalReferences."+source, value);
	  return (List<T>) this.find(Collections.singleton(criteria));
	}

	@Override
	default List<T> guess(@Param("keyword") String keyword){
		List<T> genes = new ArrayList<>();
		QueryCriteria criteria = new QueryCriteria("geneId", keyword);
		genes.addAll((List<T>) find(Collections.singleton(criteria)));
    criteria = new QueryCriteria("referenceId", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
    criteria = new QueryCriteria("symbol", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
    criteria = new QueryCriteria("aliases", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
		return genes;

	}

  @Override
  default Optional<T> bestGuess(String keyword){

    List<T> genes = new ArrayList<>();

    QueryCriteria criteria = new QueryCriteria("geneId", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    criteria = new QueryCriteria("referenceId", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    criteria = new QueryCriteria("symbol", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    criteria = new QueryCriteria("aliases", keyword);
    genes.addAll((List<T>) find(Collections.singleton(criteria)));
    if (!genes.isEmpty()){
      return Optional.of(genes.get(0));
    }

    return Optional.empty();

  }

}
