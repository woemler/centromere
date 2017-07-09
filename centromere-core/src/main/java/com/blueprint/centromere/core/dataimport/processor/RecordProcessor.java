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

package com.blueprint.centromere.core.dataimport.processor;

import com.blueprint.centromere.core.commons.support.DataFileAware;
import com.blueprint.centromere.core.commons.support.DataSetAware;
import com.blueprint.centromere.core.dataimport.DataImportComponent;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.importer.RecordImporter;
import com.blueprint.centromere.core.dataimport.reader.RecordReader;
import com.blueprint.centromere.core.dataimport.writer.RecordWriter;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import org.springframework.validation.Validator;

/**
 * Record processor take all of the individual data import components (reader, writer, validator,
 *   and importer), and combines them to handle a single data type's import
 * 
 * @author woemler
 */
public interface RecordProcessor<T extends Model<?>> 
		extends DataImportComponent, ModelSupport<T>, DataFileAware, DataSetAware {

	/**
	 * Executes the pipeline and processes the input through the individual components.
	 */
	void run() throws DataImportException;

  /**
   * Executes if the {@link #run()} method fails to execute properly, in place of the 
   * {@link #doAfter()} method.
   */
	void doOnFailure() throws DataImportException;

  /**
   * Indicates whether the processor component has failed its import execution, even if an 
   *   uncaught exception has not been thrown.
   * 
   * @return
   */
	boolean isInFailedState();
	
	/* Getters and Setters */
	void setReader(RecordReader<T> reader);
	RecordReader<T> getReader();
	void setValidator(Validator validator);
	Validator getValidator();
	void setWriter(RecordWriter<T> writer);
	RecordWriter<T> getWriter();
	void setImporter(RecordImporter importer);
	RecordImporter getImporter();
	
	
}
