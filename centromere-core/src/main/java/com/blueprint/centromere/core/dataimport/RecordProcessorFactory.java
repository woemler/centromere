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

package com.blueprint.centromere.core.dataimport;

import com.blueprint.centromere.core.model.Model;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;

import java.io.Serializable;

/**
 * Factory class for programatically creating instances of a {@link RecordProcessor} type, given types
 *   or instances of data import component classes.
 * 
 * @author woemler
 * @since 0.4.3
 */
public class RecordProcessorFactory<P extends RecordProcessor<T>, T extends Model<ID>, ID extends Serializable> {
	
	private final P processor;

	public RecordProcessorFactory(Class<P> type) throws IllegalAccessException, InstantiationException {
		this.processor = type.newInstance();
	}
	
	public RecordProcessorFactory(P processor){
		this.processor = processor;
	}
	
	public void setReader(Class<? extends RecordReader<T>> type) throws IllegalAccessException, InstantiationException {
		this.processor.setReader(type.newInstance());
	}
	
	public void setReader(RecordReader<T> reader){
		this.processor.setReader(reader);
	}
	
	public void setValidator(Class<? extends Validator> type) throws IllegalAccessException, InstantiationException {
		this.processor.setValidator(type.newInstance());
	}
	
	public void setValidator(Validator validator){
		this.setValidator(validator);
	}

	public void setWriter(Class<? extends RecordWriter<T>> type) throws IllegalAccessException, InstantiationException {
		this.processor.setWriter(type.newInstance());
	}

	public void setWriter(RecordWriter<T> writer){
		this.setWriter(writer);
	}

	public void setImporter(Class<? extends RecordImporter> type) throws IllegalAccessException, InstantiationException {
		this.processor.setImporter(type.newInstance());
	}

	public void setImporter(RecordImporter importer){
		this.setImporter(importer);
	}
	
	public void setModel(Class<T> model){
		processor.setModel(model);
	}
	
	public void setEnvironment(Environment environment){
		processor.setEnvironment(environment);
	}
	
	public P getProcessor() throws Exception {
		Assert.notNull(processor.getReader(), "RecordReader must not be null.");
		Assert.notNull(processor.getWriter(), "RecordWriter must not be null.");
		processor.afterPropertiesSet();
		return processor;
	}
	
}
