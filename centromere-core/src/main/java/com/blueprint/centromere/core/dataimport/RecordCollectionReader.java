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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple {@link RecordReader} implementation that iterates through a collection if {@link Model}
 *   records, rather than reading from a data source.
 * 
 * @author woemler
 */
public class RecordCollectionReader<T extends Model<?>> implements RecordReader<T> {
	
	private final List<T> records = new ArrayList<>();
	private Environment environment;
	private Class<T> model;

	public RecordCollectionReader(List<T> records) {
		this.records.addAll(records);
	}

	public T readRecord() throws DataImportException {
		if (!records.isEmpty()) return records.remove(0);
		return null;
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

	@Override
	public Class<T> getModel() {
			return model;
	}

	@Override
	public void setModel(Class<T> model) {
			this.model = model;
	}
}
