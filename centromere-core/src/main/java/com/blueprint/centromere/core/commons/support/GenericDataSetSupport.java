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
import com.blueprint.centromere.core.commons.model.Subject.Attributes;
import com.blueprint.centromere.core.commons.repository.SampleRepository;
import com.blueprint.centromere.core.commons.repository.SubjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link DataSetSupport} implementation.  Handles generic sample creation and fetching,
 *   given {@link Subject} and {@link DataSet} records.  
 * 
 * @author woemler
 */
public class GenericDataSetSupport implements DataSetSupport {

  private static final Logger logger = LoggerFactory.getLogger(GenericDataSetSupport.class);

  private final SubjectRepository subjectRepository;
  private final SampleRepository sampleRepository;

  public GenericDataSetSupport(
      SubjectRepository subjectRepository,
      SampleRepository sampleRepository) {
    this.subjectRepository = subjectRepository;
    this.sampleRepository = sampleRepository;
  }

  /**
   * Creates a new sample record, given only a name and an associated {@link DataSet} record.
   *
   * @param name sample name
   * @param dataSet DataSet record
   * @return a new Sample record
   */
  @Override
  public Sample createSample(String name, Subject subject, DataSet dataSet) {
    
    Sample sample = new Sample();
    sample.setName(name);
    sample.setSubjectId(subject.getId());
    sample.setDataSetId(dataSet.getId());
    sample.setHistology(getSampleHistologyFromSubject(subject));
    sample.setTissue(getSampleTissueFromSubject(subject));
    sample.setSampleType(getSampleTypeFromSubject(subject));
    setSampleAttributes(sample, subject);
    
    sample = sampleRepository.insert(sample);
    logger.info(String.format("Created new Sample record: %s", sample.toString()));
    
    List<String> sampleIds = new ArrayList<>(subject.getSampleIds());
    sampleIds.add(sample.getId());
    subject.setSampleIds(sampleIds);
    subjectRepository.update(subject);

    //TODO: Do we need to add sample Ids to data set record here?
    
    return sample;
  }

  /**
   * Creates a new sample record, given only a name and an associated {@link DataSet} record.
   *
   * @param dataSet DataSet record
   * @return a new Sample record
   */
  public Sample createSample(Subject subject, DataSet dataSet) {
    return createSample(subject.getName(), subject, dataSet);
  }

  /**
   * Checks the {@link Subject} record to see if it contains sample type information and returns it
   *   if so.
   * 
   * @param subject
   * @return
   */
  protected String getSampleTypeFromSubject(Subject subject){
    if (subject.hasAttribute("sample.type")) return subject.getAttribute("sample.type");
    if (subject.hasAttribute("sampleType")) return subject.getAttribute("sampleType");
    return null;
  }

  /**
   * Checks the {@link Subject} record to see if it contains sample type information and returns it
   *   if so.
   *
   * @param subject
   * @return
   */
  protected String getSampleHistologyFromSubject(Subject subject){
    if (subject.hasAttribute("sample.histology")) return subject.getAttribute("sample.histology");
    if (subject.hasAttribute("histology")) return subject.getAttribute("histology");
    return null;
  }

  /**
   * Checks the {@link Subject} record to see if it contains tissue type information and returns it
   *   if so.
   *
   * @param subject
   * @return
   */
  protected String getSampleTissueFromSubject(Subject subject){
    if (subject.hasAttribute("sample.tissue")) return subject.getAttribute("sample.tissue");
    if (subject.hasAttribute("tissue")) return subject.getAttribute("tissue");
    if (subject.hasAttribute("sample.primarySite")) return subject.getAttribute("sample.primarySite");
    if (subject.hasAttribute("primarySite")) return subject.getAttribute("primarySite");
    if (subject.hasAttribute("sample.primary_site")) return subject.getAttribute("sample.primary_site");
    if (subject.hasAttribute("primary_site")) return subject.getAttribute("primary_site");
    return null;
  }
  
  private String getSampleAttribute(String key, Subject subject, DataSet dataSet){
    if (subject.hasAttribute(key)){
      return subject.getAttribute(key);
    } else if (dataSet.hasParameter("default." + key)){
      return dataSet.getParameter("default." + key);
    } else {
      return "n/a";
    }
  }
  
  private void setSampleAttributes(Sample sample, Subject subject){
    for (Entry<String,String> entry: subject.getAttributes().entrySet()){
      if (entry.getKey().startsWith(Attributes.SAMPLE_ATTRIBUTE_PREFIX)){
        String key = entry.getKey().replace(Attributes.SAMPLE_ATTRIBUTE_PREFIX, "");
        if (!sample.hasAttribute(key)) sample.addAttribute(key, entry.getValue());
      }
    }
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
    return sampleRepository.findByNameAndDataSetId(name, dataSet.getId());
  }

  /**
   * {@link DataSetSupport#findSample(String, Subject)}
   */
  @Override
  public Optional<Sample> findSample(String name, Subject subject) {
    return sampleRepository.findByNameAndSubjectId(name, subject.getId());
  }

  /**
   * {@link DataSetSupport#findOrCreateSample(String, DataSet)}
   */
  @Override
  public Optional<Sample> findOrCreateSample(String name, DataSet dataSet){
    Optional<Sample> optional = this.findSample(name, dataSet);
    if (optional.isPresent()) return optional;
    Optional<Subject> subjectOptional = getSubjectRepository().findByName(name);
    if (subjectOptional.isPresent()){
      Sample sample = createSample(name, subjectOptional.get(), dataSet);
      return Optional.of(sample);
    } else {
      return Optional.empty();
    }
  }

  /**
   * {@link DataSetSupport#findOrCreateSample(String, Subject, DataSet)}
   */
  @Override
  public Optional<Sample> findOrCreateSample(String name, Subject subject, DataSet dataSet){
    Optional<Sample> optional = this.findSample(name, subject);
    if (optional.isPresent()) return optional;
    return Optional.ofNullable(createSample(name, subject, dataSet));
    
  }

  protected SubjectRepository getSubjectRepository() {
    return subjectRepository;
  }

  protected SampleRepository getSampleRepository() {
    return sampleRepository;
  }

}
