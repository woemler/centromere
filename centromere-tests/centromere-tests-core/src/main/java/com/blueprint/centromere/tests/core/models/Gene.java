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

package com.blueprint.centromere.tests.core.models;

import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public abstract class Gene<I extends Serializable> implements Attributes, Model<I> {

    private String symbol;
    private Integer entrezGeneId;
    private Integer taxId;
    private String chromosome;
    @Ignored
    private String chromosomeLocation;
    private String geneType;
    @Ignored
    private String description;
    private List<String> aliases = new ArrayList<>();
    private Map<String, String> attributes = new HashMap<>();
    private Map<String, String> externalReferences = new HashMap<>();

    public void addExternalReference(String name, String value) {
        externalReferences.put(name, value);
    }

    public boolean hasExternalReference(String name) {
        return externalReferences.containsKey(name);
    }

    @Override
    public void addAttribute(String name, String value) {
        attributes.put(name, value);
    }

    @Override
    public void addAttributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }

    @Override
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    @Override
    public String getAttribute(String name) {
        return attributes.containsKey(name) ? attributes.get(name) : null;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void addAlias(String alias) {
        if (!aliases.contains(alias)) {
            this.aliases.add(alias);
        }
    }

}
