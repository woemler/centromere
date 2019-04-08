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

package com.blueprint.centromere.core.etl.processor;

import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.util.Collection;

/**
 * Bean for registering associations between {@link Model} classes and their {@link ModelRepository}
 *   implementations, and for fetching the associated types and beans.
 *
 * @author woemler
 */
public interface ModelDataProcessorRegistry<T extends DataProcessor<?>> {

    /**
     * Tests whether the given class represents a registered {@link Model} resource. 
     *
     * @param type model type
     * @return true if model is registered
     * @throws ModelRegistryException throws if problem with checking registry
     */
    boolean isRegisteredModel(Class<? extends Model<?>> type) throws ModelRegistryException;

    /**
     * Tests whether the given data type is associated with a registered {@link DataProcessor}. 
     *
     * @param dataType data type string
     * @return true if data type is registered
     * @throws ModelRegistryException thrown if there is an issue checking the registry.
     */
    boolean isRegisteredDataType(String dataType) throws ModelRegistryException;

    /**
     * Retrieves a collection of {@link DataProcessor} instances associated with the registered 
     *   {@link Model} class.
     *
     * @param model model type
     * @return instance of the repository
     * @throws ModelRegistryException throws if problem with referencing model or repository
     */
    Collection<T> getDataProcessorsByModel(Class<? extends Model<?>> model)
        throws ModelRegistryException;

    /**
     * Retrieves a  {@link DataProcessor} instance associated with the registered data type.
     *
     * @param dataType data type to check the registry against
     * @return instance of the repository
     * @throws ModelRegistryException thrown if an issue checking the registry
     */
    T getProcessorByDataType(String dataType) throws ModelRegistryException;

    /**
     * Retrieves a collection of all {@link DataProcessor} instances associated with all registered 
     *   {@link Model} classes and data types.
     *
     * @return all registered repositories
     */
    Collection<T> getRegisteredDataProcessors();

    /**
     * Returns a collection of all registered {@link Model} classes.
     *
     * @return all registered models
     */
    Collection<Class<? extends Model<?>>> getRegisteredModels();

    /**
     * Returns a collection of all registered data type strings.
     *
     * @return
     */
    Collection<String> getRegisteredDataTypes();

}
