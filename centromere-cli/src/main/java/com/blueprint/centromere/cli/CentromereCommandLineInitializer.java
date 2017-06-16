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
import com.blueprint.centromere.core.config.Profiles;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author woemler
 */
@SpringBootApplication
public class CentromereCommandLineInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(CentromereCommandLineInitializer.class);

  public static void run(Class<?> source, String[] args){
    SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
    builder.child(CommandLineInputConfiguration.class);
    
    builder.bannerMode((Mode.LOG));
    builder.web(false);
    String[] profiles = { Profiles.CLI_PROFILE };
    builder.profiles(profiles);
    logger.info(String.format("Running Centromere with profiles: %s", Arrays.asList(profiles)));
    logger.info(String.format("Running Centromere with arguments: %s", args.toString()));
    System.out.println("Starting Centromere CLI...\n");
    builder.run(args);
  }
	
}
