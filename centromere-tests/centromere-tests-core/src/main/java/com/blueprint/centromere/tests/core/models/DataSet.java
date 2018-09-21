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

import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public abstract class DataSet<ID extends Serializable> implements Model<ID>, Attributes {
  
  private String name;
	private String source;
	@Ignored private String version;
	@Ignored private String description;
	
	@Linked(model = DataFile.class, rel = "dataFiles", field = "dataFileId") 
  private List<ID> dataFileIds = new ArrayList<>();
  
	@Linked(model = Sample.class, rel = "samples", field = "sampleId") 
  private List<ID> sampleIds = new ArrayList<>();
	
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

  public void addSampleId(ID sampleId){
    if (!sampleIds.contains(sampleId)) sampleIds.add(sampleId);
  }
  
  public void addSampleIds(Collection<String> sampleIds){
    for (String sampleId: sampleIds){
      if (!sampleIds.contains(sampleId)) sampleIds.add(sampleId);
    }
  }
  
  public void addDataFileId(ID dataFileId){
    if (!dataFileIds.contains(dataFileId)) dataFileIds.add(dataFileId);
  }
  
  public void addDataFileIds(Collection<String> dataFileIds){
    for (String dataFileId: dataFileIds){
      if (!dataFileIds.contains(dataFileId)) dataFileIds.add(dataFileId);
    }
  }

}
