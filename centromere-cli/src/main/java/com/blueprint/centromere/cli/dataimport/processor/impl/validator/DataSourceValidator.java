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

import com.blueprint.centromere.core.model.impl.DataSource;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author woemler
 * @since 0.5.0
 */
public class DataSourceValidator implements Validator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return DataSource.class.isAssignableFrom(aClass);
	}

	@Override 
	public void validate(Object o, Errors errors) {
		DataSource dataSource = (DataSource) o;
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourceType", "sourceType.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "source", "source.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dataType", "dataType.empty");
		if (dataSource.getDateCreated() == null) errors.reject("dateCreated", "dateCreated.empty");
		if (dataSource.getDateUpdated() == null) errors.reject("dateUpdated", "dateUpdated.empty");
		if (dataSource.getDataSetId() == null) errors.reject("dataSetId", "dataSetId.empty");
	}
}
