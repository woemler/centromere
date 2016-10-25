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

package com.blueprint.centromere.core.test;

import com.blueprint.centromere.core.dataimport.BasicImportOptions;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.RepositoryRecordWriter;
import com.blueprint.centromere.core.test.model.Gene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@Component
@DataTypes({ "gene_info" })
public class GeneInfoProcessor extends GenericRecordProcessor<Gene> {
	
	private final TestRepository testRepository;

	@Autowired
	public GeneInfoProcessor(TestRepository testRepository, BasicImportOptions importOptions) {
		this.setReader(new GeneInfoReader());
		this.setValidator(new GeneValidator());
		this.setWriter(new RepositoryRecordWriter<>(testRepository));
		this.setImportOptions(importOptions);
		this.setModel(Gene.class);
		this.testRepository = testRepository;
	}

	@Override 
	public void doBefore(Object... args) {
		testRepository.deleteAll();
	}

}
