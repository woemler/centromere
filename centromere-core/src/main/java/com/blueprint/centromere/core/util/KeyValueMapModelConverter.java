/*
 * Copyright 2016 William Oemler, Blueprint Medicines
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

package com.blueprint.centromere.core.util;

import com.blueprint.centromere.core.model.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * Simple utility class for converting key-value String maps into {@link Model} objects through
 *   a JSON intermediary, using Jackson.
 * 
 * @author woemler
 * @since 0.4.1
 */
public class KeyValueMapModelConverter implements Converter<Map<String, String>, Object> {

	private final Class<? extends Model> model;
	private final ObjectMapper mapper;
	private static final Logger logger = LoggerFactory.getLogger(KeyValueMapModelConverter.class);

	public KeyValueMapModelConverter(Class<? extends Model> model) {
		this.model = model;
		this.mapper = new ObjectMapper();
	}

	public KeyValueMapModelConverter(ObjectMapper mapper, Class<? extends Model> model) {
		this.model = model;
		this.mapper = mapper;
	}

	@Override 
	public Object convert(Map<String, String> map) {
		try {
			String json = mapper.writeValueAsString(map);
			return mapper.readValue(json, model);
		} catch (Exception e){
			e.printStackTrace();
			logger.warn(String.format("[CENTROMERE] Unable to convert map to %s model object: %s", model.getName(), map.toString()));
			return null;
		}
	}
}
