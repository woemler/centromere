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

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author woemler
 */
@RepositoryRestResource(path = "datafiles", collectionResourceRel = "dataFiles")
public interface DataFileRepository extends ModelRepository<DataFile, String> {
	Optional<DataFile> findByFilePath(@Param("path") String filePath );
  DataFile findOneByFilePath(String filePath);
	List<DataFile> findByDataType(String dataType);
	List<DataFile> findByDataSetId(String dataSetId);
	List<DataFile> findByModel(Class<? extends Model<?>> model);
}
