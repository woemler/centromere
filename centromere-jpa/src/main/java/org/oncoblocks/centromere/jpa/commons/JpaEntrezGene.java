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
import org.oncoblocks.centromere.core.commons.EntrezGene;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author woemler
 * @since 0.4.3
 */
@Entity
@Table(name = "entrez_gene")
@AttributeOverrides({
		@AttributeOverride(name = "entrezGeneId",
				column = @Column(name = "entrez_gene_id", unique = true, nullable = false)),
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
public class JpaEntrezGene extends EntrezGene<Long> implements Serializable {
	
	@Id 
	@Column(name = "gene_id", nullable = false, updatable = false)
	@GeneratedValue
	private Long id;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gene")
	private List<JpaEntrezGeneSymbolAlias> jpaEntrezGeneSymbolAliases = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gene")
	private List<JpaEntrezGeneAttribute> jpaEntrezGeneAttributes = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "gene")
	private List<JpaEntrezGeneDatabaseCrossReference> jpaEntrezGeneDatabaseCrossReferences = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public List<JpaEntrezGeneSymbolAlias> getJpaEntrezGeneSymbolAliases() {
		return jpaEntrezGeneSymbolAliases;
	}

	public void setJpaEntrezGeneSymbolAliases(
			List<JpaEntrezGeneSymbolAlias> jpaEntrezGeneSymbolAliases) {
		this.jpaEntrezGeneSymbolAliases = jpaEntrezGeneSymbolAliases;
	}

	@JsonIgnore
	public List<JpaEntrezGeneAttribute> getJpaEntrezGeneAttributes() {
		return jpaEntrezGeneAttributes;
	}

	public void setJpaEntrezGeneAttributes(
			List<JpaEntrezGeneAttribute> jpaEntrezGeneAttributes) {
		this.jpaEntrezGeneAttributes = jpaEntrezGeneAttributes;
	}

	@JsonIgnore
	public List<JpaEntrezGeneDatabaseCrossReference> getJpaEntrezGeneDatabaseCrossReferences() {
		return jpaEntrezGeneDatabaseCrossReferences;
	}

	public void setJpaEntrezGeneDatabaseCrossReferences(
			List<JpaEntrezGeneDatabaseCrossReference> jpaEntrezGeneDatabaseCrossReferences) {
		this.jpaEntrezGeneDatabaseCrossReferences = jpaEntrezGeneDatabaseCrossReferences;
	}

	@Override public Collection<String> getGeneSymbolAliases() {
		List<String> aliases = new ArrayList<>();
		for (JpaEntrezGeneSymbolAlias alias: jpaEntrezGeneSymbolAliases){
			aliases.add(alias.getSymbol());
		}
		return aliases;
	}

	@Override public void addGeneSymbolAlias(String alias) {
		JpaEntrezGeneSymbolAlias geneSymbolAlias = new JpaEntrezGeneSymbolAlias();
		geneSymbolAlias.setSymbol(alias);
		if (jpaEntrezGeneSymbolAliases == null){
			jpaEntrezGeneSymbolAliases = new ArrayList<>();
		}
		if (!jpaEntrezGeneSymbolAliases.contains(geneSymbolAlias)){
			jpaEntrezGeneSymbolAliases.add(geneSymbolAlias);
		}
		
	}

	@Override public Map<String, String> getDatabaseCrossReferences() {
		Map<String,String> refs = new HashMap<>();
		for (JpaEntrezGeneDatabaseCrossReference reference: jpaEntrezGeneDatabaseCrossReferences){
			refs.put(reference.getName(), reference.getValue());
		}
		return refs;
	}

	@Override public void addDatabaseCrossReference(String name, String value) {
		JpaEntrezGeneDatabaseCrossReference reference = new JpaEntrezGeneDatabaseCrossReference();
		reference.setName(name);
		reference.setValue(value);
		if (!jpaEntrezGeneDatabaseCrossReferences.contains(reference)){
			jpaEntrezGeneDatabaseCrossReferences.add(reference);
		}
	}

	@Override public Map<String, Object> getAttributes() {
		Map<String, Object> atts = new HashMap<>();
		for (JpaEntrezGeneAttribute attribute: jpaEntrezGeneAttributes){
			atts.put(attribute.getName(), attribute.getValue());
		}
		return atts;
	}

	@Override public void addAttribute(String name, Object value) {
		JpaEntrezGeneAttribute attribute = new JpaEntrezGeneAttribute();
		attribute.setName(name);
		attribute.setValue(value.toString());
		if (!jpaEntrezGeneAttributes.contains(attribute)){
			jpaEntrezGeneAttributes.add(attribute);
		}
	}

	@Override public String toString() {
		return "JpaEntrezGene{" +
				"id=" + id +
				", primaryGeneSymbol=" + this.getPrimaryGeneSymbol() +
				", entrezGeneId=" + this.getEntrezGeneId() +
				", chromosome=" + this.getChromosome() +
				", chromosomeLocation=" + this.getChromosomeLocation() +
				", taxId=" + this.getTaxId() +
				", description=" + this.getDescription() +
				", geneType=" + this.getGeneType() +
				", locusTag=" + this.getLocusTag() +
				", geneSymbolAliases=" + jpaEntrezGeneSymbolAliases +
				", attributes=" + jpaEntrezGeneAttributes +
				", databaseCrossReferences=" + jpaEntrezGeneDatabaseCrossReferences +
				'}';
	}
}
