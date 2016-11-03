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

import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.repository.BaseRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
@RepositoryRestResource(path = "subjects", collectionResourceRel = "subjects")
public interface SubjectRepository
		extends BaseRepository<Subject, Long>,
		MetadataOperations<Subject, Long>,
 		AttributeOperations<Subject> {
	
	List<Subject> findByName(@Param("name") String name);
	List<Subject> findBySpecies(@Param("species") String species);
	//Subject findOneBySamples(@Param("sampleId") Long sampleId);
	
	@Override
	default List<Subject> guess(@Param("keyword") String keyword){
		List<Subject> subjects = new ArrayList<>();
		subjects.addAll(findByName(keyword));
		return subjects;
	}
	
}
