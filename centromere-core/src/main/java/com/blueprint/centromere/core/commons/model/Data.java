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

import com.blueprint.centromere.core.model.Linked;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Base class for modeling processed genomic data.  Assumes that each record has an associated 
 *   {@link DataFile}, {@link DataSet}, {@link Sample}, and {@link Gene}.  
 * 
 * @author woemler
 * @since 0.4.3
 */
@lombok.Data
public abstract class Data {

	@Indexed
	@Linked(model = Sample.class, rel = "sample", field = "sampleId")
	private String sampleId;

	@Indexed
	@Linked(model = DataFile.class, rel = "dataFile", field = "dataFileId")
	private String dataFileId;
	
	@Indexed
	@Linked(model = DataSet.class, rel = "dataSet", field = "dataSetId")
  private String dataSetId;

	@Indexed
	@Linked(model = Gene.class, rel = "gene", field = "geneId")
	private String geneId;
  
}
