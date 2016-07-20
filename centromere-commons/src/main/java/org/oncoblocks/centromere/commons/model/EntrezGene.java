package org.oncoblocks.centromere.commons.model;

import org.oncoblocks.centromere.core.model.Model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author woemler
 */
public abstract class EntrezGene<ID extends Serializable> implements Model<ID> {
	
	private Long entrezGeneId;
	private String primaryGeneSymbol;
	private Set<String> geneSymbolAliases;
	private Integer taxId;
	private String locusTag;
	private String chromosome;
	private String chromosomeLocation;
	private String geneType;
	private String description;
	private Map<String, String> databaseCrossReferences;
	private Map<String, Object> attributes;

	public Long getEntrezGeneId() {
		return entrezGeneId;
	}

	public void setEntrezGeneId(Long entrezGeneId) {
		this.entrezGeneId = entrezGeneId;
	}

	public String getPrimaryGeneSymbol() {
		return primaryGeneSymbol;
	}

	public void setPrimaryGeneSymbol(String primaryGeneSymbol) {
		this.primaryGeneSymbol = primaryGeneSymbol;
	}

	public Set<String> getGeneSymbolAliases() {
		return geneSymbolAliases;
	}

	public void setGeneSymbolAliases(Set<String> geneSymbolAliases) {
		this.geneSymbolAliases = geneSymbolAliases;
	}

	public Integer getTaxId() {
		return taxId;
	}

	public void setTaxId(Integer taxId) {
		this.taxId = taxId;
	}

	public String getLocusTag() {
		return locusTag;
	}

	public void setLocusTag(String locusTag) {
		this.locusTag = locusTag;
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

	public Map<String, String> getDatabaseCrossReferences() {
		return databaseCrossReferences;
	}

	public void setDatabaseCrossReferences(
			Map<String, String> databaseCrossReferences) {
		this.databaseCrossReferences = databaseCrossReferences;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
