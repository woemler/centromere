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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Model for representing biological samples in analyses.  It is presumed that a single sample can 
 *   be used in one or more analyses and be a part of one or more {@link DataSet} instances.
 *
 * @author woemler
 */
@Data
public abstract class Sample<I extends Serializable> implements Model<I>, Attributes {

    private String name;
    private String subjectId;
    private String sampleType;
    private String species;
    private String gender;
    private String tissue;
    private String histology;
    @Ignored 
    private String notes;
    private Map<String, String> attributes = new HashMap<>();
    private List<String> aliases = new ArrayList<>();

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
        return attributes.getOrDefault(name, null);
    }

    public void addAlias(String alias) {
        if (!aliases.contains(alias)) {
            aliases.add(alias);
        }
    }

    public void addAliases(Collection<String> aliases) {
        for (String alias: aliases) {
            if (!this.aliases.contains(alias)) {
                this.aliases.add(alias);
            }
        }
    }

}
