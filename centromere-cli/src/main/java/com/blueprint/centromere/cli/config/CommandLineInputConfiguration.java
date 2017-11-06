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

package com.blueprint.centromere.cli.config;

import com.blueprint.centromere.cli.CentromereCommandLineInitializer;
import com.blueprint.centromere.cli.CommandLineInputExecutor;
import com.blueprint.centromere.cli.ModelProcessorBeanRegistry;
import com.blueprint.centromere.cli.commands.BatchCommandExecutor;
import com.blueprint.centromere.cli.commands.CreateCommandExecutor;
import com.blueprint.centromere.cli.commands.DeleteCommandExecutor;
import com.blueprint.centromere.cli.commands.ImportCommandExecutor;
import com.blueprint.centromere.cli.commands.ListCommandExecutor;
import com.blueprint.centromere.cli.commands.UpdateCommandExecutor;
import com.blueprint.centromere.core.config.Profiles;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.support.Repositories;

/**
 * @author woemler
 */
@Configuration
@Profile({ Profiles.CLI_PROFILE })
public class CommandLineInputConfiguration {
	
	@Bean
	public ModelProcessorBeanRegistry modelProcessorBeanRegistry(){
		return new ModelProcessorBeanRegistry();
	}
	
	@Bean
	public ImportCommandExecutor fileImportExecutor(){
		return new ImportCommandExecutor();
	}
	
	@Bean
	public BatchCommandExecutor manifestImportExecutor(){
		return new BatchCommandExecutor();
	}
	
	@Bean
  public DeleteCommandExecutor deleteCommandExecutor(){
	  return new DeleteCommandExecutor();
  }

	@Bean
  public ListCommandExecutor listCommandExecutor(){
	  return new ListCommandExecutor();
  }

  @Bean
  public CreateCommandExecutor createCommandExecutor(){
    return new CreateCommandExecutor();
  }
  
  @Bean
  public UpdateCommandExecutor updateCommandExecutor(){
    return new UpdateCommandExecutor();
  }
  
  @Bean
  public Repositories repositories(ApplicationContext context){
    return new Repositories(context);
  }
  
  @Configuration
  @Profile({CentromereCommandLineInitializer.SINGLE_COMMAND_PROFILE})
  public static class SingleCommandExecutionConfiguration {

    @Bean
    public CommandLineInputExecutor commandLineInputExecutor(){
      return new CommandLineInputExecutor();
    }
    
  }

}
