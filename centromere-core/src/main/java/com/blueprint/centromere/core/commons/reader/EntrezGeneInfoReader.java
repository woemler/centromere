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

package com.blueprint.centromere.core.commons.reader;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.dataimport.DataImportException;
import com.blueprint.centromere.core.dataimport.reader.StandardRecordFileReader;
import com.blueprint.centromere.core.model.ModelSupport;

/**
 * @author woemler
 */
public class EntrezGeneInfoReader extends StandardRecordFileReader<Gene>
		implements ModelSupport<Gene> {

	private Class<Gene> model = Gene.class;

	protected Gene getRecordFromLine(String line) {
		String[] bits = line.split("\\t");
		Gene gene;
		try {
			gene = new Gene();
		} catch (Exception e){
			throw new DataImportException(String.format("Cannot create instance of model class: %s", model.getName()), e);
		}
		gene.setTaxId(Integer.parseInt(bits[0]));
		gene.setPrimaryReferenceId(bits[1]);
		gene.setPrimaryGeneSymbol(bits[2]);
		for (String alias: bits[4].split("\\|")){
			if (!alias.replaceAll("-", "").equals("")) gene.addAlias(alias);
		}
		for (String ref : bits[5].split("\\|")) {
			String[] r = ref.split(":");
			if (!r[0].replaceAll("-", "").equals("")) gene.addExternalReference(r[0], r[r.length - 1]);
		}
		gene.setChromosome(bits[6]);
		gene.setChromosomeLocation(bits[7]);
		gene.setDescription(bits[8]);
		gene.setGeneType(bits[9]);
		gene.setReferenceSource("NCBI");
		return gene;
	}

	@Override 
	protected boolean isSkippableLine(String line) {
		return line.trim().equals("") || line.startsWith("#");
	}

	@Override
	public Class<Gene> getModel() {
		return model;
	}

	@Override 
	public void setModel(Class<Gene> model) {
		this.model = model;
	}
}
