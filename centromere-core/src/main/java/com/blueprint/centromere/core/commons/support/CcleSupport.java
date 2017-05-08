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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author woemler
 */
public class CcleSupport implements DataSetSupport {

  private static final Logger logger = LoggerFactory.getLogger(CcleSupport.class);

  private SubjectRepository subjectRepository;
  private SampleRepository sampleRepository;

  /**
   * Creates a new sample record, given a name, {@link Subject} record, and an associated
   * {@link DataSet} record.
   *
   * @param name sample name
   * @param dataSet DataSet record  @return a new Sample record
   */
  @Override
  public Sample createSample(String name, Subject subject, DataSet dataSet) {
    return null;
  }

  /**
   * Finds and returns a {@link Sample} record for the given name and {@link DataSet} record,
   * if one exists.
   *
   * @param name sample name
   * @param dataSet DataSet record
   * @return an optional sample record
   */
  @Override
  public Optional<Sample> findSample(String name, DataSet dataSet) {
    return null;
  }

  @Autowired
  public void setSubjectRepository(
      SubjectRepository subjectRepository) {
    this.subjectRepository = subjectRepository;
  }

  @Autowired
  public void setSampleRepository(
      SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }
}
