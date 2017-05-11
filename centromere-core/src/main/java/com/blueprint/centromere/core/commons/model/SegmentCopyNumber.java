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

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author woemler
 */
@Document
@CompoundIndexes({
    @CompoundIndex(def = "{'sampleId': 1, 'dataFileId': 1}"),
    @CompoundIndex(def = "{'geneId': 1, 'dataFileId': 1}")
})
public class SegmentCopyNumber extends Data {
	
	private String chromosome;
	private Long segmentStart;
	private Long segmentEnd;
	private Integer probeCount;
	private Double value; 

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public Long getSegmentStart() {
		return segmentStart;
	}

	public void setSegmentStart(Long segmentStart) {
		this.segmentStart = segmentStart;
	}

	public Long getSegmentEnd() {
		return segmentEnd;
	}

	public void setSegmentEnd(Long segmentEnd) {
		this.segmentEnd = segmentEnd;
	}

	public Integer getProbeCount() {
		return probeCount;
	}

	public void setProbeCount(Integer probeCount) {
		this.probeCount = probeCount;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
