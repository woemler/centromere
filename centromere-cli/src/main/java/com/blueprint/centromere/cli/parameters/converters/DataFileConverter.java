package com.blueprint.centromere.cli.parameters.converters;

import com.blueprint.centromere.core.commons.model.DataFile;

/**
 * @author woemler
 */
public class DataFileConverter extends GenericConverter<DataFile> {

  public DataFileConverter(){
    super(DataFile.class);
  }
  
}
