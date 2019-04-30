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

package com.blueprint.centromere.core.model;

/**
 * Ensures that component classes that support {@link Model} classes have accessible type
 * definitions.
 *
 * @author woemler
 * @since 0.4.1
 */
public interface ModelSupport<T extends Model<?>> {

    /**
     * Returns the model class reference.
     *
     * @return model class
     */
    Class<T> getModel();

}
