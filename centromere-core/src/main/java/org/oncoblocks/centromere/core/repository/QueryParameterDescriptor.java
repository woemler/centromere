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

package org.oncoblocks.centromere.core.repository;

import java.util.regex.Pattern;

/**
 * POJO that describes a model query parameter, used when reflecting {@link org.oncoblocks.centromere.core.model.Model}
 *   classes and mapping HTTP requests to {@link QueryCriteria}.
 * 
 * @author woemler
 */
public class QueryParameterDescriptor {
	
	private String paramName;
	private String fieldName;
	private Class<?> type;
	private Evaluation evaluation;
	private boolean regexMatch = false;

	public QueryParameterDescriptor() { }

	public QueryParameterDescriptor(String paramName, String fieldName, Class<?> type,
			Evaluation evaluation, boolean regexMatch) {
		this.paramName = paramName;
		this.fieldName = fieldName;
		this.type = type;
		this.evaluation = evaluation;
		this.regexMatch = regexMatch;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public boolean isRegexMatch() {
		return regexMatch;
	}

	public void setRegexMatch(boolean regexMatch) {
		this.regexMatch = regexMatch;
	}

	public QueryCriteria createQueryCriteria(Object value){
		return new QueryCriteria(fieldName, value, evaluation);
	}

	/**
	 * Tests whether the submitted parameter name matches that described by the object.  If regex is
	 *   enabled, evalutation is performed by a regex match test.
	 * 
	 * @param p
	 * @return
	 */
	public boolean parameterNameMatches(String p){
		if (regexMatch){
			return Pattern.compile(paramName).matcher(p).matches();
		} else {
			return this.paramName.equals(p);
		}
	}

	/**
	 * Given an input parameter name, determines what the name of the field to be queried in the 
	 *   database layer is.  If regex is enabled, the supplied parameter name will be returned, as it 
	 *   is expected to have matched against the predetermined regex pattern.  Otherwise, the actual
	 *   field name is returned, if available.
	 * 
	 * @param p
	 * @return
	 */
	public String getQueryableFieldName(String p){
		if (regexMatch){
			return p;
		} else if (fieldName != null){
			return fieldName;
		} else {
			return paramName;
		}
	}

	@Override public String toString() {
		return "QueryParameterDescriptor{" +
				"paramName='" + paramName + '\'' +
				", fieldName='" + fieldName + '\'' +
				", type=" + type +
				", evaluation=" + evaluation +
				'}';
	}
}
