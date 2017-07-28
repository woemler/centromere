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

import com.blueprint.centromere.core.commons.model.Mutation;
import com.blueprint.centromere.core.commons.reader.TcgaMafReader;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.support.TcgaSupport;
import com.blueprint.centromere.core.config.DataImportProperties;
import com.blueprint.centromere.core.config.DatabaseProperties;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.importer.MongoImportTempFileImporter;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.MongoImportTempFileWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes(value = { "tcga_maf_mutation" }, description = "MAF mutation records from the TCGA")
@Component
public class MafMutationProcessor extends GenericRecordProcessor<Mutation> {

    @Autowired
    public MafMutationProcessor(
        GeneRepository geneRepository,
        MongoOperations mongoOperations,
        TcgaSupport tcgaSupport,
        DataImportProperties dataImportProperties,
        DatabaseProperties databaseProperties
    ) {
      this.setReader(new TcgaMafReader(geneRepository, tcgaSupport, dataImportProperties));
      //this.setValidator(new()); // TODO: mutation validator
      this.setWriter(new MongoImportTempFileWriter<>(dataImportProperties, mongoOperations));
      this.setImporter(new MongoImportTempFileImporter<>(Mutation.class, databaseProperties));
      this.setModel(Mutation.class);
    }
}
