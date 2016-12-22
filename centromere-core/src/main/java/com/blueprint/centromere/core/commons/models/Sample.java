/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package com.blueprint.centromere.core.commons.models;

import com.blueprint.centromere.core.model.AbstractModel;

import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Model for representing a single biological sample taken from a {@link Subject} for experimentation.
 *   It is presumed that a single sample can be used in one or more assays and be a part of one or
 *   more {@link DataSet} instances.
 * 
 * @author woemler
 */
@Entity
@Document
public class Sample extends AbstractModel implements Attributes {

	private String name;
	private String sampleType;
	private String tissue;
	private String histology;
	private String notes;

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	private Map<String, String> attributes = new HashMap<>();
	
	@ManyToOne
	@JoinColumn(name = "subjectId")
	private Subject subject;

	@Column(updatable = false, insertable = false)
	private UUID subjectId;

	@ManyToOne
	@JoinColumn(name = "dataSetId")
	private DataSet dataSet;

	@Column(updatable = false, insertable = false)
	private UUID dataSetId;

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public UUID getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(UUID subjectId) {
		this.subjectId = subjectId;
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

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public UUID getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(UUID dataSetId) {
		this.dataSetId = dataSetId;
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
}
