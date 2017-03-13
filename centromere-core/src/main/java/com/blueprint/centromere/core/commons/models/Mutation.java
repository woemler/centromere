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

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * TODO: add reference transcript and alternate transcript mutations
 *
 * @author woemler
 */
@Document
public class Mutation extends Data {
	
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
}
