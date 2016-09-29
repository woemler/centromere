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
	private boolean dynaimicParameters = true;

	public QueryParameterDescriptor() { }

	public QueryParameterDescriptor(String paramName, String fieldName, Class<?> type,
			Evaluation evaluation, boolean regexMatch, boolean dynamicParameters) {
		this.paramName = paramName;
		this.fieldName = fieldName;
		this.type = type;
		this.evaluation = evaluation;
		this.regexMatch = regexMatch;
		this.dynaimicParameters = dynamicParameters;
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

	public boolean isDynaimicParameters() {
		return dynaimicParameters;
	}

	public void setDynaimicParameters(boolean dynaimicParameters) {
		this.dynaimicParameters = dynaimicParameters;
	}

	public QueryCriteria createQueryCriteria(Object value){
		return new QueryCriteria(fieldName, value, evaluation);
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
		} else if (dynaimicParameters){
			for (String suffix: Evaluation.SUFFIX_STRINGS){
				if ((paramName + suffix).equals(p)) return true;
			}
		} 
		return this.paramName.equals(p);
		
	}

	/**
	 * Given an input parameter name, determines what the name of the field to be queried in the 
	 *   database layer is.  If regex is enabled, the supplied parameter name will be returned, as it 
	 *   is expected to have matched against the predetermined regex pattern.  Otherwise, the actual
	 *   field name is returned, if available.
	 * 
	 * @param p submitted parameter string
	 * @return field name corresponding to database field
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

	/**
	 * Determines which {@link Evaluation} value should be returned.  If dynamic parameters are not 
	 *   enabled or the submitted parameter name matches the default, the default evaluation value
	 *   is returned.  Otherwise, the submitted parameter string is matched to the appropriate 
	 *   evaluation suffix to determine which should be returned.  If no match is made, an 
	 *   {@link QueryParameterException} will be thrown.
	 * 
	 * @param p
	 * @return
	 * @throws QueryParameterException
	 */
	public Evaluation getDynamicEvaluation(String p) throws QueryParameterException{
		if (regexMatch || !dynaimicParameters) return evaluation; // dynamic parameters is not enabled
		if (paramName.equals(p)) return evaluation; // submitted parameter is default
		Evaluation eval = null;
		for (String suffix: Evaluation.SUFFIX_STRINGS){
			if (parameterNameMatches(p)){
				eval = Evaluation.fromSuffix(suffix);
			}
		}
		if (eval != null){
			return eval;
		} else {
			throw new QueryParameterException(String.format("Not a valid dynamic parameter for defined " 
					+ "parameter %s: %s", paramName, p));
		}
	}

	@Override 
	public String toString() {
		return "QueryParameterDescriptor{" +
				"paramName='" + paramName + '\'' +
				", fieldName='" + fieldName + '\'' +
				", type=" + type +
				", evaluation=" + evaluation +
				", regexMatch=" + regexMatch +
				", dynaimicParameters=" + dynaimicParameters +
				'}';
	}
}
