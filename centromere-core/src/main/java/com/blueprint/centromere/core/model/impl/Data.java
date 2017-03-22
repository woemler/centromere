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

import com.blueprint.centromere.core.model.AbstractModel;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * Base class for modeling processed genomic data.  Assumes that each record has an associated 
 *   {@link DataFile}, {@link Sample}, and {@link Gene}.
 * 
 * @author woemler
 * @since 0.4.3
 */
public abstract class Data extends AbstractModel {

	@DBRef(lazy = true)
	private Sample sample;

	@Indexed
	private String sampleId;

	@DBRef(lazy = true)
	private DataFile dataFile;

	@Indexed
	private String dataFileId;
	
	@DBRef(lazy = true)
  private DataSet dataSet;
	
	@Indexed
  private String dataSetId;

	@DBRef(lazy = true)
	private Gene gene;

	@Indexed
	private String geneId;

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
		this.sampleId = sample.getId();
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
		this.dataFileId = dataFile.getId();
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
		this.geneId = gene.getId();
	}

	public String getGeneId() {
		return geneId;
	}

	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}

  public DataSet getDataSet() {
    return dataSet;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
    this.dataSetId = dataSet.getId();
  }

  public String getDataSetId() {
    return dataSetId;
  }

  public void setDataSetId(String dataSetId) {
    this.dataSetId = dataSetId;
  }
}
