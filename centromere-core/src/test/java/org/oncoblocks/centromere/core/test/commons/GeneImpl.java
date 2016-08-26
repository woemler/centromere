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

package org.oncoblocks.centromere.core.test.commons;

import org.oncoblocks.centromere.core.commons.models.Gene;

import java.util.*;

/**
 * @author woemler
 */
public class GeneImpl extends Gene<Long> {
	
	private Long id;
	private List<String> geneSymbolAliases = new ArrayList<>();
	private Map<String, String> databaseCrossReferences = new HashMap<>();
	private Map<String, Object> attributes = new HashMap<>();

	@Override 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override 
	public Collection<String> getAliases() {
		return geneSymbolAliases;
	}

	@Override 
	public void addAlias(String alias) {
		geneSymbolAliases.add(alias);
	}

	@Override 
	public boolean hasExternalReference(String name) {
		return databaseCrossReferences.containsKey(name);
	}

	@Override 
	public void addAliases(Collection<String> aliases) {
		geneSymbolAliases.addAll(aliases);
	}

	@Override 
	public Map<String, String> getExternalReferenceMap() {
		return databaseCrossReferences;
	}

	@Override 
	public void addExternalReference(String name, String value) {
		databaseCrossReferences.put(name, value);
	}

	@Override 
	public Map<String, Object> getAttributeMap() {
		return attributes;
	}

	@Override 
	public void addAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override 
	public void addAttributes(Map<String, Object> attributes) {
		attributes.putAll(attributes);
	}

	@Override 
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	@Override 
	public Object getAttribute(String name) {
		return attributes.containsKey(name) ? attributes.get(name) : null;
	}
}
