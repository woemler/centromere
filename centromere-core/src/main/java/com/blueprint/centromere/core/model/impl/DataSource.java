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
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Past;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * {@link Model} implementation referencing a single source of data, such as a file or database query.
 * 
 * @author woemler
 */
@Document(collection = "data_sources")
@Data
@AccessType(Type.PROPERTY)
public class DataSource implements Model<String>, Attributes {
  
  private String dataSourceId;
  
  @NotEmpty
  private String sourceType;
  
  @NotEmpty
	private String source;
  
  @NotEmpty
	private String dataType;
	
  @NotEmpty
	private String model;
	
	@Ignored 
  @NotEmpty
  private String checksum;
	
	@Ignored 
  @Past
  private Date dateCreated;
	
	@Ignored
  @Past 
  private Date dateUpdated;
	
	@Indexed 
  @Linked(model = DataSet.class, rel = "dataSet", field = "dataSetId") 
  private String dataSetId;
	
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

	@Id
  @Override
  public String getId() {
    return dataSourceId;
  }

  @Override
  public void setId(String id) {
    this.dataSourceId = id;
  }
  
  public static enum SourceTypes {
	  FILE,
    DATABASE,
    WEB_SERVICE,
    TEXT
  }
  
}
