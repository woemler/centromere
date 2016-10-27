/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.test.model;

import com.blueprint.centromere.core.commons.models.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
public class DataSetGenerator<T extends DataSet<?>> implements DummyDataGenerator<T> {

	@Override 
	public List<T> generateData(Class<T> type) throws Exception {
		
		List<T> dataSets = new ArrayList<>();
		
		T dataSet = type.newInstance();
		dataSet.setName("DataSetA");
		dataSet.setSource("Internal");
		dataSet.setVersion("1.0");
		dataSet.setDescription("This is an example data set.");
		dataSets.add(dataSet);

		dataSet = type.newInstance();
		dataSet.setName("DataSetB");
		dataSet.setSource("External");
		dataSet.setVersion("1.0");
		dataSet.setDescription("This is an example data set.");
		dataSets.add(dataSet);

		dataSet = type.newInstance();
		dataSet.setName("DataSetC");
		dataSet.setSource("Internal");
		dataSet.setVersion("2.0");
		dataSet.setDescription("This is an example data set.");
		dataSets.add(dataSet);

		dataSet = type.newInstance();
		dataSet.setName("DataSetD");
		dataSet.setSource("External");
		dataSet.setVersion("1.0");
		dataSet.setDescription("This is an example data set.");
		dataSets.add(dataSet);

		dataSet = type.newInstance();
		dataSet.setName("DataSetE");
		dataSet.setSource("Internal");
		dataSet.setVersion("1.0");
		dataSet.setDescription("This is an example data set.");
		dataSets.add(dataSet);
		
		return dataSets;
	}
}
