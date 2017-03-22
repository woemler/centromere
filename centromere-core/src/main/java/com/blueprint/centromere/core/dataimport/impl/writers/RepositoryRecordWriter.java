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

package com.blueprint.centromere.core.dataimport.impl.writers;

import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.RecordWriter;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;

/**
 * Simple implementation of {@link RecordWriter}, that writes all records directly to the database
 *   using a {@link CrudRepository} implementation.  Can be configured to write using an
 *   insert, update, or upsert (save) operation.
 * 
 * @author woemler
 */
public class RepositoryRecordWriter<T extends Model<ID>, ID extends Serializable> implements RecordWriter<T> {
	
	public enum WriteMode { INSERT, UPDATE, UPSERT }
	
	private ModelRepository<T, ID> repository;
	private Integer batchSize = 1;
	private WriteMode writeMode = WriteMode.INSERT;
	private Environment environment;
	private List<T> records = new ArrayList<>();

  /**
   * Empty default implementation.  The purpose of extending {@link org.springframework.beans.factory.InitializingBean}
   * is to trigger bean post-processing by a {@link BeanPostProcessor}.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(repository, "Repository must not be null");
  }

  @Override
	public void doBefore(Object... args) throws DataImportException {
		try {
		  afterPropertiesSet();
    } catch (Exception e){
		  throw new DataImportException(e.getMessage());
    }
    records = new ArrayList<>();
	}

	/**
	 * Writes the input {@link Model} record to the target Repository implementation,
	 *   using the appropriate operation.
	 * @param entity
	 */ 
	@SuppressWarnings("unchecked")
	public void writeRecord(T entity) throws DataImportException {
    if (batchSize > 1){
      records.add(entity);
      if (records.size() >= batchSize) {
        writeRecords(records);
        records = new ArrayList<>();
      }
    } else {
      writeRecords(records);
    }
	}
	
	protected void writeRecords(Collection<T> records){
	  if (writeMode.equals(WriteMode.INSERT)){
	    repository.insert(records);
    } else if (writeMode.equals(WriteMode.UPDATE)){
	    repository.update(records);
    } else {
      for (T record: records){
        if (repository.exists(record.getId())){
          repository.update(record);
        } else {
          repository.insert(record);
        }
      }
    }
  }

	@Override
	public void doAfter(Object... args) throws DataImportException {
			if (records.size() > 0) repository.save(records);
	}

	public ModelRepository<T, ID> getRepository() {
	return repository;
}

  public void setRepository(ModelRepository<T, ID> repository) {
    this.repository = repository;
  }

  @Override 
	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Environment getEnvironment() {
		return environment;
	}

  public Integer getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Integer batchSize) {
    this.batchSize = batchSize;
  }

  public WriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(
      WriteMode writeMode) {
    this.writeMode = writeMode;
  }
}
