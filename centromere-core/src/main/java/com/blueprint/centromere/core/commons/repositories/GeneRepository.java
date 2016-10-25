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
import com.blueprint.centromere.core.repository.RepositoryOperations;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface GeneRepository<T extends Gene<ID>, ID extends Serializable> 
		extends RepositoryOperations<T, ID>, GeneOperations<T, ID> {
	List<T> findByPrimaryReferenceId(String primaryReferenceId);
	List<T> findByPrimaryGeneSymbol(String primaryGeneSymbol);
}
