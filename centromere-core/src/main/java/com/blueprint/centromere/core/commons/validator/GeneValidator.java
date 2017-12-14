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

import com.blueprint.centromere.core.commons.model.Gene;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author woemler
 */
public class GeneValidator implements Validator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return Gene.class.isAssignableFrom(aClass);
	}

	@Override public void validate(Object o, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "symbol", "symbol.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxId", "taxId.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "chromosome", "chromosome.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "geneType", "geneType.empty");
	}
}
