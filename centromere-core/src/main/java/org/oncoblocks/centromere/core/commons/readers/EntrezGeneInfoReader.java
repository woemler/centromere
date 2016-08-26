/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package org.oncoblocks.centromere.core.commons.readers;

import org.oncoblocks.centromere.core.commons.models.Gene;
import org.oncoblocks.centromere.core.dataimport.DataImportException;
import org.oncoblocks.centromere.core.dataimport.StandardRecordFileReader;
import org.oncoblocks.centromere.core.model.ModelSupport;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
public class EntrezGeneInfoReader<T extends Gene<?>> extends StandardRecordFileReader<T>
		implements ModelSupport<T> {

	private Class<T> model;

	public EntrezGeneInfoReader(Class<T> model) {
		this.model = model;
	}
	
	@SuppressWarnings("unchecked")
	protected T getModelInstance() throws Exception{
		Assert.notNull(model, "Model class type must not be null.");
		return model.newInstance();
	}

	protected T getRecordFromLine(String line) throws DataImportException {
		String[] bits = line.split("\\t");
		T gene;
		try {
			gene = getModelInstance();
		} catch (Exception e){
			e.printStackTrace();
			throw new DataImportException(String.format("Cannot create instance of model class: %s", model.getName()));
		}
		gene.setTaxId(Integer.parseInt(bits[0]));
		gene.setPrimaryReferenceId(bits[1]);
		gene.setPrimaryGeneSymbol(bits[2]);
		for (String alias: bits[4].split("\\|")){
			gene.addAlias(alias);
		}
		//Map<String, String> dbXrefs = new HashMap<>();
		for (String ref : bits[5].split("\\|")) {
			String[] r = ref.split(":");
			gene.addExternalReference(r[0], r[r.length - 1]);
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
	public Class<T> getModel() {
		return model;
	}

	@Override 
	public void setModel(Class<T> model) {
		this.model = model;
	}
}
