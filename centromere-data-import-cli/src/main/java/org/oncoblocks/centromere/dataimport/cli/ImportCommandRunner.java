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

package org.oncoblocks.centromere.dataimport.cli;

import org.oncoblocks.centromere.core.dataimport.BasicImportOptions;
import org.oncoblocks.centromere.core.dataimport.DataImportException;
import org.oncoblocks.centromere.core.dataimport.ImportOptionsAware;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.util.DataTypeProcessorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Handles execution of the {@code import} command arguments.  This command attempts to process
 *   data from a referenced input source and load it into the data warehouse.  The ImportCommandRunner
 *   handles the import by taking and instance of {@link ImportCommandArguments}, generated from 
 *   parsing command line input, and using the submitted parameters to determine how the referenced
 *   data source will be processed.
 *   
 *   By default, a {@link DataTypeProcessorRegistry} will map user inputted data type labels to
 *   existing {@link RecordProcessor} beans, which are expected to be capable of 
 *   handling the referenced data source.  Before the data source is processed, processor instances
 *   are updated with user input parameters, configured, and executed upon the input data source.
 * 
 * @author woemler
 */
public class ImportCommandRunner {
	
	private DataTypeProcessorRegistry registry;
	
	private static final Logger logger = LoggerFactory.getLogger(ImportCommandRunner.class);

	public ImportCommandRunner() {
	}

	public ImportCommandRunner(DataTypeProcessorRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Runs the import of the file provided in the input arguments.  Will choose the appropriate 
	 *   {@link RecordProcessor} instance, based on the supplied data type.  
	 * 
	 * @param arguments {@link ImportCommandArguments} instance, parsed from command line args.
	 * @throws Exception
	 */
	public void run(ImportCommandArguments arguments) throws Exception {
		logger.debug(String.format("[CENTROMERE] Starting ImportCommandRunner with arguments: %s", 
				 arguments.toString()));
		RecordProcessor processor = this.getProcessorByDataType(arguments.getDataType());
		logger.debug(String.format("[CENTROMERE] Using processor %s for data type %s.", 
				processor.getClass().getName(), arguments.getDataType()));
		BasicImportOptions options = arguments.getImportOptions();
		logger.debug(String.format("[CENTROMERE] Running import with options: %s", options.toString()));
		String input = arguments.getInputFilePath();
		Map<String, String> params = arguments.getParameters();
		if (processor instanceof ImportOptionsAware){
			((ImportOptionsAware) processor).setImportOptions(options);
		}
		processor.configureComponents();
		processor.doBefore(input, params);
		processor.run(input, params);
		processor.doAfter(input, params);
		logger.debug("[CENTROMERE] Import task complete.");
	}

	/**
	 * Returns reference to a {@link RecordProcessor} instance, assuming it has been registered with
	 *   the {@link org.oncoblocks.centromere.core.util.DataTypeProcessorRegistry} instance.  Throws 
	 *   an exception if no mapping exists.
	 * 
	 * @param dataType String label associated with a particular {@link RecordProcessor}
	 * @return {@link RecordProcessor}
	 * @throws DataImportException
	 */
	private RecordProcessor getProcessorByDataType(String dataType) throws DataImportException{
		if (!registry.exists(dataType)){
			throw new DataImportException(String.format("Unable to identify appropriate RecordProcessor "
					+ "for data type: %s.  This data type may not be registered, or there may not be a bean "
					+ "for the required processor instantiated.", dataType));
		}
		return registry.find(dataType);
	}

	@Autowired
	public void setRegistry(DataTypeProcessorRegistry registry) {
		this.registry = registry;
	}


}
