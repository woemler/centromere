/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.etl.writer;

import com.blueprint.centromere.core.etl.PipelineComponent;
import com.blueprint.centromere.core.exceptions.DataProcessingException;
import com.blueprint.centromere.core.model.Model;

/**
 * Data import component class for writing imported {@link Model} records to a temporary file or
 * repository implementation.
 *
 * @author woemler
 */
public interface RecordWriter<T extends Model<?>> extends PipelineComponent {

    /**
     * Writes a single {@link Model} entity to the target destination.
     *
     * @param record record to be written.
     */
    void writeRecord(T record) throws DataProcessingException;

}
