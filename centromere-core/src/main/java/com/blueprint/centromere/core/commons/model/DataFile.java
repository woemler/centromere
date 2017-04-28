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
import com.blueprint.centromere.core.model.Model;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
public class DataFile extends AbstractModel implements Attributes {
	
	@Indexed(unique = true) private String filePath;
	private String dataType;
	private Class<? extends Model<?>> model;
	private Date dateCreated;
	private Date dateUpdated;
	@Indexed @Linked(model = DataSet.class) private String dataSetId;
	
	private Map<String, String> attributes = new HashMap<>();
	
	/* Getters and Setters */ 

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

  public Class<? extends Model<?>> getModel() {
    return model;
  }

  public void setModel(Class<? extends Model<?>> model){
	  this.model = model;
  }

  public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
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

	@Override
	public String toString() {
		return "DataFile{" +
				"filePath='" + filePath + '\'' +
				", dataType='" + dataType + '\'' +
        ", model='" + model.getName() + '\'' +
				", dateCreated=" + dateCreated +
				", dateUpdated=" + dateUpdated +
				", dataSetId='" + dataSetId + '\'' +
				", attributes=" + attributes +
				'}';
	}
}
