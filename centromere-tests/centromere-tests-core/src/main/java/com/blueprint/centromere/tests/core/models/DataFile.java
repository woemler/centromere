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

package com.blueprint.centromere.tests.core.models;

import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
public abstract class DataFile<ID extends Serializable> implements Model<ID>, Attributes {
  
  private String filePath;
  private String dataType;
	private String model;
	private String checksum;
	private Date dateCreated;
	private Date dateUpdated;
	
	@Linked(model = DataSet.class, rel = "dataSet", field = "dataSetId") 
  private ID dataSetId;
	
	private Map<String, String> attributes = new HashMap<>();

	@Transient
  @JsonIgnore
  public Class<?> getModelType() throws ClassNotFoundException {
    return Class.forName(model);
  }

  public void setModel(Class<?> modelType){
	  this.model = modelType.getName();
  }
  
	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
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
		return attributes.getOrDefault(name, null);
	}

}
