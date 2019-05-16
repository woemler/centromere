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

package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import java.util.Collection;

/**
 * Defines the API for a registry bean that performs mapping between web service request URIs and
 * {@link Model} resources in the data warehouse.
 *
 * @author woemler
 */
public interface ModelResourceRegistry {

    /**
     * Boolean test to determine if the requested URI is associated with a model resource.
     *
     * @param uri to test
     * @return true if URI is registered
     */
    boolean isRegisteredResource(String uri);

    /**
     * Boolean test to determine if the requested type is associated with a web service resource.
     *
     * @param type class to check against the registry.
     * @return true if class is associated with a model resource
     */
    boolean isRegisteredModel(Class<?> type);

    /**
     * Fetches the {@link Model} class associated with the given URI, if one is registered.
     *
     * @param uri model URI
     * @return model class
     */
    Class<? extends Model<?>> getModelByUri(String uri) throws ModelRegistryException;

    /**
     * Returns the URI associated with the given {@link Model}, if registered.
     *
     * @param model target model class
     * @return registered URI
     */
    String getUriByModel(Class<? extends Model<?>> model) throws ModelRegistryException;

    /**
     * Returns a collection of all registered models' URIs.
     */
    Collection<String> getRegisteredModelUris();

    /**
     * Returns a collection of every registered {@link Model}.
     */
    Collection<Class<? extends Model<?>>> getRegisteredModels();

}
