package com.blueprint.centromere.cli.parameters.converters;

import com.beust.jcommander.IStringConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public abstract class GenericConverter<T> implements IStringConverter<Optional<T>> {

  private static final Logger logger = LoggerFactory.getLogger(GenericConverter.class);
  
  private final Class<T> model;

  public GenericConverter(Class<T> model) {
    this.model = model;
  }

  @Override
  public Optional<T> convert(String s) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return Optional.of(objectMapper.readValue(s, model));
    } catch (IOException e){
      logger.warn(String.format("Unable to convert string argument to %s instance: %s", 
          model.getSimpleName(), s));
      return Optional.empty();
    }
  }
  
}
