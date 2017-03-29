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

package com.blueprint.centromere.core.commons.reader;

import com.blueprint.centromere.core.commons.model.Subject;
import com.blueprint.centromere.core.dataimport.impl.reader.ColumnRecordFileReader;

/**
 * 
 * 
 * @author woemler
 */
public class TcgaSubjectReader extends ColumnRecordFileReader<Subject> {

    public TcgaSubjectReader() {
        this.setModel(Subject.class);
    }

    @Override
    protected void setModelAttribute(Subject record, String attribute, String value) {
        if (attribute.equalsIgnoreCase("hybridization ref")){
            record.setName(value);
        } else if (attribute.equalsIgnoreCase("gender")){
            record.setGender(value);
        } else {
            record.addAttribute(attribute, value);
        }
        if (record.getSpecies() == null) record.setSpecies("Homo sapiens");
    }
}
