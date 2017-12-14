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

import com.blueprint.centromere.core.commons.model.Term;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.QueryCriteria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author woemler
 */
public interface TermRepository<T extends Term<ID>, ID extends Serializable> 
    extends ModelRepository<T, ID> {
  
  List<T> findByTerm(String term);
  
  List<T> findByModel(String model);

  default List<T> findByModel(Class<?> model){
    return findByModel(model.getName());
  }
  
  List<T> findByField(String field);
  
  List<T> findByModelAndField(String model, String field);

  default List<T> findByModelAndField(Class<?> model, String field){
    return findByModelAndField(model.getName(), field);
  }
  
  Optional<T> findByModelAndFieldAndTerm(String model, String field, String term);

  default Optional<T> findByModelAndFieldAndTerm(Class<?> model, String field, String term){
    return findByModelAndFieldAndTerm(model.getName(), field, term);
  }
  
  default List<T> guess(String keyword, String model, String field){
    List<QueryCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(new QueryCriteria("term", keyword, Evaluation.LIKE));
    if (model != null){
      criteriaList.add(new QueryCriteria("model", model));
    }
    if (field != null){
      criteriaList.add(new QueryCriteria("field", field));
    }
    return (List<T>) this.find(criteriaList);
  }
  
  default List<T> guess(String keyword, Class<?> model, String field){
    return guess(keyword, model.getName(), field);
  }

  default List<T> guess(String keyword){
    return guess(keyword, (String) null, null);
  }
  
  default List<T> guess(String keyword, Class<?> model){
    return guess(keyword, model, null);
  }

  default List<T> guess(String keyword, String model){
    return guess(keyword, model, null);
  }

  /**
   * Checks to see if a {@link Term} record identical to the submitted one exists, merging the two 
   *   records if so, and inserting a new record if not.
   * 
   * @param term record to be saved
   */
  default void saveTerm(T term){
    Optional<T> optional = findByModelAndFieldAndTerm(term.getModel(), term.getField(), term.getTerm());
    if (optional.isPresent()){
      T t = optional.get();
      t.addReferenceIds(term.getReferenceIds());
      this.update(t);
    } else {
      this.insert(term);
    }
  }

  /**
   * Performs a save on multiple {@link Term} records.
   * 
   * @param terms
   */
  default void saveTerms(Collection<T> terms){
    for (T term: terms){
      saveTerm(term);
    }
  }

}
