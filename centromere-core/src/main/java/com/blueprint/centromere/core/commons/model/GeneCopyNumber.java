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

package com.blueprint.centromere.core.commons.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
public class GeneCopyNumber extends Data {
	
	private Double value;
	private Double lossOfHeterozygosity;
	private Double ploidy;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

  public Double getLossOfHeterozygosity() {
    return lossOfHeterozygosity;
  }

  public void setLossOfHeterozygosity(Double lossOfHeterozygosity) {
    this.lossOfHeterozygosity = lossOfHeterozygosity;
  }

  public Double getPloidy() {
    return ploidy;
  }

  public void setPloidy(Double ploidy) {
    this.ploidy = ploidy;
  }
}
