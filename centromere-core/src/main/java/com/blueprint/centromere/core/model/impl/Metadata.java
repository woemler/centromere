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

import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Base class for non-core-metadata {@link Model} classes.  The {@code dataFileId} and {@code dataSetId}
 *   fields allow records associated with related entities to be looked up and modified.
 * 
 * @since 0.6.0
 * @author woemler
 */
@Data
public abstract class Metadata<ID extends Serializable> implements Model<ID> {

  @Indexed
  @Linked(model = DataSource.class, rel = "dataSource", field = "dataSourceId")
  private String dataSourceId;

  @Indexed
  @Linked(model = DataSet.class, rel = "dataSet", field = "dataSetId")
  private String dataSetId;
  
}
