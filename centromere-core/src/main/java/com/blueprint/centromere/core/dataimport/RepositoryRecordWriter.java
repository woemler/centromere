/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package com.blueprint.centromere.core.dataimport;

import com.blueprint.centromere.core.model.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of {@link RecordWriter}, that writes all records directly to the database
 *   using a {@link CrudRepository} implementation.  Can be configured to write using an
 *   insert, update, or upsert (save) operation.
 * 
 * @author woemler
 */
public class RepositoryRecordWriter<T extends Model<?>> implements RecordWriter<T> {
	
	public enum WriteMode { INSERT, UPDATE, UPSERT }
	
	private final CrudRepository<T, ?> repository;
	private Integer batchSize = 1;
	private WriteMode writeMode = WriteMode.INSERT;
	private Environment environment;
	private List<T> records = new ArrayList<>();

	public RepositoryRecordWriter(CrudRepository<T, ?> repository) {
		this.repository = repository;
	}

	public RepositoryRecordWriter(CrudRepository<T, ?> repository, Integer batchSize) {
		this.repository = repository;
        Assert.isTrue(batchSize > 0, "Batch size must be a positive integer.");
        this.batchSize = batchSize;
	}

	@Override
	public void doBefore(Object... args) throws DataImportException {
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
                repository.save(records);
                records = new ArrayList<>();
            }
        } else {
            repository.save(entity);
        }
	}

	@Override
	public void doAfter(Object... args) throws DataImportException {
			if (records.size() > 0) repository.save(records);
	}

	public CrudRepository<T, ?> getRepository() {
	return repository;
}

	@Override 
	@Autowired
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
