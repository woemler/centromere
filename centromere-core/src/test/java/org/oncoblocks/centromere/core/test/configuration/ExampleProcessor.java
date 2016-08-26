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

package org.oncoblocks.centromere.core.test.configuration;

import org.oncoblocks.centromere.core.dataimport.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

/**
 * @author woemler
 */
@DataTypes({"example_data"})
@Component
public class ExampleProcessor implements RecordProcessor<ExampleModel>{

	@Override public void run(Object... args) throws DataImportException {
		
	}

	@Override public void setReader(RecordReader<ExampleModel> reader) {

	}

	@Override public RecordReader<ExampleModel> getReader() {
		return null;
	}

	@Override public void setValidator(Validator validator) {

	}

	@Override public Validator getValidator() {
		return null;
	}

	@Override public void setWriter(RecordWriter<ExampleModel> writer) {

	}

	@Override public RecordWriter<ExampleModel> getWriter() {
		return null;
	}

	@Override public void setImporter(RecordImporter importer) {

	}

	@Override public RecordImporter getImporter() {
		return null;
	}

	@Override public void doBefore(Object... args) throws DataImportException {

	}

	@Override public void doAfter(Object... args) throws DataImportException {

	}

	@Override public void afterPropertiesSet() throws Exception {

	}

	@Override public Class<ExampleModel> getModel() {
		return ExampleModel.class;
	}

	@Override public void setModel(Class<ExampleModel> model) {

	}
}
