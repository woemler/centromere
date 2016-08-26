/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.core.commons.models;

import org.oncoblocks.centromere.core.model.Alias;
import org.oncoblocks.centromere.core.model.Aliases;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.repository.Evaluation;

import java.io.Serializable;

/**
 * @author woemler
 */
public abstract class GeneExpression<ID extends Serializable> implements Model<ID>, GeneData {
	
	@Aliases({
			@Alias(value = "value.gt", evaluation = Evaluation.GREATER_THAN),
			@Alias(value = "value.gte", evaluation = Evaluation.GREATER_THAN_EQUALS),
			@Alias(value = "value.lt", evaluation = Evaluation.LESS_THAN),
			@Alias(value = "value.lte", evaluation = Evaluation.LESS_THAN_EQUALS),
			@Alias(value = "value.out", evaluation = Evaluation.OUTSIDE),
			@Alias(value = "value.bt", evaluation = Evaluation.BETWEEN)
	})
	private Double value;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
