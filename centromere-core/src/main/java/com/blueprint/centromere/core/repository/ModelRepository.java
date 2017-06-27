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

package com.blueprint.centromere.core.repository;

import com.blueprint.centromere.core.exceptions.QueryParameterException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author woemler
 * @since 0.5.0
 */
@NoRepositoryBean
public interface ModelRepository<T extends Model<ID>, ID extends Serializable>
		extends PagingAndSortingRepository<T, ID>, ModelSupport<T> {
  
  Logger logger = LoggerFactory.getLogger(ModelRepository.class);

  /**
   * Searches for all records that satisfy the requested criteria.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @return all matching {@code T} records.
   */
  Iterable<T> find(Iterable<QueryCriteria> queryCriterias);

  /**
   * Searches for all records that satisfy the requested criteria, and returns them in the
   *   requested order.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @param sort {@link Sort}
   * @return all matching {@code T} records.
   */
  Iterable<T> find(Iterable<QueryCriteria> queryCriterias, Sort sort);

  /**
   * Searches for all records that satisfy the requested criteria, and returns them as a paged
   *   collection.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @param pageable {@link Pageable}
   * @return {@link Page} containing the desired set of records.
   */
  Page<T> find(Iterable<QueryCriteria> queryCriterias, Pageable pageable);

  /**
   * Returns a count of all records that satify the requested criteria.
   *
   * @param criterias {@link QueryCriteria}
   * @return a count of {@code T} records.
   */
  long count(Iterable<QueryCriteria> criterias);

  /**
   * Returns a unsorted list of distinct values of the requested field.
   *
   * @param field Model field name.
   * @return Sorted list of distinct values of {@code field}.
   */
  default Set<Object> distinct(String field){
    Sort sort = new Sort(Sort.Direction.ASC, field);
    HashSet<Object> distinct = new HashSet<>();
    for (T obj: findAll(sort)){
      BeanWrapper wrapper = new BeanWrapperImpl(obj);
      if (!wrapper.isReadableProperty(field)){
        throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
      }
      distinct.add(wrapper.getPropertyValue(field));
    }
    return distinct;
  }

  /**
   * Returns a unsorted list of distinct values of the requested field, filtered using a {@link QueryCriteria}
   *   based query.
   *
   * @param field Model field name.
   * @param criterias Query criteria to filter the field values by.
   * @return Sorted list of distinct values of {@code field}.
   */
  default Set<Object> distinct(String field, Iterable<QueryCriteria> criterias){
    HashSet<Object> distinct = new HashSet<>();
    Sort sort = new Sort(Sort.Direction.ASC, field);
    for (T obj: find(criterias, sort)){
      BeanWrapper wrapper = new BeanWrapperImpl(obj);
      if (!wrapper.isReadableProperty(field)){
        throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
      }
      distinct.add(wrapper.getPropertyValue(field));
    }
    return distinct;
  }

	/* Create records */

  /**
   * Creates a new record in the repository and returns the updated model object.
   *
   * @param entity instance of {@code T} to be persisted.
   * @return updated instance of the entity.
   */
  <S extends T> S insert(S entity);

  /**
   * Creates multiple new records and returns their updated representations.
   *
   * @param entities collection of records to be persisted.
   * @return updated instances of the entity objects.
   */
  <S extends T> Iterable<S> insert(Iterable<S> entities);

	/* Update records */

  /**
   * Updates an existing record in the repository and returns its instance.
   *
   * @param entity updated record to be persisted in the repository.
   * @return the updated entity object.
   */
  <S extends T> S update(S entity);

  /**
   * Updates multiple records and returns their instances.
   *
   * @param entities collection of records to update.
   * @return updated instances of the entity objects.
   */
  <S extends T> Iterable<S> update(Iterable<S> entities);
  
  static List<Object> getCollection(Object val){
    if (val instanceof Collection){
      return new ArrayList<Object>((Collection) val);
    } else if (val.getClass().isArray()){
      return Arrays.asList(val);
    } else {
      return Collections.singletonList(val);
    }
  }

  static boolean isMultiValue(Object val){
    return val.getClass().isArray() || val instanceof Collection;
  }

}
