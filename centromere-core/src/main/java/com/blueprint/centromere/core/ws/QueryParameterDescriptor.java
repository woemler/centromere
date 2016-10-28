/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.ws;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;

import org.springframework.core.convert.TypeDescriptor;

import java.util.regex.Pattern;

/**
 * POJO that describes a model query parameter, used when reflecting {@link Model}
 *   classes and mapping HTTP requests to {@link QueryCriteria}.
 * 
 * @author woemler
 */
public class QueryParameterDescriptor {
	
	private String paramName;
	private Path path;
	private TypeDescriptor type;
	private Ops operation;
	private boolean regexMatch = false;

	public QueryParameterDescriptor() { }

	public QueryParameterDescriptor(String paramName, Path path, TypeDescriptor type,
			Ops operation, boolean regexMatch) {
		this.paramName = paramName;
		this.path = path;
		this.type = type;
		this.operation = operation;
		this.regexMatch = regexMatch;
	}

	public QueryParameterDescriptor(String paramName, Path path, TypeDescriptor type, Ops operation) {
		this.paramName = paramName;
		this.path = path;
		this.type = type;
		this.operation = operation;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public TypeDescriptor getType() {
		return type;
	}

	public void setType(TypeDescriptor type) {
		this.type = type;
	}

	public Ops getOperation() {
		return operation;
	}

	public void setOperation(Ops operation) {
		this.operation = operation;
	}

	public boolean isRegexMatch() {
		return regexMatch;
	}

	public void setRegexMatch(boolean regexMatch) {
		this.regexMatch = regexMatch;
	}

	/**
	 * Creates a {@link Predicate} operation from a submitted value, using the parameter description.
	 *
	 * @param value
	 * @return
	 */
	public Predicate createPredicate(Object value){
		if (this.path instanceof ListPath) {
			return Expressions.predicate(this.operation, ((ListPath) this.path).any(),
					Expressions.constant(value));
		} else if (this.path instanceof MapPath){
			return Expressions.predicate(this.operation, ((MapPath)  this.path).get(""),
					Expressions.constant(value));
		} else {
			return Expressions.predicate(this.operation, this.path, Expressions.constant(value));
		}
	}

	/**
	 * Tests whether the submitted parameter name matches that described by the object.  If regex is
	 *   enabled, evaluation is performed by a regex match test.  If dynamic parameters is enabled,
	 *   the test will try to match the submitted string against all combinations of the defined 
	 *   parameter name plus valid {@link Evaluation} suffixes.
	 * 
	 * @param p submitted parameter string
	 * @return true if valid parameter
	 */
	public boolean parameterNameMatches(String p){
		if (regexMatch) {
			return Pattern.compile(paramName).matcher(p).matches();
		}
		for (String suffix: Evaluation.SUFFIX_STRINGS){
			if ((paramName + suffix).equals(p)) return true;
		}
		return this.paramName.equals(p);
		
	}

	@Override
	public String toString() {
		return "QueryParameterDescriptor{" +
				"paramName='" + paramName + '\'' +
				", path=" + path +
				", type=" + type +
				", operation=" + operation +
				", regexMatch=" + regexMatch +
				'}';
	}
}
