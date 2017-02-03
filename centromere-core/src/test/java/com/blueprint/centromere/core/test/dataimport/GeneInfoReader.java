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

package com.blueprint.centromere.core.test.dataimport;

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.dataimport.AbstractRecordFileReader;
import com.blueprint.centromere.core.dataimport.DataImportException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author woemler
 */
@Component
public class GeneInfoReader extends AbstractRecordFileReader<Gene> {

	public GeneInfoReader() { }

	@Override
	public Gene readRecord() throws DataImportException {
		Gene gene = null;
		String line;
		try {
			boolean flag = true;
			while(flag) {
				line = this.getReader().readLine();
				if (line == null || !line.startsWith("#Format: tax_id GeneID")) {
					flag = false;
					if (line != null && !line.equals("")){
						gene = getRecordFromLine(line);
					}
				}

			}
		} catch (IOException e){
			e.printStackTrace();
		}
		return gene;

	}

	private Gene getRecordFromLine(String line){
		String[] bits = line.split("\\t");
		Gene gene = new Gene();
		gene.setTaxId(Integer.parseInt(bits[0]));
		gene.setPrimaryReferenceId(bits[1]);
		gene.setPrimaryGeneSymbol(bits[2]);
		gene.setAliases(Arrays.asList(bits[3].split("\\|")));
		Map<String,Object> dbXrefs = new HashMap<>();
		for (String ref: bits[4].split("\\|")){
			String[] r = ref.split(":");
			dbXrefs.put(r[0], r[r.length-1]);
		}
		gene.setChromosome(bits[5]);
		gene.setGeneType(bits[8]);
		return gene;
	}

}
