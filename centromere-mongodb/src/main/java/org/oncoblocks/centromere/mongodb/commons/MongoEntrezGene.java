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

package org.oncoblocks.centromere.mongodb.commons;

import org.oncoblocks.centromere.core.commons.EntrezGene;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author woemler
 */
@Document(collection = "entrez_gene")
public class MongoEntrezGene extends EntrezGene<String> {
	
	@Id private String id;
	private List<String> geneSymbolAliases;
	private Map<String,String> databaseCrossReferences;
	private Map<String,Object> attributes;

	@Override public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override public List<String> getGeneSymbolAliases() {
		return geneSymbolAliases;
	}

	public void setGeneSymbolAliases(List<String> geneSymbolAliases) {
		this.geneSymbolAliases = geneSymbolAliases;
	}

	@Override public Map<String, String> getDatabaseCrossReferences() {
		return databaseCrossReferences;
	}

	public void setDatabaseCrossReferences(
			Map<String, String> databaseCrossReferences) {
		this.databaseCrossReferences = databaseCrossReferences;
	}

	@Override public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(
			Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override public void addGeneSymbolAlias(String alias) {
		if (geneSymbolAliases == null){
			geneSymbolAliases = new ArrayList<>();
		}
		if (!geneSymbolAliases.contains(alias)){
			geneSymbolAliases.add(alias);
		}
	}

	@Override public void addDatabaseCrossReference(String name, String value) {
		if (databaseCrossReferences == null){
			databaseCrossReferences = new HashMap<>();
		}
		databaseCrossReferences.put(name, value);
	}

	@Override public void addAttribute(String name, Object value) {
		if (attributes == null){
			attributes = new HashMap<>();
		}
		attributes.put(name, value);
	}
}
