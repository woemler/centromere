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
import org.springframework.data.mongodb.repository.support.QueryDslMongoRepository;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 * @author woemler
 * @since 0.5.0
 */
public class MongoModelRepository<T extends Model<ID>, ID extends Serializable> 
    extends QueryDslMongoRepository<T, ID>
    implements ModelRepository<T, ID> {

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
