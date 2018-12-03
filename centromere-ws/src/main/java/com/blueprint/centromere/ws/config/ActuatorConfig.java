package com.blueprint.centromere.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author woemler
 */
public class ActuatorConfig {

  @Configuration
  @PropertySource("classpath:web-defaults.properties")
  public static class DefaultActuatorConfig {
    
    //TODO: Custom monitoring components
    
  }
  
}
