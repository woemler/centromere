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

package com.blueprint.centromere.core.ws.controller.query;

import com.querydsl.core.types.Path;

/**
 * @author woemler
 */
public class QueryPath {
	
	private String fieldName;
	private Path path;
	private Class<?> type;
	private Class<?> model;

	public QueryPath() {
	}

	public QueryPath(String fieldName, Path path, Class<?> type, Class<?> model) {
		this.fieldName = fieldName;
		this.path = path;
		this.type = type;
		this.model = model;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getModel() {
		return model;
	}

	public void setModel(Class<?> model) {
		this.model = model;
	}
}
