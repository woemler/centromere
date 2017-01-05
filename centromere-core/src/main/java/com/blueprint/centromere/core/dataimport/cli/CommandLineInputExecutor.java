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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author woemler
 * @since 0.5.0
 */
public class CommandLineInputExecutor implements CommandLineRunner {
	
	private FileImportExecutor fileImportExecutor;
	
	public static final String IMPORT_COMMAND = "import";
	public static final String IMPORT_FILE_COMMAND = "file";
	public static final String IMPORT_BATCH_COMMAND = "batch";
	private static final Logger logger = LoggerFactory.getLogger(CommandLineInputExecutor.class);

	@Override 
	public void run(String... args) throws Exception {
		int code = 1;
		Date start = new Date();
		try {
			code = processArguments(args);
		} finally {
			Date end = new Date();
			if (code > 0){
				logger.info(String.format("Command line execution exited with errors.  Elapsed time: %s", 
						formatInterval(end.getTime() - start.getTime())));
			} else {
				logger.info(String.format("Command line execution finished.  Elapsed time: %s", 
						formatInterval(end.getTime() - start.getTime())));
			}
		}
		
	}
	
	private int processArguments(String... args) throws Exception {
		
		JCommander jc = new JCommander();
		ImportCommandArguments importCommandArguments = new ImportCommandArguments();
		ImportFileCommandArguments importFileCommandArguments = new ImportFileCommandArguments();
		ImportManifestCommandArguments importManifestCommandArguments = new ImportManifestCommandArguments();
		jc.addCommand(IMPORT_COMMAND, importCommandArguments);
		JCommander importJc = jc.getCommands().get(IMPORT_COMMAND);
		importJc.addCommand(IMPORT_FILE_COMMAND, importFileCommandArguments);
		importJc.addCommand(IMPORT_BATCH_COMMAND, importManifestCommandArguments);
		int code = 1;
		
		try {
			jc.parse(args);
		} catch (MissingCommandException e){
			logger.error("Invalid arguments.");
			printUsage(jc);
		}
		
		String mainCommand = jc.getParsedCommand();
		if (IMPORT_COMMAND.equals(mainCommand)){
			String importCommand = importJc.getParsedCommand();
			if (IMPORT_FILE_COMMAND.equals(importCommand)){
				logger.info(String.format("Running import file command with arguments: %s %s", 
						importFileCommandArguments.toString(), importCommandArguments.toString()));
				try {
					fileImportExecutor.run(importFileCommandArguments.getDataType(),
							importFileCommandArguments.getFilePath());
				} catch (Exception e){
					e.printStackTrace();
				}
				code = 0;
			} else if (IMPORT_BATCH_COMMAND.equals(importCommand)){
				logger.info(String.format("Running import batch command with arguments: %s %s",
						importManifestCommandArguments.toString(), importCommandArguments.toString()));
				// TODO
			} else {
				logger.error(String.format("Unknown import command: %s", importCommand));
				printUsage(jc);
			}
		} else {
			logger.error(String.format("Unknown command: %s", mainCommand));
			printUsage(jc);
		}
		
		return code;
		
	}
	
	private void printUsage(JCommander jc){
		JCommander importJc = jc.getCommands().get(IMPORT_COMMAND);
		jc.usage(IMPORT_COMMAND);
		importJc.usage(IMPORT_FILE_COMMAND);
		importJc.usage(IMPORT_BATCH_COMMAND);
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
}
