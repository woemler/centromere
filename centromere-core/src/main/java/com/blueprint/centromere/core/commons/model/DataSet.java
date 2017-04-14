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
import com.blueprint.centromere.core.model.Linked;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model representation of an annotated set of data, which may be compromised of multiple data types
 *   and samples.  
 * 
 * @author woemler
 */
@Document
public class DataSet extends AbstractModel implements Attributes {
	
	private String displayName;
	@Indexed(unique = true) private String shortName;
	@Indexed private String source;
	private String version;
	private String description;

	@Linked(model = DataFile.class)
	private List<String> dataFileIds = new ArrayList<>();

	@Linked(model = Sample.class)
	private List<String> sampleIds = new ArrayList<>();
	
	private Map<String, String> attributes = new HashMap<>();

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return attributes.containsKey(name) ? attributes.get(name) : null;
	}

  public List<String> getDataFileIds() {
    return dataFileIds;
  }

  public void setDataFileIds(List<String> dataFileIds) {
    this.dataFileIds = dataFileIds;
  }

  public List<String> getSampleIds() {
    return sampleIds;
  }

  public void setSampleIds(List<String> sampleIds) {
    this.sampleIds = sampleIds;
  }

  @Override
  public String toString() {
    return "DataSet{" +
        "displayName='" + displayName + '\'' +
        ", shortName='" + shortName + '\'' +
        ", source='" + source + '\'' +
        ", version='" + version + '\'' +
        ", description='" + description + '\'' +
        ", dataFileIds=" + dataFileIds +
        ", sampleIds=" + sampleIds +
        ", attributes=" + attributes +
        '}';
  }
}
