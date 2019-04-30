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

package com.blueprint.centromere.core.repository;

import com.blueprint.centromere.core.exceptions.QueryParameterException;
import java.util.regex.Pattern;

/**
 * POJO that describes a model query parameter, used when reflecting {@link
 * com.blueprint.centromere.core.model.Model} classes and mapping HTTP requests to {@link
 * QueryCriteria}.
 *
 * @author woemler
 * @since 0.4.2
 */
public class QueryParameterDescriptor {

    private String paramName;
    private String fieldName;
    private Class<?> type;
    private Evaluation evaluation;
    private boolean regexMatch;
    private boolean dynaimicParameters = true;

    public QueryParameterDescriptor() {
    }

    /**
     * Constructs a parameter descriptor with all available arguments.
     *
     * @param paramName name of the parameter
     * @param fieldName name of the model field
     * @param type the field's type
     * @param evaluation evaluation to apply when using the parameter name
     * @param regexMatch whether regular expression should be applied to match the field
     * @param dynamicParameters dynamic param matching
     */
    public QueryParameterDescriptor(String paramName, String fieldName, Class<?> type,
        Evaluation evaluation, boolean regexMatch, boolean dynamicParameters) {
        this.paramName = paramName;
        this.fieldName = fieldName;
        this.type = type;
        this.evaluation = evaluation;
        this.regexMatch = regexMatch;
        this.dynaimicParameters = dynamicParameters;
    }

    /**
     * Constructs a parameter descriptor with default arguments.
     *
     * @param paramName name of the parameter
     * @param fieldName name of the model field
     * @param type the field's type
     * @param evaluation evaluation to apply when using the parameter name
     */
    public QueryParameterDescriptor(String paramName, String fieldName, Class<?> type,
        Evaluation evaluation) {
        this.paramName = paramName;
        this.fieldName = fieldName;
        this.type = type;
        this.evaluation = evaluation;
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

    /**
     * Tests whether the submitted parameter name matches that described by the object.  If regex is
     * enabled, evaluation is performed by a regex match test.  If dynamic options is enabled, the
     * test will try to match the submitted string against all combinations of the defined parameter
     * name plus valid {@link Evaluation} suffixes.
     *
     * @param p submitted parameter string
     * @return true if valid parameter
     */
    public boolean parameterNameMatches(String p) {
        if (regexMatch) {
            return Pattern.compile(paramName).matcher(p).matches();
        } else if (dynaimicParameters) {
            for (String suffix : Evaluation.SUFFIX_STRINGS) {
                if ((paramName + suffix).equals(p)) {
                    return true;
                }
            }
        }
        return this.paramName.equals(p);

    }

    /**
     * Given an input parameter name, determines what the name of the field to be queried in the
     * database layer is.  If regex is enabled, the supplied parameter name will be returned, as it
     * is expected to have matched against the predetermined regex pattern.  Otherwise, the actual
     * field name is returned, if available.
     *
     * @param p submitted parameter string
     * @return field name corresponding to database field
     */
    public String getQueryableFieldName(String p) {
        if (regexMatch) {
            return p;
        } else if (fieldName != null) {
            return fieldName;
        } else {
            return paramName;
        }
    }

    /**
     * Determines which {@link Evaluation} value should be returned.  If dynamic options are not
     * enabled or the submitted parameter name matches the default, the default evaluation value is
     * returned.  Otherwise, the submitted parameter string is matched to the appropriate evaluation
     * suffix to determine which should be returned.  If no match is made, an {@link
     * QueryParameterException} will be thrown.
     *
     * @param parameterName parameter name
     * @return evaluation to be applied.
     * @throws QueryParameterException thrown if no match is made.
     */
    public Evaluation getDynamicEvaluation(String parameterName) throws QueryParameterException {
        if (regexMatch || !dynaimicParameters) {
            return evaluation; // dynamic options is not enabled
        }
        if (paramName.equals(parameterName)) {
            return evaluation; // submitted parameter is default
        }
        Evaluation eval = null;
        if (parameterNameMatches(parameterName)) {
            for (String suffix : Evaluation.SUFFIX_STRINGS) {
                if ((paramName + suffix).equals(parameterName)) {
                    eval = Evaluation.fromSuffix(suffix);
                }
            }
        }
        if (eval != null) {
            return eval;
        } else {
            throw new QueryParameterException(
                String.format("Not a valid dynamic parameter for defined "
                    + "parameter %s: %s", paramName, parameterName));
        }
    }

    /**
     * Returns a {@link QueryCriteria} with the default {@link Evaluation} for the supplied value.
     *
     * @param value value to use for filtering
     * @return new query criteria
     */
    public QueryCriteria createQueryCriteria(Object value) {
        return new QueryCriteria(fieldName, value, evaluation);
    }

    /**
     * Returns a {@link QueryCriteria} object with an {@link Evaluation} value determined by the
     * supplied parameter name.
     *
     * @param parameterName parameter name
     * @param value value to check
     * @return new query criteria
     */
    public QueryCriteria createQueryCriteria(String parameterName, Object value) {
        return new QueryCriteria(fieldName, value, getDynamicEvaluation(parameterName));
    }

    @Override
    public String toString() {
        return "QueryParameterDescriptor{"
            + "paramName='" + paramName + '\''
            + ", fieldName='" + fieldName + '\''
            + ", type=" + type
            + ", evaluation=" + evaluation
            + ", regexMatch=" + regexMatch
            + ", dynaimicParameters=" + dynaimicParameters
            + '}';
    }

}
