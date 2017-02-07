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

package com.blueprint.centromere.core.test.dataimport;

import com.blueprint.centromere.core.commons.models.Gene;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author woemler
 */
@Component
public class GeneValidator implements Validator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return aClass.equals(Gene.class);
	}

	@Override 
	public void validate(Object o, Errors errors) {
		Gene gene = (Gene) o;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors,  "primaryGeneSymbol", "symbol.empty");
	}
}
