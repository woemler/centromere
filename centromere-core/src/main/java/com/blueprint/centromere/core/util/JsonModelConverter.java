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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

/**
 * Simple utility class for converting JSON into {@link Model} objects using Jackson.
 * 
 * @author woemler
 * @since 0.4.1
 */
public class JsonModelConverter implements Converter<String, Object> {
	
	private final ObjectMapper mapper;
	private final Class<? extends Model> model;
	private static final Logger logger = LoggerFactory.getLogger(JsonModelConverter.class);

	public JsonModelConverter(Class<? extends Model> model) {
		this.mapper = new ObjectMapper();
		this.model = model;
	}

	public JsonModelConverter(ObjectMapper mapper, Class<? extends Model> model) {
		this.mapper = mapper;
		this.model = model;
	}

	@Override 
	public Object convert(String s) {
		try {
			return mapper.readValue(s, model);
		} catch (IOException e){
			logger.warn(String.format("[CENTROMERE] Unable to convert JSON to %s model object: %s", model.getName(), s));
			e.printStackTrace();
			return null;
		}
	}
}
