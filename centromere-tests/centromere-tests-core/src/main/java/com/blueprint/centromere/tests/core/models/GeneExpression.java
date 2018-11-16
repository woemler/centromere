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

package com.blueprint.centromere.tests.core.models;

import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import lombok.Data;

/**
 * @author woemler
 */
@Data
public abstract class GeneExpression<ID extends Serializable> implements Model<ID> {
  
  @Linked(model = DataFile.class, rel = "dataFile", field = "id")
  private ID dataFileId;

  @Linked(model = DataSet.class, rel = "dataSet", field = "id")
  private ID dataSetId;

  @Linked(model = Sample.class, rel = "sample", field = "id")
  private ID sampleId;

  @Linked(model = Gene.class, rel = "gene", field = "id")
  private ID geneId;
  
  private Double value;

}
