/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.cli.dataimport.processor.impl;

import com.blueprint.centromere.cli.dataimport.DataTypes;
import com.blueprint.centromere.cli.dataimport.processor.GenericRecordProcessor;
import com.blueprint.centromere.cli.dataimport.processor.impl.validator.GeneValidator;
import com.blueprint.centromere.cli.dataimport.reader.impl.EntrezGeneInfoReader;
import com.blueprint.centromere.cli.dataimport.writer.RepositoryRecordWriter;
import com.blueprint.centromere.core.model.impl.Gene;
import com.blueprint.centromere.core.repository.impl.GeneRepository;
import org.springframework.stereotype.Component;

/**
 * Default processor for {@link Gene} records originating from Entrez Gene flat files.
 *
 * @author woemler
 * @since 0.5.0
 */
@Component
@DataTypes(value = "entrez_gene", description = "Entrez Gene records")
public class EntrezGeneInfoProcessor extends GenericRecordProcessor<Gene> {

    public EntrezGeneInfoProcessor(GeneRepository geneRepository) {
        this.setReader(new EntrezGeneInfoReader());
        this.setValidator(new GeneValidator());
        this.setWriter(new RepositoryRecordWriter<>(geneRepository));
        this.setModel(Gene.class);
    }

}
