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

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.commons.reader.EntrezGeneInfoReader;
import com.blueprint.centromere.core.commons.repository.GeneRepository;
import com.blueprint.centromere.core.commons.validator.GeneValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.writer.RepositoryRecordWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default processor for {@link Gene} records originating from Entrez Gene flat files.
 *
 * @author woemler
 * @since 0.5.0
 */
@DataTypes("entrez_gene")
@Component
public class EntrezGeneInfoProcessor extends GenericRecordProcessor<Gene> {

    @Autowired
    public EntrezGeneInfoProcessor(GeneRepository geneRepository) {
        
        this.setReader(new EntrezGeneInfoReader());
        
        this.setValidator(new GeneValidator());
        
        RepositoryRecordWriter<Gene, String> writer = new RepositoryRecordWriter<>();
        writer.setRepository(geneRepository);
        writer.setBatchSize(1000);
        this.setWriter(writer);
        
        this.setModel(Gene.class);
    }

}
