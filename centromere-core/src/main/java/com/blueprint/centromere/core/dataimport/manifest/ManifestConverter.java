/*
 * Copyright 2016 the original author or authors
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

package com.blueprint.centromere.core.dataimport.manifest;

import org.springframework.core.convert.converter.Converter;

/**
 * Spring {@link Converter} extension for converting {@link ImportManifest} instances into a target
 *   type.
 *
 * @author woemler
 * @since 0.5.0
 */
public interface ManifestConverter<T> extends Converter<ImportManifest, T> {
}
