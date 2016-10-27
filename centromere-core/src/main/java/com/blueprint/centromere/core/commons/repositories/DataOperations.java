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

package com.blueprint.centromere.core.commons.repositories;

import com.blueprint.centromere.core.commons.models.DataFile;
import com.blueprint.centromere.core.commons.models.Sample;
import com.blueprint.centromere.core.model.Model;

import java.io.Serializable;
import java.util.List;

/**
 * @author woemler
 */
public interface DataOperations<T extends Model<?>> {
	<S extends DataFile<I>, I extends Serializable> List<T> findByDataFileId(I dataFileId);
	<S extends Sample, I extends Serializable> List<T> findBySampleId(I sampleId);
}
