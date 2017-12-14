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
import java.io.Serializable;

/**
 * Default processor for {@link Gene} records originating from Entrez Gene flat files.
 *
 * @author woemler
 * @since 0.5.0
 */
@DataTypes(value = "entrez_gene", description = "Entrez Gene records")
public class EntrezGeneInfoProcessor<T extends Gene<ID>, ID extends Serializable> 
    extends GenericRecordProcessor<T> {

    public EntrezGeneInfoProcessor(Class<T> model, GeneRepository<T, ID> geneRepository) {
        this.setReader(new EntrezGeneInfoReader<>(model));
        this.setValidator(new GeneValidator());
        this.setWriter(new RepositoryRecordWriter<>(geneRepository));
        this.setModel(model);
    }

}
