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

package com.blueprint.centromere.mongodb;

import java.io.Serializable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * @author woemler
 */
public class MongoModelRepositoryFactoryBean<R extends MongoRepository<T, ID>, T, ID extends Serializable> 
    extends MongoRepositoryFactoryBean<R, T, ID> {

  public MongoModelRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
    super(repositoryInterface);
  }

  @Override
  protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
    return new MongoModelRepositoryFactory<>(operations);
  }

  private static class MongoModelRepositoryFactory<T, ID extends Serializable>
      extends MongoRepositoryFactory {

    private final MongoOperations mongoOperations;

    public MongoModelRepositoryFactory(MongoOperations mongoOperations) {
      super(mongoOperations);
      this.mongoOperations = mongoOperations;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
      MongoEntityInformation entityInformation = getEntityInformation(information.getDomainType());
      return new MongoModelRepository<>(entityInformation, mongoOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
      return MongoModelRepository.class;
    }
  }

}
