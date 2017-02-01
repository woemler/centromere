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

import java.util.Arrays;
import java.util.List;

/**
 * @author woemler
 */
public enum Evaluation {
	EQUALS,
	IN,
	NOT_EQUALS,
	NOT_IN,
	LIKE,
	NOT_LIKE,
	STARTS_WITH,
	ENDS_WITH,
	GREATER_THAN,
	LESS_THAN,
	GREATER_THAN_EQUALS,
	LESS_THAN_EQUALS,
	BETWEEN,
	BETWEEN_INCLUSIVE,
	OUTSIDE,
	OUTSIDE_INCLUSIVE,
	IS_NULL,
	NOT_NULL,
	IS_TRUE,
	IS_FALSE;

	public static final String EQUALS_SUFFIX = "Equals";
	public static final String IN_SUFFIX = "In";
	public static final String NOT_EQUALS_SUFFIX = "NotEquals";
	public static final String NOT_IN_SUFFIX = "NotIn";
	public static final String LIKE_SUFFIX = "Like";
	public static final String NOT_LIKE_SUFFIX = "NotLike";
	public static final String STARTS_WITH_SUFFIX = "StartsWith";
	public static final String ENDS_WITH_SUFFIX = "EndsWith";
	public static final String GREATER_THAN_SUFFIX = "GreaterThan";
	public static final String LESS_THAN_SUFFIX = "LessThan";
	public static final String GREATER_THAN_EQUALS_SUFFIX = "GreaterThanOrEquals";
	public static final String LESS_THAN_EQUALS_SUFFIX = "LessThanOrEquals";
	public static final String BETWEEN_SUFFIX = "Between";
	public static final String BETWEEN_INCLUSIVE_SUFFIX = "BetweenIncluding";
	public static final String OUTSIDE_SUFFIX = "Outside";
	public static final String OUTSIDE_INCLUSIVE_SUFFIX = "OutsideIncluding";
	public static final String IS_NULL_SUFFIX = "IsNull";
	public static final String NOT_NULL_SUFFIX = "IsNotNull";
	public static final String IS_TRUE_SUFFIX = "IsTrue";
	public static final String IS_FALSE_SUFFIX = "IsFalse";

	public static final List<String> SUFFIX_STRINGS = Arrays.asList(
			EQUALS_SUFFIX, IN_SUFFIX, NOT_EQUALS_SUFFIX, NOT_IN_SUFFIX, LIKE_SUFFIX, NOT_LIKE_SUFFIX,
			STARTS_WITH_SUFFIX, ENDS_WITH_SUFFIX, GREATER_THAN_EQUALS_SUFFIX, GREATER_THAN_SUFFIX,
			LESS_THAN_SUFFIX, LESS_THAN_EQUALS_SUFFIX, BETWEEN_INCLUSIVE_SUFFIX, BETWEEN_SUFFIX,
			OUTSIDE_SUFFIX, OUTSIDE_INCLUSIVE_SUFFIX, IS_NULL_SUFFIX, IS_TRUE_SUFFIX, NOT_NULL_SUFFIX,
			IS_FALSE_SUFFIX
	);

	public static Evaluation fromSuffix(String suffix){
		if (EQUALS_SUFFIX.equals(suffix)){
			return EQUALS;
		} else if (IN_SUFFIX.equals(suffix)){
			return IN;
		} else if (NOT_EQUALS_SUFFIX.equals(suffix)){
			return NOT_EQUALS;
		} else if (NOT_IN_SUFFIX.equals(suffix)){
			return NOT_IN;
		} else if (LIKE_SUFFIX.equals(suffix)){
			return LIKE;
		} else if (NOT_LIKE_SUFFIX.equals(suffix)){
			return NOT_LIKE;
		} else if (STARTS_WITH_SUFFIX.equals(suffix)){
			return STARTS_WITH;
		} else if (ENDS_WITH_SUFFIX.equals(suffix)){
			return ENDS_WITH;
		} else if (GREATER_THAN_SUFFIX.equals(suffix)){
			return GREATER_THAN;
		} else if (LESS_THAN_SUFFIX.equals(suffix)){
			return LESS_THAN;
		} else if (GREATER_THAN_EQUALS_SUFFIX.equals(suffix)){
			return GREATER_THAN_EQUALS;
		} else if (LESS_THAN_EQUALS_SUFFIX.equals(suffix)){
			return LESS_THAN_EQUALS;
		} else if (BETWEEN_SUFFIX.equals(suffix)){
			return BETWEEN;
		} else if (BETWEEN_INCLUSIVE_SUFFIX.equals(suffix)){
			return BETWEEN_INCLUSIVE;
		} else if (OUTSIDE_SUFFIX.equals(suffix)){
			return OUTSIDE;
		} else if (OUTSIDE_INCLUSIVE_SUFFIX.equals(suffix)){
			return OUTSIDE_INCLUSIVE;
		} else if (IS_NULL_SUFFIX.equals(suffix)){
			return IS_NULL;
		} else if (NOT_NULL_SUFFIX.equals(suffix)){
			return NOT_NULL;
		} else if (IS_TRUE_SUFFIX.equals(suffix)){
			return IS_TRUE;
		} else if (IS_FALSE_SUFFIX.equals(suffix)){
			return IS_FALSE;
		} else {
			return null;
		}
	}

