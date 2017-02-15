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

import com.blueprint.centromere.core.commons.models.GeneExpression;
import com.blueprint.centromere.core.commons.readers.TcgaRnaSeqGeneExpressionFileReader;
import com.blueprint.centromere.core.commons.repositories.GeneExpressionRepository;
import com.blueprint.centromere.core.commons.repositories.GeneRepository;
import com.blueprint.centromere.core.commons.repositories.SampleRepository;
import com.blueprint.centromere.core.commons.repositories.SubjectRepository;
import com.blueprint.centromere.core.commons.validators.GeneExpressionValidator;
import com.blueprint.centromere.core.dataimport.DataTypes;
import com.blueprint.centromere.core.dataimport.DelimtedTextFileWriter;
import com.blueprint.centromere.core.dataimport.MySqlImportFileImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author woemler
 */
@DataTypes({ "tcga_gene_expression" })
@Component
public class TcgaGeneExpressionProcessor extends CommonsDataProcessor<GeneExpression> {

    @Autowired
    public TcgaGeneExpressionProcessor(
        SampleRepository sampleRepository,
        SubjectRepository subjectRepository,
        GeneRepository geneRepository,
        GeneExpressionRepository geneExpressionRepository,
        Environment environment
    ) {
      
      this.setEnvironment(environment);
      
      this.setReader(new TcgaRnaSeqGeneExpressionFileReader(sampleRepository, subjectRepository, 
          geneRepository, environment));
      
      this.setValidator(new GeneExpressionValidator());
      
      DelimtedTextFileWriter writer = new DelimtedTextFileWriter<>();
      writer.setEnvironment(environment);
      this.setWriter(writer);
      
      // TODO: generic factory for importer creation to support MySQL/MongoDB/etc
      MySqlImportFileImporter importer = new MySqlImportFileImporter(
          environment.getRequiredProperty("centromere.db.host"),
          environment.getRequiredProperty("centromere.db.user"),
          environment.getRequiredProperty("centromere.db.password"),
          environment.getRequiredProperty("centromere.db.name")
      );
      importer.setEnvironment(environment);
      this.setImporter(importer);
      
      //this.setWriter(new RepositoryRecordWriter<>(geneExpressionRepository, 1000));
      this.setModel(GeneExpression.class);
    }
}
