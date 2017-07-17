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

package com.blueprint.centromere.core.commons.processor;

import com.blueprint.centromere.core.commons.model.Term;
import com.blueprint.centromere.core.commons.repository.TermRepository;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.exception.DataImportException;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.processor.RecordProcessor;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This processor will extract all values from {@link Model} fields with {@link com.blueprint.centromere.core.commons.support.ManagedTerm}
 *   annotations and create a dictionary repository that indexes them.
 * 
 * @author woemler
 */
@DataTypes(value = { "managed_terms", "terms" }, description = "Builds a dictionary of managed terms," 
    + " extracting field values from models with @ManagedTerm annotations.")
@Component
public class ManagedTermProcessor extends GenericRecordProcessor<Term> {
  
  private static final Logger logger = LoggerFactory.getLogger(ManagedTermProcessor.class);
  
  private TermRepository termRepository;

  public ManagedTermProcessor(TermRepository termRepository) {
    this.termRepository = termRepository;
    this.setModel(Term.class);
  }

  /**
   * Performs configuration steps prior to each execution of {@link #run()}.  Assigns
   * options and metadata objects to the individual processing components that are expecting them.
   */
  @Override
  public void doBefore() throws DataImportException {
    logger.info("Clearing existing Term records.");
    termRepository.deleteAll();
  }

  /**
   * To be executed after the main component method is called for the last time.  Handles job
   * cleanup and association of metadata records.
   */
  @Override
  public void doAfter() throws DataImportException {
    
  }

  /**
   * Executes if the {@link #run()} method fails to execute properly, in place of the
   * {@link #doAfter()} method.
   */
  @Override
  public void doOnFailure() {
    
  }

  /**
   * {@link RecordProcessor#run()}
   */
  @Override
  public void run() throws DataImportException {
    for (Class<?> model: this.getRegistry().getRegisteredModels()){
      if (Term.modelHasManagedTerms(model)){
        ModelRepository<?,?> repository = this.getRegistry().getRepositoryByModel(model);
        for (Model<?> record: repository.findAll()){
          try {
            List<Term> terms = Term.getModelTerms(record);
            termRepository.saveTerms(terms);
          } catch (IllegalAccessException e){
            throw new DataImportException(e);
          }
        }
      }
    }
  }

}
