/*
 * Copyright 2018 the original author or authors
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

import com.blueprint.centromere.core.model.Ignored;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Base gene model implementation.  Allows for varied metadata sources, such as EntrezGene or Ensembl.
 *   The {@link #geneId} field should correspond to the uniquely identifying ID for the genes, where
 *   the {@link #symbol} field should correspond to the primary gene symbol (eg HGNC).  The {@link #aliases}
 *   field can contain all non-primary gene symbols associated with the gene.
 * 
 * @author woemler
 */
@Document(collection = "genes")
@Data
@AccessType(Type.PROPERTY)
public class Gene extends Metadata<String> implements Attributes {
  
  private String geneId;
	
	@Indexed 
  @ManagedTerm 
  @NotEmpty
  private String symbol;
	
	@Indexed
  @ManagedTerm
  @NotEmpty
  private String referenceId;
	
	@NotEmpty
	private Integer taxId;
	
	@NotEmpty
	private String chromosome;
	
	@Ignored 
  private String chromosomeLocation;
	
	@NotEmpty
	private String geneType;
	
	@Ignored 
  private String description;
	
	@Indexed 
  @ManagedTerm 
  private List<String> aliases = new ArrayList<>();
	
	private Map<String, String> attributes = new HashMap<>();
	
	private Map<String, String> externalReferences = new HashMap<>();
  
  public void addExternalReference(String name, String value){
		externalReferences.put(name, value);
	}

	public boolean hasExternalReference(String name){
		return externalReferences.containsKey(name);
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

  @Id
  @Override
  public String getId() {
    return geneId;
  }

  @Override
  public void setId(String id) {
    this.geneId = id;
  }
}
