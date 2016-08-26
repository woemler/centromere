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

package org.oncoblocks.centromere.core.dataimport;

import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.model.ModelSupport;
import org.springframework.beans.BeanWrapperImpl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author woemler
 * @since 0.4.3
 */
public class BasicColumnMappingRecordReader<T extends Model<?>> extends AbstractRecordFileReader<T>
		implements ImportOptionsAware, ModelSupport<T> {
	
	private BasicImportOptions options = new BasicImportOptions();
	private Class<T> model;
	private Map<String,String> columnMap = new LinkedHashMap<>();
	private boolean headerFlag = true;
	
	@Override 
	public T readRecord() throws DataImportException {
		try {
			String line = this.getReader().readLine();
			while (line != null){
				if (!line.trim().equals("") && !line.startsWith("#")){
					if (headerFlag){
						parseHeader(line);
					} else {
						T record = getRecordFromLine(line);
						if (record != null){
							return record;
						}
					}
				}
				line = this.getReader().readLine();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private void parseHeader(String line){
		
	}
	
	private T getRecordFromLine(String line){
		T record = null;
		BeanWrapperImpl wrapper = new BeanWrapperImpl(model);
		
		return record;
	}

	@Override 
	public void doBefore(Object... args) throws DataImportException {
		super.doBefore(args);
		headerFlag = true;
	}

	public BasicImportOptions getImportOptions() {
		return options;
	}

	public void setImportOptions(ImportOptions options) {
		this.options = new BasicImportOptions(options);
	}

	public void setImportOptions(BasicImportOptions options) {
		this.options = options;
	}

	@Override public Class<T> getModel() {
		return model;
	}

	public void setModel(Class<T> model) {
		this.model = model;
	}
}
