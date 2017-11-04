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

import com.blueprint.centromere.core.model.AbstractModel;
import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Linked;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model representation of an annotated set of data, which may be compromised of multiple data types
 *   and samples.  
 * 
 * @author woemler
 */
@Document
@Data
public class DataSet extends AbstractModel implements Attributes {
	
	private String displayName;
	@Indexed(unique = true) private String shortName;
	@Indexed private String source;
	@Ignored private String version;
	@Ignored private String description;
	@Linked(model = DataFile.class, rel = "dataFiles") private List<String> dataFileIds = new ArrayList<>();
  @Linked(model = Sample.class, rel = "samples") private List<String> sampleIds = new ArrayList<>();
	private Map<String, String> attributes = new HashMap<>();
	@Ignored private Map<String, String> parameters = new HashMap<>();

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
  
  public void addDataFileId(String dataFileId){
    if (!dataFileIds.contains(dataFileId)) dataFileIds.add(dataFileId);
  }
  
  public void addDataFileIds(Collection<String> dataFileIds){
    for (String dataFileId: dataFileIds){
      addDataFileId(dataFileId);
    }
  }

}
