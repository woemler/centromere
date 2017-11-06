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
import com.blueprint.centromere.core.model.Ignored;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for representing biological samples in analyses.  It is presumed that a single sample can 
 *   be used in one or more analyses and be a part of one or more {@link DataSet} instances.
 * 
 * @author woemler
 */
@Document
@Data
public class Sample extends AbstractMongoModel implements Attributes {

	@Indexed(unique = true) 
  @ManagedTerm 
  private String name;
	
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

}
