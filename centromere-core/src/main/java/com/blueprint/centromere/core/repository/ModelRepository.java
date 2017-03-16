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
import com.google.common.collect.Iterables;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author woemler
 * @since 0.5.0
 */
@NoRepositoryBean
public interface ModelRepository<T extends Model<ID>, ID extends Serializable>
		extends PagingAndSortingRepository<T, ID>, ModelSupport<T>,
    QueryDslPredicateExecutor<T>, QuerydslBinderCustomizer {

  default long count(Predicate predicate){
    return Iterables.size(findAll(predicate));
  }

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

  default Set<Object> distinct(String field, Predicate predicate){
    HashSet<Object> distinct = new HashSet<>();
    Sort sort = new Sort(Sort.Direction.ASC, field);
    for (T obj: findAll(predicate, sort)){
      BeanWrapper wrapper = new BeanWrapperImpl(obj);
      if (!wrapper.isReadableProperty(field)){
        throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
      }
      distinct.add(wrapper.getPropertyValue(field));
    }
    return distinct;
  }

  /**
   * Searches for all records that satisfy the requested criteria.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @return all matching {@code T} records.
   */
  default Iterable<T> find(Iterable<QueryCriteria> queryCriterias){
    return this.findAll(getPredicateFromQueryCriteria(queryCriterias));
  }

  /**
   * Searches for all records that satisfy the requested criteria, and returns them in the 
   *   requested order.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @param sort {@link Sort}
   * @return all matching {@code T} records.
   */
  default Iterable<T> find(Iterable<QueryCriteria> queryCriterias, Sort sort){
    Predicate predicate = getPredicateFromQueryCriteria(queryCriterias);
    return this.findAll(predicate, sort);
  }

  /**
   * Searches for all records that satisfy the requested criteria, and returns them as a paged
   *   collection.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @param pageable {@link Pageable}
   * @return {@link Page} containing the desired set of records. 
   */
  default Page<T> find(Iterable<QueryCriteria> queryCriterias, Pageable pageable){
    return this.findAll(getPredicateFromQueryCriteria(queryCriterias), pageable);
  }

  /**
   * Returns a count of all records that satify the requested criteria.
   *
   * @param criterias {@link QueryCriteria}
   * @return a count of {@code T} records.
   */
  default long count(Iterable<QueryCriteria> criterias){
    return Iterables.size(find(criterias));
  }


//  default Iterable<Object> distinct(String field){
//    Sort sort = new Sort(Sort.Direction.ASC, field);
//    HashSet<Object> distinct = new HashSet<>();
//    for (T obj: findAll(sort)){
//      BeanWrapper wrapper = new BeanWrapperImpl(obj);
//      if (!wrapper.isReadableProperty(field)){
//        throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
//      }
//      distinct.add(wrapper.getPropertyValue(field));
//    }
//    return distinct;
//  }

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

  default Predicate getPredicateFromQueryCriteria(Iterable<QueryCriteria> queryCriterias){
    PathBuilder<T> pathBuilder = new PathBuilder(this.getModel(), this.getModel().getSimpleName());
    List<BooleanExpression> expressions = new ArrayList<>();
    for (QueryCriteria queryCriteria: queryCriterias) {
      BooleanExpression expression = null;

      Path path = pathBuilder.get(queryCriteria.getKey());
      if (path instanceof StringPath) {
        StringPath p = (StringPath) path;
        switch (queryCriteria.getEvaluation()) {
          case STARTS_WITH:
            expression = p.startsWithIgnoreCase((String) queryCriteria.getValue());
            break;
          case ENDS_WITH:
            expression = p.endsWithIgnoreCase((String) queryCriteria.getValue());
            break;
          case LIKE:
            expression = p.likeIgnoreCase((String) queryCriteria.getValue());
            break;
          case NOT_LIKE:
            expression = p.notLike((String) queryCriteria.getValue());
            break;
          default:
            if (isMultiValue(queryCriteria.getValue())){
              //expression = p.in(getCollection(queryCriteria.getValue()));
            } else {
              expression = p.equalsIgnoreCase((String) queryCriteria.getValue());
            }
        }
      } else if (path instanceof NumberPath) {
        NumberPath p = (NumberPath) path;
        switch (queryCriteria.getEvaluation()) {
          case GREATER_THAN:
            expression = p.gt((Number) queryCriteria.getValue());
            break;
          case LESS_THAN:
            expression = p.lt((Number) queryCriteria.getValue());
            break;
          case GREATER_THAN_EQUALS:
            expression = p.goe((Number) queryCriteria.getValue());
            break;
          case LESS_THAN_EQUALS:
            expression = p.loe((Number) queryCriteria.getValue());
            break;
          case BETWEEN:
            // TODO
            //List<Number> n =  (List<Number>) getCollection(queryCriteria.getValue());
            //expression = p.between((Number) queryCriteria.getValue());
            break;
          case OUTSIDE:
            // TODO
            //expression = p.notBetween();
            break;
          default:
            expression = p.eq((Number) queryCriteria.getValue());
        }
      } else {
        SimplePath p = (SimplePath) path;
        expression = p.eq(queryCriteria.getValue());
      }
      if (expression != null) expressions.add(expression);
    }
    BooleanExpression[] predicates = new BooleanExpression[expressions.size()];
    predicates = expressions.toArray(predicates);
    return Expressions.allOf(predicates);

  }

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

  @Override
  @SuppressWarnings("unchecked")
  default void customize(QuerydslBindings bindings, EntityPath entityPath){

    // String properties
    bindings.bind(String.class).all((path, strings) -> {
      List<Object> value = new ArrayList<>(strings);
      List<String> values;
      Object o = value.get(0); // only the first occurrence of a query parameter is accepted
      if (path instanceof MapPath) {
        MapPath p = (MapPath) path;
        if (o instanceof String){
          String s = (String) o;
          String key = s.split(":")[0];
          values = Arrays.asList(s.split(":")[1].split(","));
          if (values.size() > 1){
            return p.get(key).in(values);
          } else {
            return p.get(key).eq(values.get(0));
          }
        } else {
          throw new IllegalArgumentException("Cannot determine map key from non-string parameter object.");
        }
      } else if (path instanceof ListPath){
        ListPath p = (ListPath) path;
        if (o instanceof List){
          List<Object> l = (List<Object>) o;
          return p.any().in(l);
        } else {
          return p.any().in(o);
        }
      } else {
        if (o instanceof String){
          String s = (String) o;
          StringPath p = (StringPath) path;
          values = Arrays.asList(s.split(","));
          if (values.size() > 1){
            return p.in(values);
          } else {
            String v = values.get(0);
            if (v.startsWith("*")) {
              v = "%" + v.replaceFirst("\\*", "") + "%";
              return p.like(v);
            } else if (v.startsWith("!*")){
              v = "%" + v.replaceFirst("!\\*", "") + "%";
              return p.notLike(v);
            } else if (v.startsWith("!")){
              v = v.replaceFirst("!", "");
              return p.ne(v);
            } else {
              return p.eq(v); // defaults to equality test
            }
          }
        } else {
          return ((SimplePath) path).eq(o);
        }
      }
    });

  }

}
