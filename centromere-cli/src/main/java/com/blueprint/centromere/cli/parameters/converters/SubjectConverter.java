package com.blueprint.centromere.cli.parameters.converters;

import com.blueprint.centromere.core.commons.model.Subject;

/**
 * @author woemler
 */
public class SubjectConverter extends GenericConverter<Subject> {
  
  public SubjectConverter(){
    super(Subject.class);
  }

}
