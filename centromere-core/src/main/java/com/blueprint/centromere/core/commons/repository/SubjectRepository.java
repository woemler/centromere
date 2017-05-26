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

import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.ModelResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
@ModelResource("subjects")
public interface SubjectRepository
		extends ModelRepository<Subject, String>,
		MetadataOperations<Subject>,
 		AttributeOperations<Subject> {
	
  Optional<Subject> findByName(@Param("name") String name);
	List<Subject> findBySpecies(@Param("species") String species);
  @Query("{ 'sampleIds': ?0 }") Optional<Subject> findBySampleId(String sampleId);
  @Query("{ 'aliases': ?0 }") List<Subject> findByAlias(String alias);
	
	@Override
	default List<Subject> guess(@Param("keyword") String keyword){
		List<Subject> subjects = new ArrayList<>();
		Optional<Subject> optional = findByName(keyword);
    optional.ifPresent(subjects::add);
		subjects.addAll(findByAlias(keyword));
		return subjects;
	}

  @Override
  default Optional<Subject> bestGuess(String keyword){
    Optional<Subject> optional = findByName(keyword);
    if (optional.isPresent()) return optional;
    List<Subject> subjects = findByAlias(keyword);
    if (!subjects.isEmpty()){
      return Optional.of(subjects.get(0));
    }
    return Optional.empty();
  }
	
}
