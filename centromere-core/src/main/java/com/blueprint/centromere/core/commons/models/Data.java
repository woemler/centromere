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

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Base class for modeling processed genomic data.  Assumes that each record has an associated 
 *   {@link DataFile}, {@link Sample}, and {@link Gene}.
 * 
 * @author woemler
 * @since 0.4.3
 */
@MappedSuperclass
public abstract class Data extends AbstractModel {

	@ManyToOne
	@JoinColumn(name = "sampleId")
	private Sample sample;

	@Column(updatable = false, insertable = false)
	private String sampleId;

	@ManyToOne
	@JoinColumn(name = "dataFileId")
	private DataFile dataFile;

	@Column(updatable = false, insertable = false)
	private String dataFileId;

	@ManyToOne
	@JoinColumn(name = "geneId")
	private Gene gene;

	@Column(updatable = false, insertable = false)
	private String geneId;

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public DataFile getDataFile() {
		return dataFile;
	}

	public void setDataFile(DataFile dataFile) {
		this.dataFile = dataFile;
	}

	public String getDataFileId() {
		return dataFileId;
	}

	public void setDataFileId(String dataFileId) {
		this.dataFileId = dataFileId;
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	public String getGeneId() {
		return geneId;
	}

	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}
}
