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

package com.blueprint.centromere.core.dataimport.cli;

import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.RecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author woemler
 * @since 0.5.0
 */
public class FileImportExecutor {
	
	private ModelProcessorBeanRegistry processorRegistry;
	
	private static final Logger logger = LoggerFactory.getLogger(FileImportExecutor.class);
	
	public void run(String dataType, String filePath) throws DataImportException{
		if (!processorRegistry.isSupportedDataType(dataType)){
			throw new DataImportException(String.format("Data type %s is not supported by a registered " 
					+ "record processor.", dataType));
		}
		logger.info(String.format("Running file import: data-type=%s  file=%s", dataType, filePath));
		RecordProcessor processor = processorRegistry.getByDataType(dataType);
		processor.doBefore(filePath);
		processor.run(filePath);
		processor.doAfter();
	}

	@Autowired
	public void setProcessorRegistry(ModelProcessorBeanRegistry processorRegistry) {
		this.processorRegistry = processorRegistry;
	}
}