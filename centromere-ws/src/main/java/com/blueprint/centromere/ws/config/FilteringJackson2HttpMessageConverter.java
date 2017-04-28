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

package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.ws.controller.ResponseEnvelope;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Uses {@link ResponseEnvelope} to identify filterable entities and
 *   filters or includes fields based upon request parameters.
 * 
 * @author woemler 
 */

public class FilteringJackson2HttpMessageConverter extends
    TypeConstrainedMappingJackson2HttpMessageConverter {

  public FilteringJackson2HttpMessageConverter() {
    super(Object.class);
  }

  @Override
	public void setPrefixJson(boolean prefixJson) {
		super.setPrefixJson(prefixJson);
	}

	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		ObjectMapper objectMapper = getObjectMapper();
    JsonEncoding encoding = this.getJsonEncoding(outputMessage.getHeaders().getContentType());
		JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);

		try {
      
      ObjectWriter writer;
      Object content = object;
      FilterProvider filters;

			if (object instanceof ResponseEnvelope) {

				ResponseEnvelope envelope = (ResponseEnvelope) object;
				content = envelope.getEntity();
				Set<String> fieldSet = envelope.getFieldSet();
				Set<String> exclude = envelope.getExclude();
				
				if (fieldSet != null && !fieldSet.isEmpty()) {
					if (content instanceof ResourceSupport){
						fieldSet.add("content"); // Don't filter out the wrapped content.
					}
					filters = new SimpleFilterProvider()
							.addFilter("fieldFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet))
								.setFailOnUnknownId(false);
				} else if (exclude != null && !exclude.isEmpty()) {
					filters = new SimpleFilterProvider()
							.addFilter("fieldFilter", SimpleBeanPropertyFilter.serializeAllExcept(exclude))
								.setFailOnUnknownId(false);
				} else {
					filters = new SimpleFilterProvider()
							.addFilter("fieldFilter", SimpleBeanPropertyFilter.serializeAllExcept())
								.setFailOnUnknownId(false);
				}

			} else {
        filters = new SimpleFilterProvider()
            .addFilter("fieldFilter", SimpleBeanPropertyFilter.serializeAllExcept())
            .setFailOnUnknownId(false);
      }

      writer = objectMapper.writer(filters);
      writePrefix(jsonGenerator, content);
			writer.writeValue(jsonGenerator, content);
			writeSuffix(jsonGenerator, content);
			jsonGenerator.flush();

		} catch (JsonProcessingException e){
			e.printStackTrace();
			throw new HttpMessageNotWritableException("Could not write JSON: " + e.getMessage());
		}

	}
}
