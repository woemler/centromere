/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.cli.dataimport.processor.impl.validator;

import com.blueprint.centromere.core.model.impl.GeneCopyNumber;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author woemler
 */
public class GeneCopyNumberValidator implements Validator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return GeneCopyNumber.class.isAssignableFrom(aClass);
	}

	@Override 
	public void validate(Object o, Errors errors) {
		GeneCopyNumber data = (GeneCopyNumber) o;
    if (data.getDataSourceId() == null) errors.reject("dataSourceId", "dataSourceId.empty");
    if (data.getDataSetId() == null) errors.reject("dataSetId", "dataSetId.empty");
    if (data.getGeneId() == null) errors.reject("geneId", "geneId.empty");
    if (data.getSampleId() == null) errors.reject("sampleId", "sampleId.empty");
		if (data.getValue() == null) errors.reject("value", "value.empty");
	}
}
