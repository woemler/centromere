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

package org.oncoblocks.centromere.mongodb.commons.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.oncoblocks.centromere.core.commons.models.Gene;
import org.oncoblocks.centromere.core.model.Alias;
import org.oncoblocks.centromere.core.model.Ignored;
import org.oncoblocks.centromere.core.model.ModelAttributes;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * @author woemler
 */
@ModelAttributes(uri = "genes")
@Document(collection = "genes")
public class MongoGene extends Gene<String> {
	
	@Id 
	@Alias("geneId")
	private String id;
	
	@Alias("alias")
	private List<String> aliases;
	
	@Alias(value = "references.\\w+", regex = true)
	@Ignored
	private Map<String,String> externalReferences;
	
	@Alias(value = "attributes.\\w+", regex = true)
	@Ignored
	private Map<String,Object> attributes;

	
	@Override 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override 
	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	@Override 
	public Map<String, String> getExternalReferenceMap() {
		return externalReferences;
	}

	public void setExternalReferences(
			Map<String, String> externalReferences) {
		this.externalReferences = externalReferences;
	}

	@Override 
	@JsonIgnore
	public Map<String, Object> getAttributeMap() {
		return attributes;
	}

	@Override 
	public boolean hasExternalReference(String name) {
		return externalReferences.containsKey(name);
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

	public void setAttributes(
			Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override 
	public void addAlias(String alias) {
		if (aliases == null){
			aliases = new ArrayList<>();
		}
		if (!aliases.contains(alias)){
			aliases.add(alias);
		}
	}

	@Override 
	public void addAliases(Collection<String> aliases) {
		if (this.aliases == null){
			this.aliases = new ArrayList<>();
		} 
		this.aliases.addAll(aliases);
	}

	@Override 
	public void addExternalReference(String name, String value) {
		if (externalReferences == null){
			externalReferences = new HashMap<>();
		}
		externalReferences.put(name, value);
	}

	@Override 
	public void addAttribute(String name, Object value) {
		if (attributes == null){
			attributes = new HashMap<>();
		}
		attributes.put(name, value);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
