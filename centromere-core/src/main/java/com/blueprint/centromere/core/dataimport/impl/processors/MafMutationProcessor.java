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

package com.blueprint.centromere.core.dataimport.impl.processors;

import com.blueprint.centromere.core.dataimport.GenericRecordProcessor;
import com.blueprint.centromere.core.model.impl.Mutation;
import com.blueprint.centromere.core.dataimport.impl.readers.MafReader;
import com.blueprint.centromere.core.dataimport.impl.repositories.GeneRepository;
import com.blueprint.centromere.core.dataimport.impl.support.TcgaSupport;
import com.blueprint.centromere.core.dataimport.impl.validators.GeneExpressionValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.impl.importers.MongoImportTempFileImporter;
import com.blueprint.centromere.core.dataimport.impl.writers.MongoImportTempFileWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes({ "maf_mutation" })
@Component
public class MafMutationProcessor extends GenericRecordProcessor<Mutation> {

    @Autowired
    public MafMutationProcessor(
        TcgaSupport tcgaSupport,
        GeneRepository geneRepository,
        MongoOperations mongoOperations,
        Environment environment
    ) {

      this.setEnvironment(environment);

      MafReader reader = new MafReader(geneRepository, tcgaSupport);
      reader.setEnvironment(environment);
      this.setReader(reader);
      
      this.setValidator(new GeneExpressionValidator());

      MongoImportTempFileWriter<Mutation> writer = new MongoImportTempFileWriter<>(mongoOperations);
      writer.setEnvironment(environment);

      MongoImportTempFileImporter<Mutation> importer = new MongoImportTempFileImporter<>(Mutation.class, environment);
      this.setImporter(importer);

      this.setModel(Mutation.class);
    }
}
