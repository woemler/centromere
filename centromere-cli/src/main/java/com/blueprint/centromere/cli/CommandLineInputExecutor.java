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
import com.beust.jcommander.MissingCommandException;
import com.blueprint.centromere.cli.Printer.Level;
import com.blueprint.centromere.cli.arguments.DeleteCommandArguments;
import com.blueprint.centromere.cli.arguments.ImportCommandArguments;
import com.blueprint.centromere.cli.arguments.ImportFileCommandArguments;
import com.blueprint.centromere.cli.arguments.ImportManifestCommandArguments;
import com.blueprint.centromere.cli.arguments.ListCommandArguments;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * Primary command line input handler.  Parses input and execute the appropriate functions.
 *
 * @author woemler
 * @since 0.5.0
 */
public class CommandLineInputExecutor implements CommandLineRunner {
	
  private FileImportExecutor fileImportExecutor;
	private ManifestImportExecutor manifestImportExecutor;
	private ListCommandExecutor listCommandExecutor;
	private DeleteCommandExecutor deleteCommandExecutor;
	
	public static final String IMPORT_COMMAND = "import";
	public static final String IMPORT_FILE_COMMAND = "file";
	public static final String IMPORT_BATCH_COMMAND = "batch";

	public static final String LIST_COMMAND = "list";
	public static final String DELETE_COMMAND = "delete";

	private static final Logger logger = LoggerFactory.getLogger(CommandLineInputExecutor.class);

  /**
   * Accepts command line input and passes it to processing methods.  Throws an exception to halt
   *   the pipeline if an error is hit in a runner.
   * 
   * @param args string arguments from command line
   * @throws Exception Exception any exception thrown by runners
   */
	@Override 
	public void run(String... args) throws Exception {
		int code = 1;
		Date start = new Date();
		try {
			code = processArguments(args);
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
		
		JCommander jc = new JCommander();
		jc.setAcceptUnknownOptions(true);
		
		ImportCommandArguments importCommandArguments = new ImportCommandArguments();
		ImportFileCommandArguments importFileCommandArguments = new ImportFileCommandArguments();
		ImportManifestCommandArguments importManifestCommandArguments = new ImportManifestCommandArguments();
		
		jc.addCommand(IMPORT_COMMAND, importCommandArguments);
		
		JCommander importJc = jc.getCommands().get(IMPORT_COMMAND);
		importJc.addCommand(IMPORT_FILE_COMMAND, importFileCommandArguments);
		importJc.addCommand(IMPORT_BATCH_COMMAND, importManifestCommandArguments);

    ListCommandArguments listCommandArguments = new ListCommandArguments();
    jc.addCommand(LIST_COMMAND, listCommandArguments);
    JCommander listJc = jc.getCommands().get(LIST_COMMAND);

    DeleteCommandArguments deleteCommandArguments = new DeleteCommandArguments();
    jc.addCommand(DELETE_COMMAND, deleteCommandArguments);
    JCommander deleteJc = jc.getCommands().get(DELETE_COMMAND);
    
		int code = 1;
		
		try {
			jc.parse(args);
		} catch (MissingCommandException e){
      unknownCommand();
			return code;
		}
		
		String mainCommand = jc.getParsedCommand();
		
		// File import
		if (IMPORT_COMMAND.equals(mainCommand)) {
      
		  String importCommand = importJc.getParsedCommand();
      
		  // Single file import
		  if (IMPORT_FILE_COMMAND.equals(importCommand)) {
        logger.info(String.format("Running import file command with arguments: %s %s",
            importFileCommandArguments.toString(), importCommandArguments.toString()));
        try {
          fileImportExecutor.run(importFileCommandArguments.getDataType(),
              importFileCommandArguments.getFilePath());
        } catch (Exception e) {
          throw new CommandLineRunnerException(e);
        }
        code = 0;
      
      // Manifest import
		  } else if (IMPORT_BATCH_COMMAND.equals(importCommand)) {
		    
        logger.info(String.format("Running import batch command with arguments: %s %s",
            importManifestCommandArguments.toString(), importCommandArguments.toString()));
        try {
          manifestImportExecutor.run(importManifestCommandArguments.getFilePath());
        } catch (Exception e){
          throw new CommandLineRunnerException(e);
        }
        code = 0;
      
		  } else {
		    
        Printer.print(String.format("Unknown import command: %s", importCommand), logger, Level.ERROR);
        System.out.println("\nAvailable import commands: ");
        System.out.println("\n    file: Imports a single file.");
        importJc.usage(IMPORT_FILE_COMMAND);
        System.out.println("\n    batch: Imports multiple files, defined using a manifest file.");
        importJc.usage(IMPORT_BATCH_COMMAND);
        
      }
      
    } else if (LIST_COMMAND.equals(mainCommand)) {

      String listable = "";
      if (listCommandArguments.getArgs() != null && !listCommandArguments.getArgs().isEmpty()) {
        listable = listCommandArguments.getArgs().get(0);
      }
      listCommandExecutor.run(listable, listCommandArguments.getShowDetails());
      code = 0;

    } else if (DELETE_COMMAND.equals(mainCommand)){

		  String deleteable = "";
      List<String> toDelete = deleteCommandArguments.getArgs();
		  if (toDelete != null && !toDelete.isEmpty()){
        deleteable = toDelete.remove(0);
		    if (toDelete.size() > 0) {
          deleteCommandExecutor.run(deleteable, toDelete);
          code = 0;
        } else {
		      Printer.print(String.format("No items selected for deletion: %s", deleteable), logger, Level.WARN);
        }
		    
      } else {
		    deleteJc.usage();
      }
		  
		} else {
			unknownCommand();
		}
		
		return code;
		
	}
	
	private void unknownCommand(){
    logger.error("Invalid command");
    System.out.println("ERROR: Invalid command");
    System.out.println("Available commands: import, list, delete");
    System.out.println("    import: Imports one or more files into the data warehouse.");
    System.out.println("    list:   List available data models, file processors, imported files, and other resources.");
    System.out.println("    delete:   Remove data sets, data files, and their associated records from the warehouse.");
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
	public void setFileImportExecutor(FileImportExecutor fileImportExecutor) {
		this.fileImportExecutor = fileImportExecutor;
	}

	@Autowired
  public void setManifestImportExecutor(ManifestImportExecutor manifestImportExecutor) {
    this.manifestImportExecutor = manifestImportExecutor;
  }

  @Autowired
  public void setListCommandExecutor(ListCommandExecutor listCommandExecutor) {
    this.listCommandExecutor = listCommandExecutor;
  }

  @Autowired
  public void setDeleteCommandExecutor(DeleteCommandExecutor deleteCommandExecutor) {
    this.deleteCommandExecutor = deleteCommandExecutor;
  }
}