	public static Evaluation guessEvaluation(String name){
		if (name.endsWith(EQUALS_SUFFIX)){
			return EQUALS;
		} else if (name.endsWith(NOT_IN_SUFFIX)){
			return NOT_IN;
		} else if (name.endsWith(IN_SUFFIX)){
			return IN;
		} else if (name.endsWith(NOT_LIKE_SUFFIX)){
			return NOT_LIKE;
		} else if (name.endsWith(LIKE_SUFFIX)){
			return LIKE;
		} else if (name.endsWith(STARTS_WITH_SUFFIX)){
			return STARTS_WITH;
		} else if (name.endsWith(ENDS_WITH_SUFFIX)){
			return ENDS_WITH;
		} else if (name.endsWith(GREATER_THAN_EQUALS_SUFFIX)){
			return GREATER_THAN_EQUALS;
		} else if (name.endsWith(GREATER_THAN_SUFFIX)){
			return GREATER_THAN;
		} else if (name.endsWith(LESS_THAN_EQUALS_SUFFIX)){
			return LESS_THAN_EQUALS;
		} else if (name.endsWith(LESS_THAN_SUFFIX)){
			return LESS_THAN;
		} else if (name.endsWith(BETWEEN_SUFFIX)){
			return BETWEEN;
		} else if (name.endsWith(BETWEEN_INCLUSIVE_SUFFIX)){
			return BETWEEN_INCLUSIVE;
		} else if (name.endsWith(OUTSIDE_SUFFIX)){
			return OUTSIDE;
		} else if (name.endsWith(OUTSIDE_INCLUSIVE_SUFFIX)){
			return OUTSIDE_INCLUSIVE;
		} else if (name.endsWith(IS_NULL_SUFFIX)){
			return IS_NULL;
		} else if (name.endsWith(NOT_NULL_SUFFIX)){
			return NOT_NULL;
		} else if (name.endsWith(IS_TRUE_SUFFIX)){
			return IS_TRUE;
		} else if (name.endsWith(IS_FALSE_SUFFIX)){
			return IS_FALSE;
		} else {
			return EQUALS;
		}
	}

	public static String getSuffix(Evaluation evaluation){
		if (evaluation.equals(EQUALS)){
			return EQUALS_SUFFIX;
		} else if (evaluation.equals(IN)){
			return IN_SUFFIX;
		} else if (evaluation.equals(NOT_EQUALS)){
			return NOT_EQUALS_SUFFIX;
		} else if (evaluation.equals(NOT_IN)){
			return NOT_IN_SUFFIX;
		} else if (evaluation.equals(LIKE)){
			return LIKE_SUFFIX;
		} else if (evaluation.equals(NOT_LIKE)){
			return NOT_LIKE_SUFFIX;
		} else if (evaluation.equals(STARTS_WITH)){
			return STARTS_WITH_SUFFIX;
		} else if (evaluation.equals(ENDS_WITH)){
			return ENDS_WITH_SUFFIX;
		} else if (evaluation.equals(GREATER_THAN)){
			return GREATER_THAN_SUFFIX;
		} else if (evaluation.equals(LESS_THAN)){
			return LESS_THAN_SUFFIX;
		} else if (evaluation.equals(GREATER_THAN_EQUALS)){
			return GREATER_THAN_EQUALS_SUFFIX;
		} else if (evaluation.equals(LESS_THAN_EQUALS)){
			return LESS_THAN_EQUALS_SUFFIX;
		} else if (evaluation.equals(BETWEEN)){
			return BETWEEN_SUFFIX;
		} else if (evaluation.equals(BETWEEN_INCLUSIVE)){
			return BETWEEN_INCLUSIVE_SUFFIX;
		} else if (evaluation.equals(OUTSIDE)){
			return OUTSIDE_SUFFIX;
		} else if (evaluation.equals(OUTSIDE_INCLUSIVE)){
			return OUTSIDE_INCLUSIVE_SUFFIX;
		} else if (evaluation.equals(IS_NULL)){
			return IS_NULL_SUFFIX;
		} else if (evaluation.equals(NOT_NULL)){
			return NOT_NULL_SUFFIX;
		} else if (evaluation.equals(IS_TRUE)){
			return IS_TRUE_SUFFIX;
		} else if (evaluation.equals(IS_FALSE)){
			return IS_FALSE_SUFFIX;
		} else {
			return null;
		}
	}

}
