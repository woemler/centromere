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

import java.io.Serializable;

/**
 * 
 * @author woemler
 * @since 0.4.3
 */
public interface Data {
	<T extends DataFile<I>, I extends Serializable> I getDataFileId();
	<T extends DataFile<I>, I extends Serializable> void setDataFileMetadata(T dataFile);
	<T extends Sample<I>, I extends Serializable> I getSampleId();
	<T extends Sample<I>, I extends Serializable> void setSampleMetadata(T sample);
}
