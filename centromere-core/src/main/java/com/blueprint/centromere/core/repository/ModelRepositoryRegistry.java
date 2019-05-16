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

package com.blueprint.centromere.core.repository;

import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import java.util.Collection;

/**
 * Bean for registering associations between {@link Model} classes and their {@link ModelRepository}
 * implementations, and for fetching the associated types and beans.
 *
 * @author woemler
 */
public interface ModelRepositoryRegistry {

    /**
     * Tests whether the given class represents a registered {@link Model} resource.
     *
     * @param type model type
     * @return true if model is registered
     */
    boolean isRegisteredModel(Class<?> type);

    /**
     * Retrieves the {@link ModelRepository} instance associated with the registered {@link Model}
     * class.
     *
     * @param model model type
     * @return instance of the repository
     * @throws ModelRegistryException throws if problem with referencing model or repository
     */
    ModelRepository<?, ?> getRepositoryByModel(Class<? extends Model<?>> model)
        throws ModelRegistryException;

    /**
     * Retrieves a collection of all {@link ModelRepository} instances associated with all
     * registered {@link Model} classes.
     *
     * @return all registered repositories
     */
    Collection<? extends ModelRepository<?, ?>> getRegisteredModelRepositories();

    /**
     * Returns a collection of all registered {@link Model} classes.
     *
     * @return all registered models
     */
    Collection<Class<? extends Model<?>>> getRegisteredModels();

}
