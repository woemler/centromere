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

package org.oncoblocks.centromere.core.commons;

import org.oncoblocks.centromere.core.dataimport.AbstractRecordFileReader;
import org.oncoblocks.centromere.core.dataimport.DataImportException;
import org.oncoblocks.centromere.core.model.ModelSupport;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author woemler
 */
public class EntrezGeneInfoReader<T extends EntrezGene<?>> extends AbstractRecordFileReader<T>
		implements ModelSupport<T> {

	private Class<T> model;

	public EntrezGeneInfoReader(Class<T> model) {
		this.model = model;
	}

	public T readRecord() throws DataImportException {
		try {
			String line = this.getReader().readLine();
			while (line != null) {
				if (!line.trim().equals("") && !line.startsWith("#")) {
					return getRecordFromLine(line);
				}
				line = this.getReader().readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		gene.setEntrezGeneId(Long.parseLong(bits[1]));
		gene.setPrimaryGeneSymbol(bits[2]);
		gene.setGeneSymbolAliases(new HashSet<>(Arrays.asList(bits[4].split("\\|"))));
		Map<String, String> dbXrefs = new HashMap<>();
		for (String ref : bits[5].split("\\|")) {
			String[] r = ref.split(":");
			dbXrefs.put(r[0], r[r.length - 1]);
		}
		gene.setDatabaseCrossReferences(dbXrefs);
		gene.setChromosome(bits[6]);
		gene.setChromosomeLocation(bits[7]);
		gene.setDescription(bits[8]);
		gene.setGeneType(bits[9]);
		return gene;
	}

	public Class<T> getModel() {
		return model;
	}

}
