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

package com.blueprint.centromere.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.commands.BatchCommandExecutor;
import com.blueprint.centromere.cli.commands.CreateCommandExecutor;
import com.blueprint.centromere.cli.commands.DeleteCommandExecutor;
import com.blueprint.centromere.cli.commands.ImportCommandExecutor;
import com.blueprint.centromere.cli.commands.ListCommandExecutor;
import com.blueprint.centromere.cli.commands.UpdateCommandExecutor;
import com.blueprint.centromere.cli.parameters.BaseParameters;
import com.blueprint.centromere.cli.parameters.BatchCommandParameters;
import com.blueprint.centromere.cli.parameters.CreateCommandParameters;
import com.blueprint.centromere.cli.parameters.DeleteCommandParameters;
import com.blueprint.centromere.cli.parameters.ImportCommandParameters;
import com.blueprint.centromere.cli.parameters.ListCommandParameters;
import com.blueprint.centromere.cli.parameters.UpdateCommandParameters;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * Primary command line input handler.  Parses input and execute the appropriate functions.
 *
 * @author woemler
 * @since 0.5.0
 */
public class JCommanderInputExecutor implements ApplicationRunner {

  private static final Logger logger = LoggerFactory.getLogger(JCommanderInputExecutor.class);
	
  private ImportCommandExecutor importCommandExecutor;
	private BatchCommandExecutor batchCommandExecutor;
	private ListCommandExecutor listCommandExecutor;
	private DeleteCommandExecutor deleteCommandExecutor;
	private CreateCommandExecutor createCommandExecutor;
	private UpdateCommandExecutor updateCommandExecutor;
	
	private JCommander jc;

  /**
   * Accepts command line input and passes it to processing methods.  Throws an exception to halt
   *   the pipeline if an error is hit in a runner.
   *
   * @param args string arguments from command line
   * @throws Exception Exception any exception thrown by runners
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    int code = 1;
    Date start = new Date();
    try {
      code = processArguments(args.getSourceArgs());
    } finally {
      Date end = new Date();
      String message;
      if (code > 0){
        message = String.format("Command line execution exited with errors.  Elapsed time: %s",
            formatInterval(end.getTime() - start.getTime()));

      } else {
        message = String.format("Command line execution finished.  Elapsed time: %s",
            formatInterval(end.getTime() - start.getTime()));
      }
      logger.info(message);
    }
  }
  
  /**
   * Processes the input arguments and executes the appropriate action.  Uses JCommander for argument
   *   parsing.
   * 
   * @param args string arguments from command line
   * @return application exit code
   * @throws Exception any exception thrown by runners
   */
	private int processArguments(String... args) throws Exception {

    BaseParameters baseParameters = new BaseParameters();
    ImportCommandParameters importParameters = new ImportCommandParameters();
    BatchCommandParameters batchParameters = new BatchCommandParameters();
    CreateCommandParameters createParameters = new CreateCommandParameters();
    UpdateCommandParameters updateParameters = new UpdateCommandParameters();
    ListCommandParameters listParameters = new ListCommandParameters();
    DeleteCommandParameters deleteParameters = new DeleteCommandParameters();
	  
	  jc = JCommander.newBuilder()
        .acceptUnknownOptions(true)
        .addObject(baseParameters)
        .addCommand(ImportCommandParameters.COMMAND, importParameters)
        .addCommand(BatchCommandParameters.COMMAND, batchParameters)
        .addCommand(CreateCommandParameters.COMMAND, createParameters)
        .addCommand(UpdateCommandParameters.COMMAND, updateParameters)
        .addCommand(ListCommandParameters.COMMAND, listParameters)
        .addCommand(DeleteCommandParameters.COMMAND, deleteParameters)
        .build();
    
		int code = 1;
		
		try {
			jc.parse(args);
		} catch (ParameterException e){
      jc.usage();
			return code;
		}
		
		String mainCommand = jc.getParsedCommand();
		
		// File import
		if (ImportCommandParameters.COMMAND.equals(mainCommand)) {

      Printer.print(String.format("Running import file command with arguments: %s",
          importParameters.toString()), logger, Level.INFO);
      try {
        importCommandExecutor.run(importParameters);
      } catch (Exception e) {
        throw new CommandLineRunnerException(e);
      }
      code = 0;
      
    // Manifest import
    } else if (BatchCommandParameters.COMMAND.equals(mainCommand)) {
		    
        Printer.print(String.format("Running import batch command with arguments: %s ",
            batchParameters.toString()), logger, Level.INFO);
        try {
          batchCommandExecutor.run(batchParameters.getFilePath());
        } catch (Exception e){
          throw new CommandLineRunnerException(e);
        }
        code = 0;
      
    } 
    
    // CREATE command
    else if (CreateCommandParameters.COMMAND.equals(mainCommand)){
		  
		  Printer.print(String.format("Creating new model record with arguments: %s", 
          createParameters.toString()), logger, Level.INFO);
		  try {
		    createCommandExecutor.run(createParameters);
      } catch (Exception e){
		    throw new CommandLineRunnerException(e);
      }
      code = 0;
		  
    }

    // UPDATE command
    else if (UpdateCommandParameters.COMMAND.equals(mainCommand)){

      Printer.print(String.format("Updating existing model record with arguments: %s",
          updateParameters.toString()), logger, Level.INFO);
      try {
        updateCommandExecutor.run(updateParameters);
      } catch (Exception e){
        throw new CommandLineRunnerException(e);
      }
      code = 0;

    }
    
    // List command
    else if (ListCommandParameters.COMMAND.equals(mainCommand)) {

      String listable = "";
      if (listParameters.getArgs() != null && !listParameters.getArgs().isEmpty()) {
        listable = listParameters.getArgs().get(0);
      }
      listCommandExecutor.run(listable, listParameters.isShowDetails());
      code = 0;

    } 
    
    // Delete command
    else if (DeleteCommandParameters.COMMAND.equals(mainCommand)){

      deleteCommandExecutor.run(deleteParameters);
      code = 0;
		  
		} else {
			jc.usage();
			//code = 1;
		}
		
		return code;
		
	}
	
	
	/**
	 * From http://stackoverflow.com/a/6710604/1458983
	 * Converts a long-formatted timespan into a human-readable string that denotes the length of time 
	 *   that has elapsed.
	 * @param l Long representation of a diff between two time stamps.
	 * @return String formatted time span.
	 */
	private static String formatInterval(final long l) {
		final long hr = TimeUnit.MILLISECONDS.toHours(l);
		final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
		final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
		return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
	}

	@Autowired
	public void setImportCommandExecutor(ImportCommandExecutor importCommandExecutor) {
		this.importCommandExecutor = importCommandExecutor;
	}

	@Autowired
  public void setBatchCommandExecutor(BatchCommandExecutor batchCommandExecutor) {
    this.batchCommandExecutor = batchCommandExecutor;
  }

  @Autowired
  public void setListCommandExecutor(ListCommandExecutor listCommandExecutor) {
    this.listCommandExecutor = listCommandExecutor;
  }

  @Autowired
  public void setDeleteCommandExecutor(DeleteCommandExecutor deleteCommandExecutor) {
    this.deleteCommandExecutor = deleteCommandExecutor;
  }

  @Autowired
  public void setCreateCommandExecutor(CreateCommandExecutor createCommandExecutor) {
    this.createCommandExecutor = createCommandExecutor;
  }

  @Autowired
  public void setUpdateCommandExecutor(
      UpdateCommandExecutor updateCommandExecutor) {
    this.updateCommandExecutor = updateCommandExecutor;
  }
}
