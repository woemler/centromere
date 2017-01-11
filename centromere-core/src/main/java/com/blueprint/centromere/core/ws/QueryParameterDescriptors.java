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

package com.blueprint.centromere.core.ws;

import java.util.ArrayList;
import java.util.List;

/**
 * @author woemler
 */
@Deprecated
public class QueryParameterDescriptors {

    private List<QueryParameterDescriptor> descriptors = new ArrayList<>();

    public QueryParameterDescriptors() {
    }

    public QueryParameterDescriptors(List<QueryParameterDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public List<QueryParameterDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<QueryParameterDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public void add(QueryParameterDescriptor descriptor){
        this.descriptors.add(descriptor);
    }

    public boolean matches(String param){
        for (QueryParameterDescriptor descriptor: descriptors){
            if (descriptor.parameterNameMatches(param)) return true;
        }
        return false;
    }

    public QueryParameterDescriptor get(String param){
        for (QueryParameterDescriptor descriptor: descriptors){
            if (descriptor.parameterNameMatches(param)) return descriptor;
        }
        return null;
    }
}
