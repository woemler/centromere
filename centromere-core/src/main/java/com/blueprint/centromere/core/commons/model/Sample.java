/*
 * Copyright 2018 the original author or authors
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
import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Model for representing biological samples in analyses.  It is presumed that a single sample can 
 *   be used in one or more analyses and be a part of one or more {@link DataSet} instances.
 * 
 * @author woemler
 */
@Data
public abstract class Sample<ID extends Serializable> implements Model<ID>, Attributes {

  @Indexed(unique = true)
  @NotEmpty
  private String sampleId;
  
  @Indexed
  @ManagedTerm
  private String name;
  
  @Indexed
  private String subjectId;
	
	@ManagedTerm
	private String sampleType;
	
	@ManagedTerm
  private String species;
	
  private String gender;
	
	@ManagedTerm 
  private String tissue;
	
	@ManagedTerm 
  private String histology;
	
	@Ignored 
  private String notes;
	
	@ManagedTerm 
  private Map<String, String> attributes = new HashMap<>();
	
	@Indexed
  private List<String> aliases = new ArrayList<>();

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
		return attributes.getOrDefault(name, null);
	}
	
	public void addAlias(String alias){
    if (!aliases.contains(alias)) aliases.add(alias);
  }
  
  public void addAliases(Collection<String> aliases){
	  for (String alias: aliases){
	    if (!this.aliases.contains(alias)) this.aliases.add(alias);
    }
  }

}
