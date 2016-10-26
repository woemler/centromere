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
import java.util.Map;

/**
 * @author woemler
 */
@MappedSuperclass
public abstract class Gene<ID extends Serializable> implements Model<ID>, Attributes, SimpleAliases {
	
	@Alias("primaryId")
	private String primaryReferenceId;
	
	@Alias("symbol")
	private String primaryGeneSymbol;
	
	private Integer taxId;
	
	private String chromosome;
	
	@Ignored
	private String chromosomeLocation;
	
	private String geneType;
	
	@Ignored
	private String description;
	
	private String referenceSource;

	
	public abstract Map<String, String> getExternalReferenceMap();
	public abstract void addExternalReference(String name, String value);
	public abstract boolean hasExternalReference(String name);

	
	public String getPrimaryReferenceId() {
		return primaryReferenceId;
	}

	public void setPrimaryReferenceId(String primaryReferenceId) {
		this.primaryReferenceId = primaryReferenceId;
	}

	public String getReferenceSource() {
		return referenceSource;
	}

	public void setReferenceSource(String referenceSource) {
		this.referenceSource = referenceSource;
	}

	public String getPrimaryGeneSymbol() {
		return primaryGeneSymbol;
	}

	public void setPrimaryGeneSymbol(String primaryGeneSymbol) {
		this.primaryGeneSymbol = primaryGeneSymbol;
	}

	

	public Integer getTaxId() {
		return taxId;
	}

	public void setTaxId(Integer taxId) {
		this.taxId = taxId;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getChromosomeLocation() {
		return chromosomeLocation;
	}

	public void setChromosomeLocation(String chromosomeLocation) {
		this.chromosomeLocation = chromosomeLocation;
	}

	public String getGeneType() {
		return geneType;
	}

	public void setGeneType(String geneType) {
		this.geneType = geneType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
