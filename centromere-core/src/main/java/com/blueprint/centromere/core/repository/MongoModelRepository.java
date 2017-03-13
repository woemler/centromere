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

import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 * @author woemler
 * @since 0.5.0
 */
public class MongoModelRepository<T extends Model<ID>, ID extends Serializable> 
    extends SimpleMongoRepository<T, ID>
    implements ModelRepository<T, ID>, MongoOperationsAware {

  private final MongoOperations mongoOperations;
  private final MongoEntityInformation<T, ID> metadata;
  private final Class<T> model;

  public MongoModelRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
    super(metadata, mongoOperations);
    this.mongoOperations = mongoOperations;
    this.metadata = metadata;
    this.model = metadata.getJavaType();
  }

  /**
   * Searches for all records that satisfy the requested criteria.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @return all matching {@code T} records.
   */
  @Override
  public Iterable<T> find(Iterable<QueryCriteria> queryCriterias) {
    Criteria criteria = getQueryFromQueryCriteria(queryCriterias);
    Query query = new Query();
    if (criteria != null){
      query.addCriteria(criteria);
    }
    return mongoOperations.find(query, model);
  }

  /**
   * Searches for all records that satisfy the requested criteria, and returns them in the
   * requested order.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @param sort {@link Sort}
   * @return all matching {@code T} records.
   */
  @Override
  public Iterable<T> find(Iterable<QueryCriteria> queryCriterias, Sort sort) {
    Criteria criteria = getQueryFromQueryCriteria(queryCriterias);
    Query query = new Query();
    if (criteria != null){
      query.addCriteria(criteria);
    }
    return mongoOperations.find(query.with(sort), model);
  }

  /**
   * Searches for all records that satisfy the requested criteria, and returns them as a paged
   * collection.
   *
   * @param queryCriterias {@link QueryCriteria}
   * @param pageable {@link Pageable}
   * @return {@link Page} containing the desired set of records.
   */
  @Override
  public Page<T> find(Iterable<QueryCriteria> queryCriterias, Pageable pageable) {
    Criteria criteria = getQueryFromQueryCriteria(queryCriterias);
    Query query = new Query();
    if (criteria != null){
      query.addCriteria(criteria);
    }
    List<T> entities = mongoOperations.find(query.with(pageable), model);
    long count = count(queryCriterias);
    return new PageImpl<>(entities, pageable, count);
  }

  /**
   * Updates an existing record in the repository and returns its instance.
   *
   * @param entity updated record to be persisted in the repository.
   * @return the updated entity object.
   */
  @Override
  public <S extends T> S update(S entity) {
    if (exists(entity.getId())){
      mongoOperations.save(entity);
      return entity;
    } else {
      throw new ModelPersistenceException(String.format("Model record does not exist in the database," 
          + " and cannot be updated: %s", entity.toString()));  
    }
  }

  /**
   * Updates multiple records and returns their instances.
   *
   * @param entities collection of records to update.
   * @return updated instances of the entity objects.
   */
  @Override
  public <S extends T> Iterable<S> update(Iterable<S> entities) {
    for (S entity: entities){
      this.update(entity);
    }
    return entities;
  }

  @Override
  public MongoOperations getMongoOperations() {
    return mongoOperations;
  }

  protected Criteria getQueryFromQueryCriteria(Iterable<QueryCriteria> queryCriterias){
    List<Criteria> criteriaList = new ArrayList<>();
    for (QueryCriteria queryCriteria: queryCriterias){
      Criteria criteria = null;
      if (queryCriteria != null) {
        switch (queryCriteria.getEvaluation()) {
          case EQUALS:
            criteria = new Criteria(queryCriteria.getKey()).is(queryCriteria.getValue());
            break;
          case NOT_EQUALS:
            criteria = new Criteria(queryCriteria.getKey()).not().is(queryCriteria.getValue());
            break;
          case IN:
            criteria = new Criteria(queryCriteria.getKey()).in((Collection) queryCriteria.getValue());
            break;
          case NOT_IN:
            criteria = new Criteria(queryCriteria.getKey()).nin((Collection) queryCriteria.getValue());
            break;
          case IS_NULL:
            criteria = new Criteria(queryCriteria.getKey()).is(null);
            break;
          case NOT_NULL:
            criteria = new Criteria(queryCriteria.getKey()).not().is(null);
            break;
          case GREATER_THAN:
            criteria = new Criteria(queryCriteria.getKey()).gt(queryCriteria.getValue());
            break;
          case GREATER_THAN_EQUALS:
            criteria = new Criteria(queryCriteria.getKey()).gte(queryCriteria.getValue());
            break;
          case LESS_THAN:
            criteria = new Criteria(queryCriteria.getKey()).lt(queryCriteria.getValue());
            break;
          case LESS_THAN_EQUALS:
            criteria = new Criteria(queryCriteria.getKey()).lte(queryCriteria.getValue());
            break;
          case BETWEEN:
            criteria = new Criteria().andOperator(
                Criteria.where(queryCriteria.getKey()).gt(((List) queryCriteria.getValue()).get(0)),
                Criteria.where(queryCriteria.getKey()).lt(((List) queryCriteria.getValue()).get(1)));
            break;
          case OUTSIDE:
            criteria = new Criteria().orOperator(
                Criteria.where(queryCriteria.getKey()).lt(((List) queryCriteria.getValue()).get(0)),
                Criteria.where(queryCriteria.getKey()).gt(((List) queryCriteria.getValue()).get(1)));
            break;
          case BETWEEN_INCLUSIVE:
            criteria = new Criteria().andOperator(
                Criteria.where(queryCriteria.getKey()).gte(((List) queryCriteria.getValue()).get(0)),
                Criteria.where(queryCriteria.getKey()).lte(((List) queryCriteria.getValue()).get(1)));
            break;
          case OUTSIDE_INCLUSIVE:
            criteria = new Criteria().orOperator(
                Criteria.where(queryCriteria.getKey()).lte(((List) queryCriteria.getValue()).get(0)),
                Criteria.where(queryCriteria.getKey()).gte(((List) queryCriteria.getValue()).get(1)));
            break;
          case LIKE:
            criteria = new Criteria(queryCriteria.getKey()).regex((String) queryCriteria.getValue());
            break;
          case NOT_LIKE:
            // TODO
            break;
          case STARTS_WITH:
            criteria = new Criteria(queryCriteria.getKey()).regex("^" + queryCriteria.getValue());
            break;
          case ENDS_WITH:
            criteria = new Criteria(queryCriteria.getKey()).regex(queryCriteria.getValue() + "$");
            break;
          default:
            criteria = new Criteria(queryCriteria.getKey()).is(queryCriteria.getValue());
        }
        criteriaList.add(criteria);
      }
    }
    return criteriaList.size() > 0 ?
        new Criteria().andOperator(criteriaList.toArray(new Criteria[]{})) : null;
  }

  /**
   * Returns the model class reference.
   */
  @Override
  public Class<T> getModel() {
    return model;
  }

  /**
   * Assigns the given model to target object.
   * TODO: Do we still need to support this method?
   */
  @Override
  public void setModel(Class<T> model) {
    
  }
}
