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
@lombok.Data
public class Mutation extends Data implements Attributes {
  
  private String chromosome;
  private Integer dnaStartPosition;
  private Integer dnaStopPosition;
	private String strand;
	@Indexed private String variantClassification;
	@Indexed private String variantType;
  private Set<String> externalReferences = new HashSet<>();
	private String referenceAllele;
	private String alternateAllele;
  private String codonChange;
	private String nucleotideChange;
	private String nucleotideTranscript;
	private String proteinChange;
	private String proteinTranscript;
	private List<VariantTranscript> alternateTranscripts = new ArrayList<>();
	private Map<String,String> attributes = new HashMap<>();

  public void addExternalReference(String reference){
	  this.externalReferences.add(reference);
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

  @Override
  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }

  @Override
  public void addAttributes(Map<String, String> attributes) {
    attributes.putAll(attributes);
  }

  @Override
  public boolean hasAttribute(String name) {
    return attributes.containsKey(name);
  }

  @Override
  public String getAttribute(String name) {
    return attributes.containsKey(name) ? attributes.get(name) : null;
  }

  /**
   * Nested class for capturing additional transcript variants.
   */
  @lombok.Data
	public static class VariantTranscript {

		private String transcriptId;
		private String variantClassification;
		private String transcriptChange;
		private String geneId;

  }
	
}
