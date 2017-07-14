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
import com.blueprint.centromere.core.model.AbstractModel;
import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Linked;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for representing a single biological sample taken from a {@link Subject} for experimentation.
 *   It is presumed that a single sample can be used in one or more assays and be a part of one or
 *   more {@link DataSet} instances.
 * 
 * @author woemler
 */
@Document
public class Sample extends AbstractModel implements Attributes {

	@Indexed @ManagedTerm private String name;
	private String sampleType;
	@ManagedTerm private String tissue;
	@ManagedTerm private String histology;
	@Ignored private String notes;
	private Map<String, String> attributes = new HashMap<>();
	@Indexed @Linked(model = Subject.class, rel = "subject") private String subjectId;
	@Indexed @Linked(model = DataSet.class, rel = "dataSet") private String dataSetId;

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getTissue() {
		return tissue;
	}

	public void setTissue(String tissue) {
		this.tissue = tissue;
	}

	public String getHistology() {
		return histology;
	}

	public void setHistology(String histology) {
		this.histology = histology;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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

	@Override
	public String toString() {
		return "Sample{" +
				"name='" + name + '\'' +
				", sampleType='" + sampleType + '\'' +
				", tissue='" + tissue + '\'' +
				", histology='" + histology + '\'' +
				", notes='" + notes + '\'' +
				", attributes=" + attributes +
				", subjectId='" + subjectId + '\'' +
        ", dataSetId='" + dataSetId + '\'' +
				'}';
	}
}
