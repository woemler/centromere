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

package com.blueprint.centromere.tests.core.repositories;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.core.repository.QueryCriteria;
import java.util.Collections;
import java.util.List;
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
public interface AttributeOperations<T extends Model<?>> extends ModelSupport<T> {

    @SuppressWarnings("unchecked")
    default List<T> findWithAttribute(@Param("name") String name) {
        QueryCriteria criteria = new QueryCriteria("attributes." + name, null, Evaluation.NOT_NULL);
        return (List<T>) ((ModelRepository) this).find(Collections.singleton(criteria));
    }

    @SuppressWarnings("unchecked")
    default List<T> findByAttribute(@Param("name") String name, @Param("value") String value) {
        QueryCriteria criteria = new QueryCriteria("attributes." + name, value);
        return (List<T>) ((ModelRepository) this).find(Collections.singleton(criteria));
    }

}
