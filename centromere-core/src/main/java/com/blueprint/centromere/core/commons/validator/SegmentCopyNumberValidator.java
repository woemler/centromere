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

import com.blueprint.centromere.core.commons.model.SegmentCopyNumber;
import org.springframework.validation.Errors;

/**
 * @author woemler
 */
public class SegmentCopyNumberValidator extends DataValidator {

	@Override 
	public boolean supports(Class<?> aClass) {
		return aClass.equals(SegmentCopyNumber.class);
	}

	@Override 
	public void validate(Object o, Errors errors) {
		super.validate(o, errors);
		SegmentCopyNumber data = (SegmentCopyNumber) o;
		if (data.getValue() == null) errors.reject("value", "value.empty");
		if (data.getChromosome() == null) errors.reject("chromosome", "chromosome.empty");
		if (data.getSegmentStart() == null) errors.reject("segmentStart", "segmentStart.empty");
		if (data.getSegmentStart() < 0) errors.reject("segmentStart", "segmentStart.invalidNumber");
		if (data.getSegmentEnd() == null) errors.reject("segmentEnd", "segmentEnd.empty");
		if (data.getSegmentEnd() < 0) errors.reject("segmentEnd", "segmentEnd.invalidNumber");
	}
}
