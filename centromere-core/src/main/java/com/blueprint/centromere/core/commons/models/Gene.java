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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * @author woemler
 */
@Entity
@Table(indexes = {
		@Index(name = "GENES_IDX_01", columnList = "primaryReferenceId", unique = true),
		@Index(name = "GENES_IDX_02", columnList = "primaryGeneSymbol")
})
public class Gene extends AbstractModel implements Attributes {

	private String primaryReferenceId;
	private String primaryGeneSymbol;
	private Integer taxId;
	private String chromosome;
	private String chromosomeLocation;
	private String geneType;
	private String description;
	private String referenceSource;

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	private List<String> aliases = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	private Map<String, String> attributes = new HashMap<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	private Map<String, String> externalReferences = new HashMap<>();

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

	public Map<String, String> getExternalReferences(){
		return externalReferences;
	}

	public void addExternalReference(String name, String value){
		externalReferences.put(name, value);
	}

	public boolean hasExternalReference(String name){
		return externalReferences.containsKey(name);
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setExternalReferences(Map<String, String> externalReferences) {
		this.externalReferences = externalReferences;
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

	public void addAlias(String alias){
		if (!aliases.contains(alias)) this.aliases.add(alias);
	}

	@Override
	public String toString() {
		return "Gene{" +
				"primaryReferenceId='" + primaryReferenceId + '\'' +
				", primaryGeneSymbol='" + primaryGeneSymbol + '\'' +
				", taxId=" + taxId +
				", chromosome='" + chromosome + '\'' +
				", chromosomeLocation='" + chromosomeLocation + '\'' +
				", geneType='" + geneType + '\'' +
				", description='" + description + '\'' +
				", referenceSource='" + referenceSource + '\'' +
				", aliases=" + aliases +
				", attributes=" + attributes +
				", externalReferences=" + externalReferences +
				'}';
	}
}
