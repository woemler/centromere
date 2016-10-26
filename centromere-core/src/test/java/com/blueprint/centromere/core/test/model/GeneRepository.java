/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.test.model;

import com.blueprint.centromere.core.repository.BaseRepository;
import com.blueprint.centromere.core.repository.MetadataOperations;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public interface GeneRepository extends BaseRepository<Gene, Long>, MetadataOperations<Gene, Long> {
	
	List<Gene> findByPrimaryGeneSymbol(@Param("symbol") String symbol);
	Gene findOneByEntrezGeneId(@Param("entrezGeneId") Long entrezGeneId);
	List<Gene> findByAliases(@Param("alias") String alias);

	@Override 
	default Iterable<Gene> guess(String keyword){
		List<Gene> genes = new ArrayList<>();
		genes.addAll(findByPrimaryGeneSymbol(keyword));
		genes.addAll(findByAliases(keyword));
		try {
			Long id = Long.parseLong(keyword);
			genes.add(findOneByEntrezGeneId(id));
		} catch (NumberFormatException e){
			// pass
		}
		return genes;
	}
}
