/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.etl.writer;

import com.blueprint.centromere.core.exceptions.DataProcessingException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Simple implementation of {@link RecordWriter}, that writes all records directly to the database
 *   using a {@link ModelRepository} instance.  Can be configured to write using an
 *   insert, update, or upsert (save) operation.
 * 
 * @author woemler
 */
public class RepositoryRecordWriter<T extends Model<ID>, ID extends Serializable>
    implements RecordWriter<T> {
	
	public enum WriteMode { INSERT, UPDATE, UPSERT }
	
	private final ModelRepository<T, ID> repository;
	
	private Integer batchSize = 1;
	private WriteMode writeMode = WriteMode.INSERT;
	private List<T> records = new ArrayList<>();

  public RepositoryRecordWriter(ModelRepository<T, ID> repository) {
    this.repository = repository;
  }

  public RepositoryRecordWriter(ModelRepository<T, ID> repository, WriteMode writeMode) {
    this.repository = repository;
    this.writeMode = writeMode;
  }
  
  public RepositoryRecordWriter(ModelRepository<T, ID> repository, WriteMode writeMode, Integer batchSize) {
    this.repository = repository;
    this.writeMode = writeMode;
    this.batchSize = batchSize;
  }

  @Override
	public void doBefore(File file, Map<String, String> args) throws DataProcessingException {
		records = new ArrayList<>();
	}

	/**
	 * Writes the input {@link Model} record to the target Repository implementation,
	 *   using the appropriate operation.
	 * @param entity
	 */ 
	@SuppressWarnings("unchecked")
	public void writeRecord(T entity) throws DataProcessingException {
    if (batchSize > 1){
      records.add(entity);
      if (records.size() >= batchSize) {
        writeRecords(records);
        records = new ArrayList<>();
      }
    } else {
      writeRecords(Collections.singleton(entity));
    }
	}
	
	protected void writeRecords(Collection<T> records) throws DataProcessingException {
	  if (writeMode.equals(WriteMode.INSERT)){
	    repository.insert(records);
    } else if (writeMode.equals(WriteMode.UPDATE)){
	    repository.update(records);
    } else {
      for (T record: records){
        if (record.getId() != null && repository.existsById(record.getId())){
          repository.update(record);
        } else {
          repository.insert(record);
        }
      }
    }
  }

	@Override
	public void doOnSuccess(File file, Map<String, String> args) throws DataProcessingException {
			if (records.size() > 0) writeRecords(records);
	}

  @Override
  public void doOnFailure(File file, Map<String, String> args) throws DataProcessingException {
    
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

  public void setWriteMode(WriteMode writeMode) {
    this.writeMode = writeMode;
  }
}
