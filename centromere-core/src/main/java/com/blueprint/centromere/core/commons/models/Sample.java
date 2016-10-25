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

import com.blueprint.centromere.core.model.Alias;
import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Model for representing a single biological sample taken from a {@link Subject} for experimentation.
 *   It is presumed that a single sample can be used in one or more assays and be a part of one or
 *   more {@link DataSet} instances.
 * 
 * @author woemler
 */
@MappedSuperclass
public abstract class Sample<ID extends Serializable> implements Model<ID>, Attributes, SimpleAliases {
	
	private String name;
	
	@Alias("type")
	private String sampleType;
	
	private String tissue;
	
	private String histology;
	
	@Ignored
	private String notes;
	
	
	abstract public Object getSubjectId();
	abstract public <S extends Subject<?>> void setSubjectMetadata(S subject);
	
	
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
}
