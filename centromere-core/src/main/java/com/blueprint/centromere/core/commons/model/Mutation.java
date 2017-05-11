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

package com.blueprint.centromere.core.commons.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
@CompoundIndexes({
    @CompoundIndex(def = "{'sampleId': 1, 'dataFileId': 1}"),
    @CompoundIndex(def = "{'geneId': 1, 'dataFileId': 1}")
})
public class Mutation extends Data implements Attributes {
  
	private String referenceGenome;
  private String chromosome;
  private Integer dnaStartPosition;
  private Integer dnaStopPosition;
	private String strand;
	@Indexed private String variantClassification;
	private String variantType;
	private String referenceAllele;
	private String alternateAllele;
	private Map<String, ExternalReference> externalReferenes = new HashMap<>();
	private String cDnaChange;
	private String codonChange;
	private String aminoAcidChange;
	private String mrnaTranscript;
	private String proteinTranscript;
	private List<AlternateTranscript> alternateTranscripts = new ArrayList<>();
	private Map<String,String> attributes = new HashMap<>();

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

  public String getVariantClassification() {
    return variantClassification;
  }

  public void setVariantClassification(String variantClassification) {
    this.variantClassification = variantClassification;
  }

  public String getVariantType() {
    return variantType;
  }

  public void setVariantType(String variantType) {
    this.variantType = variantType;
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

  public Map<String, ExternalReference> getExternalReferenes() {
    return externalReferenes;
  }

  public void setExternalReferenes(
      Map<String, ExternalReference> externalReferenes) {
    this.externalReferenes = externalReferenes;
  }

  public String getAminoAcidChange() {
    return aminoAcidChange;
  }

  public void setAminoAcidChange(String aminoAcidChange) {
    this.aminoAcidChange = aminoAcidChange;
  }

  public String getMrnaTranscript() {
    return mrnaTranscript;
  }

  public void setMrnaTranscript(String mrnaTranscript) {
    this.mrnaTranscript = mrnaTranscript;
  }

  public String getProteinTranscript() {
    return proteinTranscript;
  }

  public void setProteinTranscript(String proteinTranscript) {
    this.proteinTranscript = proteinTranscript;
  }

  public List<AlternateTranscript> getAlternateTranscripts() {
    return alternateTranscripts;
  }

  public void setAlternateTranscripts(
      List<AlternateTranscript> alternateTranscripts) {
    this.alternateTranscripts = alternateTranscripts;
  }
  
  public void addExternalReference(ExternalReference reference){
	  this.externalReferenes.put(reference.getSource(), reference);
  }

  public void addExternalReference(String source, ExternalReference reference){
    this.externalReferenes.put(source, reference);
  }
  
  public ExternalReference getExternalReference(String source){
    return this.externalReferenes.getOrDefault(source, null);
  }
  
  public void addAlternateTranscript(AlternateTranscript alternateTranscript){
    this.alternateTranscripts.add(alternateTranscript);
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
   * Nested class for referencing external database references for variants.
   */
  public static class ExternalReference {
    
    private String source;
    private String referenceId;
    private String notes;

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getReferenceId() {
      return referenceId;
    }

    public void setReferenceId(String referenceId) {
      this.referenceId = referenceId;
    }

    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
    }

    @Override
    public String toString() {
      return "ExternalReference{" +
          "source='" + source + '\'' +
          ", referenceId='" + referenceId + '\'' +
          ", notes='" + notes + '\'' +
          '}';
    }
  }

  /**
   * Nested class for capturing additional transcript variants.
   */
	public static class AlternateTranscript {

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
