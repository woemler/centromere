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

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.model.ModelRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

/**
 * @author woemler
 */
@RepositoryRestResource(path = "datafiles", collectionResourceRel = "dataFiles")
public interface DataFileRepository extends ModelRepository<DataFile, String> {
	List<DataFile> findByFilePath(String filePath);
	List<DataFile> findByDataType(String dataType);
	List<DataFile> findByDataSetId(UUID dataSetId);
}
