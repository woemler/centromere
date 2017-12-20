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

package com.blueprint.centromere.core.commons.validator;

import com.blueprint.centromere.core.commons.model.Sample;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author woemler
 * @since 0.5.0
 */
public class SampleValidator implements Validator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return Sample.class.isAssignableFrom(aClass);
	}

	@Override 
	public void validate(Object o, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.empty");
	  ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sampleType", "sampleType.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tissue", "tissue.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "histology", "histology.empty");
	}
}
