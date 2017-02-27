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

import com.blueprint.centromere.core.commons.models.DataSet;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.commons.models.Subject;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author woemler
 * @since 0.5.0
 */
public class TcgaSupport {

  private static final Logger logger = LoggerFactory.getLogger(TcgaSupport.class);

  private SubjectRepository subjectRepository;
  private SampleRepository sampleRepository;

  public Sample createSample(String sampleName, DataSet dataSet){

    String subjectName = getSubjectNameFromSampleName(sampleName);
    if (subjectName == null){
      logger.warn(String.format("Unable to extract subject name from sample name: %s", sampleName));
      return null;
    }

    Subject subject = subjectRepository.findOneByName(subjectName);
    if (subject == null){
      logger.warn(String.format("No registered subject exists for name: %s", subjectName));
      return null;
    }

    Sample sample = new Sample();
    sample.setName(sampleName);
    sample.setSubject(subject);
    sample.setSubjectId(subject.getId());
    sample.setDataSet(dataSet);
    sample.setDataSetId(dataSet.getId());
    sample.setTissue(subject.getAttribute("tumor_tissue_site"));
    sample.setHistology(subject.getAttribute("histological_type"));
    sampleRepository.save(sample);

    return sample;

  }

  public Sample findSample(String sampleName){
    return sampleRepository.findOneByName(sampleName);
  }

  private static final Pattern subjectNamePattern =
      Pattern.compile("(tcga-[a-zA-Z0-9]+-[a-zA-Z0-9]+)-.+", Pattern.CASE_INSENSITIVE);

  public static String getSubjectNameFromSampleName(String sample){
    Matcher matcher = subjectNamePattern.matcher(sample);
    if (matcher.matches()) {
      return matcher.group(1);
    } else {
      return null;
    }
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
