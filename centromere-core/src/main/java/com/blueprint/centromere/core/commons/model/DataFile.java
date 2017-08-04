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
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
@Data
public class DataFile extends AbstractModel implements Attributes {
	
	@Indexed(unique = true) private String filePath;
	private String dataType;
	private String model;
	@Ignored private String checksum;
	@Ignored private Date dateCreated;
	@Ignored private Date dateUpdated;
	@Indexed @Linked(model = DataSet.class, rel = "dataSet") private String dataSetId;
	private Map<String, String> attributes = new HashMap<>();

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
