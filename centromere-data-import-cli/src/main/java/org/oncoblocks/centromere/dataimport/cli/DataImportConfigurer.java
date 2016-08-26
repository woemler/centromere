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

import org.oncoblocks.centromere.core.config.*;
import org.oncoblocks.centromere.core.dataimport.RecordProcessor;
import org.oncoblocks.centromere.core.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Map;

/**
 * Helper class for handling default creation of command line importer bean classes and configurations.
 *   Includes several classes that can be overridden to provide customized behavior.  
 * 
 * @author woemler
 */

public abstract class DataImportConfigurer extends ModelComponentRegistrationConfigurer {
	
	@Autowired private ApplicationContext applicationContext;
	

	@Bean
	public DataImportCommandLineRunner commandLineRunner(){
		return new DataImportCommandLineRunner(addCommandRunner(), importCommandRunner());
	}

	@Bean
	public ImportCommandRunner importCommandRunner(){
		return new ImportCommandRunner((DataTypeProcessorBeanRegistry) this.modelRegistry().getProcessorRegistry());
	}

	@Bean
	public AddCommandRunner addCommandRunner(){
		return new AddCommandRunner();
	}

	
}
