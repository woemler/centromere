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

package com.blueprint.centromere.core.dataimport;

import com.blueprint.centromere.core.commons.model.DataFile;
import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.model.Sample;
import com.blueprint.centromere.core.config.ApplicationOptions;
import org.springframework.util.StringUtils;

/**
 * API for evaluating various data import parameters.
 *
 * @author woemler
 * @since 0.5.0
 */
public interface ImportOptions extends ApplicationOptions {

  /**
   * Returns true if invalid records should be skipped, rather than raising an exception.
   *
   * @return
   */
  boolean skipInvalidRecords();

  /**
   * Tests whether a record is blatantly invalid.  This should not be confused with testing that a
   *   record is valid, which should be handled by {@link org.springframework.validation.Validator}
   *   classes.
   *
   * @param record
   * @return
   */
  default boolean isInvalidRecord(Object record)  {
    return record == null;
  }

  /**
   * Returns true if invalid samples should be skipped, rather than raising an exception.
   *
   * @return
   */
  boolean skipInvalidSamples();

  /**
   * Tests whether a sample is blatantly invalid.  This should not be confused with testing that a
   *   sample is valid, which should be handled by {@link org.springframework.validation.Validator}
   *   classes.
   *
   * @param sample
   * @return
   */
  default boolean isInvalidSample(Sample sample)  {
    return sample == null || StringUtils.isEmpty(sample.getName()) || sample.getSubjectId() == null;
  }

  /**
   * Returns true if invalid genes should be skipped, rather than raising an exception.
   *
   * @return
   */
  boolean skipInvalidGenes();

  /**
   * Tests whether a gene is blatantly invalid.  This should not be confused with testing that a
   *   gene is valid, which should be handled by {@link org.springframework.validation.Validator}
   *   classes.
   *
   * @param gene
   * @return
   */
  default boolean isInvalidGene(Gene gene)  {
    return gene == null || StringUtils.isEmpty(gene.getPrimaryGeneSymbol())
        || StringUtils.isEmpty(gene.getPrimaryReferenceId());
  }

  /**
   * Returns true if invalid files should be skipped, rather than raising an exception.
   *
   * @return
   */
  boolean skipInvalidFiles();

  /**
   * Returns true if files that exist in the {@link DataFile},
   *  repository should be skipped, rather than raising an exception.
   *
   * @return
   */
  boolean skipExistingFiles();

  /**
   * When true, will overwrite data files that already exist in the database.
   *
   * @return
   */
  boolean overwriteExistingFiles();

  /**
   * When true, will overwrite data sets that already exist in the database.
   *
   * @return
   */
  boolean overwriteExistingDataSets();

  /**
   * Returns the directory path to be used for storing temporary files.
   *
   * @return
   */
  String getTempFilePath();

}
