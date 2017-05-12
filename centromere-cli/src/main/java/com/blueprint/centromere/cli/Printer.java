package com.blueprint.centromere.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public class Printer {
  
  private static final Logger logger = LoggerFactory.getLogger(Printer.class);

  public static enum Level { ERROR, WARN, INFO, DEBUG }
  
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
