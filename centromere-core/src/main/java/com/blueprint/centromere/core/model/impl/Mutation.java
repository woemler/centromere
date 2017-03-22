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

package com.blueprint.centromere.core.model.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * TODO: add reference transcript and alternate transcript mutations
 *
 * @author woemler
 */
@Document
public class Mutation extends Data implements Attributes {
	
	private String chromosome;
	private String referenceGenome;
	private String strand;
	private Integer dnaStartPosition;
	private Integer dnaStopPosition;
	private String referenceAllele;
	private String alternateAllele;
	private String cDnaChange;
	private String codonChange;
	
	private String proteinChange;
	
	private String mutationClassification;
	private String mutationType;
	
	private Map<String, String> attributes = new HashMap<>();

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public String getReferenceGenome() {
		return referenceGenome;
	}

	public void setReferenceGenome(String referenceGenome) {
		this.referenceGenome = referenceGenome;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public Integer getDnaStartPosition() {
		return dnaStartPosition;
	}

	public void setDnaStartPosition(Integer dnaStartPosition) {
		this.dnaStartPosition = dnaStartPosition;
	}

	public Integer getDnaStopPosition() {
		return dnaStopPosition;
	}

	public void setDnaStopPosition(Integer dnaStopPosition) {
		this.dnaStopPosition = dnaStopPosition;
	}

	public String getReferenceAllele() {
		return referenceAllele;
	}

	public void setReferenceAllele(String referenceAllele) {
		this.referenceAllele = referenceAllele;
	}

	public String getAlternateAllele() {
		return alternateAllele;
	}

	public void setAlternateAllele(String alternateAllele) {
		this.alternateAllele = alternateAllele;
	}

	public String getcDnaChange() {
		return cDnaChange;
	}

	public void setcDnaChange(String cDnaChange) {
		this.cDnaChange = cDnaChange;
	}

	public String getCodonChange() {
		return codonChange;
	}

	public void setCodonChange(String codonChange) {
		this.codonChange = codonChange;
	}

	public String getProteinChange() {
		return proteinChange;
	}

	public void setProteinChange(String proteinChange) {
		this.proteinChange = proteinChange;
	}

	public String getMutationClassification() {
		return mutationClassification;
	}

	public void setMutationClassification(String mutationClassification) {
		this.mutationClassification = mutationClassification;
	}

	public String getMutationType() {
		return mutationType;
	}

	public void setMutationType(String mutationType) {
		this.mutationType = mutationType;
	}

  @Override
  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  /**
   * Creates a new attribute key-value mapping.
   *
   * @param name attribute name
   * @param value attribute value
   */
  @Override
  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }

  /**
   * Adds multiple attribute mappings at once.
   *
   * @param attributes Map of key-value attributes
   */
  @Override
  public void addAttributes(Map<String, String> attributes) {
    attributes.putAll(attributes);
  }

  /**
   * Tests whether an attribute has been registered.
   *
   * @param name attribute name.
   */
  @Override
  public boolean hasAttribute(String name) {
    return attributes.containsKey(name);
  }

  /**
   * Gets the value of the given attribute.
   *
   * @param name attribute name
   * @return attribute value.
   */
  @Override
  public String getAttribute(String name) {
    return attributes.containsKey(name) ? attributes.get(name) : null;
  }

  /**
   * Nested class for capturing additional transcript variants.
   */
	public static class OtherTranscripts {

		private String geneSymbol;
		private String transcriptId;
		private String variantClassification;
		private String proteinChange;

		public String getGeneSymbol() {
			return geneSymbol;
		}

		public void setGeneSymbol(String geneSymbol) {
			this.geneSymbol = geneSymbol;
		}

		public String getTranscriptId() {
			return transcriptId;
		}

		public void setTranscriptId(String transcriptId) {
			this.transcriptId = transcriptId;
		}

		public String getVariantClassification() {
			return variantClassification;
		}

		public void setVariantClassification(String variantClassification) {
			this.variantClassification = variantClassification;
		}

		public String getProteinChange() {
			return proteinChange;
		}

		public void setProteinChange(String proteinChange) {
			this.proteinChange = proteinChange;
		}

		@Override public String toString() {
			return "OtherTranscripts{" +
					"geneSymbol='" + geneSymbol + '\'' +
					", transcriptId='" + transcriptId + '\'' +
					", variantClassification='" + variantClassification + '\'' +
					", proteinChange='" + proteinChange + '\'' +
					'}';
		}
	}
	
}
