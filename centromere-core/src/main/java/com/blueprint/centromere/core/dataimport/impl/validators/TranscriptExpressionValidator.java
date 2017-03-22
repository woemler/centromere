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

package com.blueprint.centromere.core.dataimport.impl.validators;

import com.blueprint.centromere.core.model.impl.TranscriptExpression;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author woemler
 */
public class TranscriptExpressionValidator extends DataValidator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return aClass.equals(TranscriptExpression.class);
	}

	@Override 
	public void validate(Object o, Errors errors) {
		super.validate(o, errors);
		TranscriptExpression data = (TranscriptExpression) o;
		if (data.getValue() == null) errors.reject("value", "value.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "transcriptAccession", "transcriptAccession.empty");
	}
}
