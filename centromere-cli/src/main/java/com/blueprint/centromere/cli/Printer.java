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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public class Printer {
  
  private static final Logger logger = LoggerFactory.getLogger(Printer.class);

  public enum Level { ERROR, WARN, INFO, DEBUG }
  
  public static void print(String message, Logger logger,  Level level){
    switch (level) {
      case ERROR:
        logger.error(message);
        break;
      case WARN:
        logger.warn(message);
        break;
      case INFO:
        logger.info(message);
        break;
      case DEBUG:
        logger.debug(message);
        break;
      default:
        logger.debug(message);
    }
    System.out.println(message);
  }
  
  public static void print(String message, Level level){
    print(message, logger, level);
  }
  
  public static void print(String message){
    print(message, logger, Level.DEBUG);
  }

}
