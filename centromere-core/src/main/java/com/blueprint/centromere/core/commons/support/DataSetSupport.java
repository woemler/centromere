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
import java.util.Optional;

/**
 * Generic support API for working with specific data set metadata creation or fetching.
 * 
 * @author woemler
 */
public interface DataSetSupport {
  
  //Sample fetchOrCreateSample(String name, Subject subject, DataSet dataSet);

  /**
   * Creates a new sample record, given a name, {@link Subject} record, and an associated 
   *   {@link DataSet} record.
   * 
   * @param name sample name
   * @param dataSet DataSet record
   * @return a new Sample record
   */
  Sample createSample(String name, Subject subject, DataSet dataSet);

  /**
   * Finds and returns a {@link Sample} record for the given name and {@link DataSet} record, 
   *   if one exists.
   * 
   * @param name sample name
   * @param dataSet DataSet record
   * @return an optional sample record
   */
  Optional<Sample> findSample(String name, DataSet dataSet);

  /**
   * Finds and returns a {@link Sample} record for the given name and {@link Subject} record, 
   *   if one exists.
   * 
   * @param name
   * @param subject
   * @return
   */
  Optional<Sample> findSample(String name, Subject subject);

  /**
   * Checks to see if a {@link Sample} with the given name exists in the {@link DataSet} scope.  If 
   *   one does, it is returned, otherwise, a new record is created and returned.  If no {@link Subject}
   *   can be found to associate with the sample, an empty {@link Optional} is returned.
   * 
   * @param name
   * @param dataSet
   * @return
   */
  Optional<Sample> findOrCreateSample(String name, DataSet dataSet);

  /**
   * Checks to see if a {@link Sample} with the given name exists in the {@link DataSet} scope.  If 
   *   one does, it is returned, otherwise, a new record is created and returned.  
   *
   * @param name
   * @param subject
   * @param dataSet
   * @return
   */
  Optional<Sample> findOrCreateSample(String name, Subject subject, DataSet dataSet);

}
