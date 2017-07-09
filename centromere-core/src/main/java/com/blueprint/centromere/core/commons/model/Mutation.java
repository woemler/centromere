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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
public class Mutation extends Data implements Attributes {
  
  private String chromosome;
  private Integer dnaStartPosition;
  private Integer dnaStopPosition;
	private String strand;
	@Indexed private String variantClassification;
	@Indexed private String variantType;
  private Set<String> externalReferenes = new HashSet<>();
	private String referenceAllele;
	private String alternateAllele;
  private String codonChange;
	private String nucleotideChange;
	private String nucleotideTranscript;
	private String proteinChange;
	private String proteinTranscript;
	private List<VariantTranscript> alternateTranscripts = new ArrayList<>();
	private Map<String,String> attributes = new HashMap<>();

  public String getChromosome() {
    return chromosome;
  }

  public void setChromosome(String chromosome) {
    this.chromosome = chromosome;
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

  public Set<String> getExternalReferenes() {
    return externalReferenes;
  }

  public void setExternalReferenes(Collection<String> referenes) {
    this.externalReferenes = new HashSet<>(referenes);
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

  public String getCodonChange() {
    return codonChange;
  }

  public void setCodonChange(String codonChange) {
    this.codonChange = codonChange;
  }

  public String getNucleotideChange() {
    return nucleotideChange;
  }

  public void setNucleotideChange(String nucleotideChange) {
    this.nucleotideChange = nucleotideChange;
  }

  public String getNucleotideTranscript() {
    return nucleotideTranscript;
  }

  public void setNucleotideTranscript(String nucleotideTranscript) {
    this.nucleotideTranscript = nucleotideTranscript;
  }

  public String getProteinChange() {
    return proteinChange;
  }

  public void setProteinChange(String proteinChange) {
    this.proteinChange = proteinChange;
  }

  public String getProteinTranscript() {
    return proteinTranscript;
  }

  public void setProteinTranscript(String proteinTranscript) {
    this.proteinTranscript = proteinTranscript;
  }

  public List<VariantTranscript> getAlternateTranscripts() {
    return alternateTranscripts;
  }

  public void setAlternateTranscripts(
      List<VariantTranscript> alternateTranscripts) {
    this.alternateTranscripts = alternateTranscripts;
  }

  public void addExternalReference(String reference){
	  this.externalReferenes.add(reference);
  }
  
  public void addAlternateTranscript(VariantTranscript transcript){
    this.alternateTranscripts.add(transcript);
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

  @Override
  public String toString() {
    return "Mutation{" +
        "chromosome='" + chromosome + '\'' +
        ", dnaStartPosition=" + dnaStartPosition +
        ", dnaStopPosition=" + dnaStopPosition +
        ", strand='" + strand + '\'' +
        ", variantClassification='" + variantClassification + '\'' +
        ", variantType='" + variantType + '\'' +
        ", externalReferenes=" + externalReferenes +
        ", referenceAllele='" + referenceAllele + '\'' +
        ", alternateAllele='" + alternateAllele + '\'' +
        ", codonChange='" + codonChange + '\'' +
        ", nucleotideChange='" + nucleotideChange + '\'' +
        ", nucleotideTranscript='" + nucleotideTranscript + '\'' +
        ", proteinChange='" + proteinChange + '\'' +
        ", proteinTranscript='" + proteinTranscript + '\'' +
        ", alternateTranscripts=" + alternateTranscripts +
        ", attributes=" + attributes +
        '}';
  }

  /**
   * Nested class for capturing additional transcript variants.
   */
	public static class VariantTranscript {

		private String transcriptId;
		private String variantClassification;
		private String transcriptChange;
		private String geneId;

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

    public String getTranscriptChange() {
      return transcriptChange;
    }

    public void setTranscriptChange(String transcriptChange) {
      this.transcriptChange = transcriptChange;
    }

    public String getGeneId() {
      return geneId;
    }

    public void setGeneId(String geneId) {
      this.geneId = geneId;
    }

    @Override
    public String toString() {
      return "VariantTranscript{" +
          "transcriptId='" + transcriptId + '\'' +
          ", variantClassification='" + variantClassification + '\'' +
          ", transcriptChange='" + transcriptChange + '\'' +
          ", geneId='" + geneId + '\'' +
          '}';
    }
  }
	
}
