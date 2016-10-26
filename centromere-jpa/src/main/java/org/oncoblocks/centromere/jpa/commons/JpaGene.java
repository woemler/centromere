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

package org.oncoblocks.centromere.jpa.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.blueprint.centromere.core.commons.models.Gene;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author woemler
 * @since 0.4.3
 */
@Entity
@Table(name = "genes")
@AttributeOverrides({
		@AttributeOverride(name = "primaryReferenceId",
				column = @Column(name = "primary_reference_id", unique = true, nullable = false)),
		@AttributeOverride(name = "primaryGeneSymbol",
				column = @Column(name = "primary_gene_symbol", nullable = false)),
		@AttributeOverride(name = "taxId",
				column = @Column(name = "taxId", length = 8, nullable = false)),
		@AttributeOverride(name = "locusTag", column = @Column(name = "locus_tag")),
		@AttributeOverride(name = "chromosome",
				column = @Column(name = "chromosome", nullable = false, length = 2)),
		@AttributeOverride(name = "chromosomeLocation", column = @Column(name = "chromosome_location")),
		@AttributeOverride(name = "geneType", column = @Column(name = "gene_type")),
		@AttributeOverride(name = "description", column = @Column(name = "description", length = 1024))
})
public class JpaGene extends Gene<Long> implements Serializable {
	
	@Id 
	@Column(name = "gene_id", nullable = false, updatable = false)
	@GeneratedValue
	private Long id;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gene")
	private List<JpaGeneSymbolAlias> jpaGeneSymbolAliases = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gene")
	private List<JpaGeneAttribute> jpaGeneAttributes = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gene")
	private List<JpaGeneExternalReference> jpaGeneExternalReferences = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public List<JpaGeneSymbolAlias> getJpaGeneSymbolAliases() {
		return jpaGeneSymbolAliases;
	}

	public void setJpaGeneSymbolAliases(
			List<JpaGeneSymbolAlias> jpaGeneSymbolAliases) {
		this.jpaGeneSymbolAliases = jpaGeneSymbolAliases;
	}

	@JsonIgnore
	public List<JpaGeneAttribute> getJpaGeneAttributes() {
		return jpaGeneAttributes;
	}

	public void setJpaGeneAttributes(
			List<JpaGeneAttribute> jpaGeneAttributes) {
		this.jpaGeneAttributes = jpaGeneAttributes;
	}

	@JsonIgnore
	public List<JpaGeneExternalReference> getJpaGeneExternalReferences() {
		return jpaGeneExternalReferences;
	}

	public void setJpaGeneExternalReferences(
			List<JpaGeneExternalReference> jpaGeneExternalReferences) {
		this.jpaGeneExternalReferences = jpaGeneExternalReferences;
	}

	@Override public Collection<String> getAliases() {
		List<String> aliases = new ArrayList<>();
		for (JpaGeneSymbolAlias alias: jpaGeneSymbolAliases){
			aliases.add(alias.getSymbol());
		}
		return aliases;
	}

	@Override public void addAlias(String alias) {
		JpaGeneSymbolAlias geneSymbolAlias = new JpaGeneSymbolAlias();
		geneSymbolAlias.setSymbol(alias);
		if (jpaGeneSymbolAliases == null){
			jpaGeneSymbolAliases = new ArrayList<>();
		}
		if (!jpaGeneSymbolAliases.contains(geneSymbolAlias)){
			jpaGeneSymbolAliases.add(geneSymbolAlias);
		}
		
	}

	@Override public Map<String, String> getExternalReferenceMap() {
		Map<String,String> refs = new HashMap<>();
		for (JpaGeneExternalReference reference: jpaGeneExternalReferences){
			refs.put(reference.getName(), reference.getValue());
		}
		return refs;
	}

	@Override public void addExternalReference(String name, String value) {
		JpaGeneExternalReference reference = new JpaGeneExternalReference();
		reference.setName(name);
		reference.setValue(value);
		if (!jpaGeneExternalReferences.contains(reference)){
			jpaGeneExternalReferences.add(reference);
		}
	}

	@Override public Map<String, Object> getAttributeMap() {
		Map<String, Object> atts = new HashMap<>();
		for (JpaGeneAttribute attribute: jpaGeneAttributes){
			atts.put(attribute.getName(), attribute.getValue());
		}
		return atts;
	}

	@Override public void addAttribute(String name, Object value) {
		JpaGeneAttribute attribute = new JpaGeneAttribute();
		attribute.setName(name);
		attribute.setValue(value.toString());
		if (!jpaGeneAttributes.contains(attribute)){
			jpaGeneAttributes.add(attribute);
		}
	}

	@Override public boolean hasExternalReference(String name) {
		return this.getExternalReferenceMap().containsKey(name);
	}

	@Override public void addAttributes(Map<String, Object> attributes) {
		for (Map.Entry entry: attributes.entrySet()){
			this.addAttribute((String) entry.getKey(), entry.getValue());
		}
	}

	@Override public boolean hasAttribute(String name) {
		return this.getAttributeMap().containsKey(name);
	}

	@Override public Object getAttribute(String name) {
		Map<String,Object> map = this.getAttributeMap();
		return map.containsKey(name) ? map.get(name) : null;
	}

	@Override public void addAliases(Collection<String> aliases) {
		for (String alias: aliases){
			this.addAlias(alias);
		}
	}

	@Override public String toString() {
		return "JpaEntrezGene{" +
				"id=" + id +
				", primaryGeneSymbol=" + this.getPrimaryGeneSymbol() +
				", primaryReferenceId=" + this.getPrimaryReferenceId() +
				", chromosome=" + this.getChromosome() +
				", chromosomeLocation=" + this.getChromosomeLocation() +
				", taxId=" + this.getTaxId() +
				", description=" + this.getDescription() +
				", geneType=" + this.getGeneType() +
				", geneSymbolAliases=" + jpaGeneSymbolAliases +
				", attributes=" + jpaGeneAttributes +
				", databaseCrossReferences=" + jpaGeneExternalReferences +
				'}';
	}
}
