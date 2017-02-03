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

package com.blueprint.centromere.core.commons.processors;

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.commons.readers.EntrezGeneInfoReader;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.validators.GeneValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.GenericRecordProcessor;
import com.blueprint.centromere.core.dataimport.RepositoryRecordWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default processor for {@link Gene} records originating from Entrez Gene flat files.
 *
 * @author woemler
 * @since 0.5.0
 */
@DataTypes({ "entrez_gene" })
@Component
public class EntrezGeneInfoProcessor extends GenericRecordProcessor<Gene> {

    @Autowired
    public EntrezGeneInfoProcessor(GeneRepository geneRepository) {
        this.setReader(new EntrezGeneInfoReader());
        this.setValidator(new GeneValidator());
        this.setWriter(new RepositoryRecordWriter<>(geneRepository));
        this.setModel(Gene.class);
    }

}
