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

import com.blueprint.centromere.cli.config.CommandLineInputConfiguration;
import com.blueprint.centromere.core.config.AutoConfigureCentromere;
import com.blueprint.centromere.core.config.Profiles;
import com.google.common.collect.ObjectArrays;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Base class for creating Spring Boot executables for command line interfaces with Centromere data
 *   warehouses. Main application classes should extend this and be annotated with {@link AutoConfigureCentromere}.
 * 
 * @author woemler
 */
@SpringBootApplication
public class CentromereCommandLineInitializer {

  public static final String SINGLE_EXECUTION_COMMAND = "-X";
  public static final String SINGLE_COMMAND_PROFILE = "single_command";
  
	private static final Logger logger = LoggerFactory.getLogger(CentromereCommandLineInitializer.class);

  /**
   * Create and execute a {@link org.springframework.boot.SpringApplication} with the configured Centromere
   *   components.
   * 
   * @param source
   * @param args
   */
  public static void run(Class<?> source, String[] args){
    SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
    builder.child(CommandLineInputConfiguration.class);
    builder.bannerMode((Mode.LOG));
    builder.web(false);
    builder.profiles(getActiveProfiles(source, args));
    logger.info(String.format("Running Centromere with arguments: %s", Arrays.asList(args).toString()));
    System.out.println("Starting Centromere CLI...\n");
    builder.run(args);
  }

  /**
   * Inspects the command line arguments and the target source application class to determine if any
   *   additional profiles should be activated.  
   * 
   * @param source
   * @param args
   * @return
   */
  private static String[] getActiveProfiles(Class<?> source, String[] args){
    
    String[] profiles;
    
    if (source.isAnnotationPresent(AutoConfigureCentromere.class)){
      AutoConfigureCentromere annotation = source.getAnnotation(AutoConfigureCentromere.class);
      profiles = annotation.useCustomSchema() ?
          new String[] { Profiles.CLI_PROFILE, Profiles.SCHEMA_CUSTOM } : new String[] { Profiles.CLI_PROFILE };
      logger.info(String.format("Running Centromere with profiles: %s", Arrays.asList(profiles)));
    } else {
      logger.info("Running Centromere with default profiles.");
      profiles = new String[]{};
    }
    
    if (Arrays.asList(args).contains(SINGLE_EXECUTION_COMMAND)) {
      logger.info("Running in single-command mode.");
      profiles = ObjectArrays.concat(profiles, SINGLE_COMMAND_PROFILE);
    } else {
      logger.info("Running in interactive shell mode.");
    }
    
    return profiles;
  }
	
}
