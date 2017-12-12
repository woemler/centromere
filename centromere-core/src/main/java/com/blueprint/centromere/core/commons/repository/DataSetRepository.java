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

import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelResource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author woemler
 */
@ModelResource("datasets")
public interface DataSetRepository extends ModelRepository<DataSet, String> {
	Optional<DataSet> findByDataSetId(String dataSetId); 
	Optional<DataSet> findByName(String name);
	List<DataSet> findBySource(String source);
	@Query("{ 'sampleIds': ?0 }")  List<DataSet> findBySampleId(String sampleId);
  @Query("{ 'dataFileIds': ?0 }")  List<DataSet> findByDataFileId(String dataFileId);
}
