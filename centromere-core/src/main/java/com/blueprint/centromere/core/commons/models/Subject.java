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

import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Collection;

/**
 * Model representation of an individual organism, such as a clinical subject or model animal.  A 
 *   single Subject can be assumed to have one or more {@link Sample} entities describing biological
 *   material taken for experimentation.
 * 
 * @author woemler
 */
@MappedSuperclass
public abstract class Subject<ID extends Serializable> implements Model<ID>, Attributes, SimpleAliases {
	
	private String name;
	
	private String species;
	
	private String gender;
	
	@Ignored
	private String notes;

	abstract public Collection<?> getSampleIds();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
