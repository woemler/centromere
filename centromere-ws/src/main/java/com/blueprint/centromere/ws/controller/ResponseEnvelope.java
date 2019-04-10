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

package com.blueprint.centromere.ws.controller;

import com.blueprint.centromere.ws.config.FilteringJackson2HttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper for GET request responses to allow for field filtering via 
 *   {@link FilteringJackson2HttpMessageConverter}
 *
 * @author woemler
 */
public class ResponseEnvelope {

    private final Set<String> includedFields;
    private final Set<String> excludedFields;
    private final Object entity;

    public ResponseEnvelope(Object entity) {
        this.entity = entity;
        this.includedFields = new HashSet<>();
        this.excludedFields = new HashSet<>();
    }

    public ResponseEnvelope(Object entity, Set<String> includedFields, Set<String> excludedFields) {
        this.includedFields = includedFields;
        this.excludedFields = excludedFields;
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }

    @JsonIgnore
    public Set<String> getIncludedFields() {
        return includedFields;
    }

    @JsonIgnore
    public Set<String> getExcludedFields() {
        return excludedFields;
    }

}
