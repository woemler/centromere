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
import java.io.Serializable;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author woemler
 */
@Data
public abstract class GeneData<ID extends Serializable> extends SampleData<ID> {

  @Indexed
  @Linked(model = Gene.class, rel = "gene", field = "geneId")
  private String geneId;

}
