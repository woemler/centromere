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

package com.blueprint.centromere.core.config;

import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import java.util.Collection;

/**
 * @author woemler
 */
public interface ModelResourceRegistry {

  boolean isRegisteredResource(String uri) throws ModelRegistryException;
  boolean isRegisteredModel(Class<?> type) throws ModelRegistryException;
  Class<? extends Model<?>> getModelByUri(String uri) throws ModelRegistryException;
  String getUriByModel(Class<? extends Model<?>> model) throws ModelRegistryException;
  Collection<String> getRegisteredModelUris();
  Collection<Class<? extends Model<?>>> getRegisteredModels();

}
