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

package com.blueprint.centromere.core.model.impl;

import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model representation of an annotated set of data, which may be compromised of multiple data types
 *   and samples.  
 * 
 * @author woemler
 */
@Document(collection = "data_sets")
@Data
@AccessType(Type.PROPERTY)
public class DataSet implements Model<String>, Attributes {
  
  private String dataSetId;
  
  @Indexed(unique = true)
  @NotEmpty
  private String name;
	
	@Indexed 
  @NotEmpty
  private String source;
	
	@Ignored 
  private String version;
	
	@Ignored 
  private String description;
	
	@Linked(model = DataSource.class, rel = "dataSources", field = "dataSourceId") 
  private List<String> dataSourceIds = new ArrayList<>();
  
	@Linked(model = Sample.class, rel = "samples", field = "sampleId") 
  private List<String> sampleIds = new ArrayList<>();
	
  private Map<String, String> attributes = new HashMap<>();
	
  @Ignored 
  private Map<String, String> parameters = new HashMap<>();

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
		return attributes.containsKey(name) ? attributes.get(name) : null;
	}

  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }

  public void addParameters(Map<String, String> parameters) {
    this.parameters.putAll(parameters);
  }

  public boolean hasParameter(String name) {
    return parameters.containsKey(name);
  }

  public String getParameter(String name) {
    return parameters.containsKey(name) ? parameters.get(name) : null;
  }

  public void addSampleId(String sampleId){
    if (!sampleIds.contains(sampleId)) sampleIds.add(sampleId);
  }
  
  public void addSampleIds(Collection<String> sampleIds){
    for (String sampleId: sampleIds){
      addSampleId(sampleId);
    }
  }
  
  public void adddataSourceId(String dataSourceId){
    if (!dataSourceIds.contains(dataSourceId)) dataSourceIds.add(dataSourceId);
  }
  
  public void adddataSourceIds(Collection<String> dataSourceIds){
    for (String dataSourceId: dataSourceIds){
      adddataSourceId(dataSourceId);
    }
  }

  @Id
  @Override
  public String getId() {
    return dataSetId;
  }

  @Override
  public void setId(String id) {
    this.dataSetId = id;
  }
}
