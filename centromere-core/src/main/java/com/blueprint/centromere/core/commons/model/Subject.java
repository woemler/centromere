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

package com.blueprint.centromere.core.commons.model;

import com.blueprint.centromere.core.commons.support.ManagedTerm;
import com.blueprint.centromere.core.model.AbstractModel;
import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Linked;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model representation of an individual organism, such as a clinical subject or model animal.  A 
 *   single Subject can be assumed to have one or more {@link Sample} entities describing biological
 *   material taken for experimentation.
 * 
 * @author woemler
 */
@Document
@Data
public class Subject extends AbstractModel implements Attributes {
	
	@Indexed(unique = true) @ManagedTerm private String name;
	private String species;
	private String gender;
	@Ignored private String notes;
	@ManagedTerm @Indexed private List<String> aliases = new ArrayList<>();
	@ManagedTerm private Map<String, String> attributes = new HashMap<>();
	@Linked(model = Sample.class, rel = "samples") private List<String> sampleIds = new ArrayList<>();

  @Override
  public Map<String, String> getAttributes() {
    return attributes;
  }

  @Override
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	@Override
	public void addAttributes(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
	}

	@Override
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	@Override
	public String getAttribute(String name) {
		return attributes.containsKey(name) ? attributes.get(name) : null;
	}

	public void addAlias(String alias){
		if (!aliases.contains(alias)) this.aliases.add(alias);
	}

  	
	public static class Attributes {
	  
	  public static final String SAMPLE_ATTRIBUTE_PREFIX = "sample-";
	  
	  public static final String SAMPLE_HISTOLOGY = SAMPLE_ATTRIBUTE_PREFIX + "histology";
    public static final String SAMPLE_TISSUE = SAMPLE_ATTRIBUTE_PREFIX + "tissue";
    public static final String SAMPLE_TYPE = SAMPLE_ATTRIBUTE_PREFIX + "type";
    
    public static final String AGE = "age";
	  
  } 
	
}
