package com.blueprint.centromere.cli.parameters.converters;

import com.blueprint.centromere.core.commons.model.Sample;

/**
 * @author woemler
 */
public class SampleConverter extends GenericConverter<Sample> {

  public SampleConverter(){
    super(Sample.class);
  }
  
}
