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

import com.blueprint.centromere.core.commons.model.Mutation;
import org.springframework.validation.Errors;

/**
 * @author woemler
 */
public class MutationValidator extends DataValidator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return aClass.equals(Mutation.class);
	}

	@Override 
	public void validate(Object o, Errors errors) {
		Mutation data = (Mutation) o;
    if (data.getDataFileId() == null) errors.reject("dataFileId", "dataFileId.empty");
    if (data.getDataSetId() == null) errors.reject("dataSetId", "dataSetId.empty");
    if (data.getSampleId() == null) errors.reject("sampleId", "sampleId.empty");
		if (data.getVariantType() == null) errors.reject("variantType", "variantType.empty");
		if (data.getChromosome() == null) errors.reject("chromosome", "chromosome.empty");
		if (data.getDnaStartPosition() == null) errors.reject("dnaStartPosition", "dnaStartPosition.empty");
		//if (data.getDnaStopPosition() == null) errors.reject("dnaStopPosition", "dnaStopPosition.empty");
		if (data.getVariantClassification() == null) errors.reject("variantClassification", "variantClassification.empty");
	}
}
