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

package com.blueprint.centromere.tests.core.repositories;

import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.tests.core.models.Sample;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface SampleRepository<T extends Sample<I>, I extends Serializable>
    extends ModelRepository<T, I>, AttributeOperations<T>, GuessOperations<T> {

    List<T> findByName(String name);
    
    List<T> findBySubjectId(String subjectId);
    
    List<T> findByAliases(@Param("alias") String alias);
    
    List<T> findBySampleType(@Param("type") String sampleType);
    
    List<T> findByTissue(@Param("tissue") String tissue);
    
    List<T> findByHistology(@Param("histology") String histology);
    
    List<T> findBySpecies(@Param("species") String species);

    @Override
    default List<T> guess(@Param("keyword") String keyword) {
        List<T> samples = new ArrayList<>();
        samples.addAll(findByName(keyword));
        samples.addAll(findByAliases(keyword));
        samples.addAll(findByTissue(keyword));
        samples.addAll(findByHistology(keyword));
        samples.addAll(findBySampleType(keyword));
        return samples;
    }

    @Override
    default Optional<T> bestGuess(String keyword) {
        List<T> samples = this.findByName(keyword);
        if (!samples.isEmpty()) {
            return Optional.of(samples.get(0));
        }
        samples = this.findByAliases(keyword);
        return samples.isEmpty() ? Optional.empty() : Optional.of(samples.get(0));
    }

}
