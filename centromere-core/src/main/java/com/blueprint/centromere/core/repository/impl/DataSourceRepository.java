/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.core.repository.impl;

import com.blueprint.centromere.core.model.impl.DataSource;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelResource;
import java.util.List;
import java.util.Optional;

/**
 * @author woemler
 */
@ModelResource("datasource")
public interface DataSourceRepository extends ModelRepository<DataSource, String> {
	Optional<DataSource> findByDataSourceId(String dataSourceId);
  Optional<DataSource> findBySource(String source);
  List<DataSource> findBySourceType(String sourceType);
  List<DataSource> findByDataType(String dataType);
	List<DataSource> findByDataSetId(String dataSetId);
	List<DataSource> findByModel(String model);
}
