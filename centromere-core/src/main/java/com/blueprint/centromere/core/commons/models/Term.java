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
import com.blueprint.centromere.core.model.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Model for representing controlled and/or indexed terms.  Can be used for reference ontologies, 
 *   or cataloging metadata values for easy searching.
 * 
 * @author woemler
 */
@Entity
@Document
public class Term extends AbstractModel {
	
	private String term;
	private String source;
	private String cetegory;
	private String subCategory;
	private String referenceId;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCetegory() {
		return cetegory;
	}

	public void setCetegory(String cetegory) {
		this.cetegory = cetegory;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
}