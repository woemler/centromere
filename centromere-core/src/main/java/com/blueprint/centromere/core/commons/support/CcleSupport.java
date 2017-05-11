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

package com.blueprint.centromere.core.commons.support;

import com.blueprint.centromere.core.commons.model.DataSet;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woemler
 */
public class CcleSupport extends GenericDataSetSupport {

  private static final Logger logger = LoggerFactory.getLogger(CcleSupport.class);
  private static final String SAMPLE_TYPE = "cell line";

  public CcleSupport(
      SubjectRepository subjectRepository,
      SampleRepository sampleRepository) {
    super(subjectRepository, sampleRepository);
  }

  /**
   * Creates a new sample record, given only a name and an associated {@link DataSet} record.
   *
   * @param name sample name
   * @param dataSet DataSet record  @return a new Sample record
   */
  @Override
  public Sample createSample(String name, Subject subject, DataSet dataSet) {
    Sample sample = super.createSample(name, subject, dataSet);
    sample.setSampleType(SAMPLE_TYPE);
    return sample;
  }

  /**
   * Creates a new sample record, given only a name and an associated {@link DataSet} record.
   *
   * @param dataSet DataSet record
   * @return a new Sample record
   */
  @Override
  public Sample createSample(Subject subject, DataSet dataSet) {
    Sample sample = super.createSample(subject, dataSet);
    sample.setSampleType(SAMPLE_TYPE);
    return sample;
  }
  
  public Optional<Sample> fetchOrCreateSample(String name, DataSet dataSet){
    Optional<Sample> optional = this.findSample(name, dataSet);
    if (optional.isPresent()) return optional;
    Optional<Subject> subjectOptional = getSubjectRepository().findByName(name);
    if (subjectOptional.isPresent()){
      return Optional.ofNullable(createSample(name, subjectOptional.get(), dataSet));
    } else {
      return Optional.empty();
    }
  }
}
