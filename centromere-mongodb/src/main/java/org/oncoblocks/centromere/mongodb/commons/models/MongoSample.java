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

import org.oncoblocks.centromere.core.commons.models.Sample;
import org.oncoblocks.centromere.core.commons.models.Subject;
import org.oncoblocks.centromere.core.model.Alias;
import org.oncoblocks.centromere.core.model.ForeignKey;
import org.oncoblocks.centromere.core.model.ModelAttributes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.*;

/**
 * @author woemler
 */
@ModelAttributes(uri = "samples")
@Document(collection = "samples")
public class MongoSample extends Sample<String> {
	
	@Id 
	@Alias("sampleId")
	private String id;
	
	@ForeignKey(model = MongoSubject.class, relationship = ForeignKey.Relationship.MANY_TO_ONE, 
			rel = "subject", field = "id")
	private String subjectId;

	@Alias(value = "attributes.\\w+", regex = true)
	private Map<String,Object> attributes = new HashMap<>();
	
	@Alias("alias")
	private List<String> aliases = new ArrayList<>();


	@Override 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getSubjectId(){
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override 
	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
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

	@Override 
	public Map<String, Object> getAttributeMap() {
		return attributes;
	}

	@Override 
	public void addAlias(String alias) {
		if (!aliases.contains(alias)){
			aliases.add(alias);
		}
	}

	@Override 
	public void addAliases(Collection<String> aliases) {
		for (String alias: aliases){
			this.addAlias(alias);
		}
	}

	@Override 
	public <S extends Subject<?>> void setSubjectMetadata(S subject) {
		this.subjectId = ((MongoSubject) subject).getId();	
	}
}
