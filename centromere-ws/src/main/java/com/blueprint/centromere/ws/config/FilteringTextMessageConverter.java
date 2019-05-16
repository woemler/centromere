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

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.ws.controller.FilterableResource;
import com.blueprint.centromere.ws.controller.ResponseEnvelope;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Message converter that takes web service response data and converts it to delimited-text in a
 * tabular format.  Supports field filtering using {@link ResponseEnvelope} attributes.
 *
 * @author woemler
 */
public class FilteringTextMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final MediaType mediaType;
    private String delimiter = "\t";

    public FilteringTextMessageConverter(MediaType supportedMediaType, String delimiter) {
        super(supportedMediaType);
        this.delimiter = delimiter;
        this.mediaType = supportedMediaType;
    }

    public FilteringTextMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
        this.mediaType = supportedMediaType;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return Resource.class.equals(aClass)
            || FilterableResource.class.equals(aClass)
            || Resources.class.equals(aClass)
            || PagedResources.class.equals(aClass)
            || ResourceSupport.class.equals(aClass)
            || Model.class.isAssignableFrom(aClass)
            || ResponseEnvelope.class.equals(aClass);
    }

    @Override
    protected Object readInternal(Class<?> aClass, HttpInputMessage httpInputMessage)
        throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING", justification = "Default of UTF16 is fine.")
    protected void writeInternal(Object o, HttpOutputMessage httpOutputMessage)
        throws IOException, HttpMessageNotWritableException {

        httpOutputMessage.getHeaders().setContentType(this.mediaType);
        OutputStream out = httpOutputMessage.getBody();
        PrintWriter writer = new PrintWriter(out);
        boolean showHeader = true;
        Set<String> includedFields = new HashSet<>();
        Set<String> excludedFields = new HashSet<>();
        Object obj = o;

        // If object is a ResponseEnvelope, get the wrapped object
        if (obj.getClass().equals(ResponseEnvelope.class)) {
            includedFields = ((ResponseEnvelope) obj).getIncludedFields();
            excludedFields = ((ResponseEnvelope) obj).getExcludedFields();
            obj = ((ResponseEnvelope) obj).getEntity();
        }
        if (obj.getClass().equals(PageImpl.class)) {
            obj = ((Page) obj).getContent();
        }

        // If the object is not a collection, add it to one for easy iteration
        if (!(obj instanceof Collection<?>)) {
            obj = Arrays.asList(obj);
        }

        // Iterate through the object collection
        for (Object entity : (Collection<?>) obj) {

            String entityString;
            Class<?> currentClass;

            try {

                StringBuilder buffer = new StringBuilder();

                // Header line
                if (showHeader) {

                    currentClass = entity.getClass();

                    while (currentClass != null) {

                        for (Field field : currentClass.getDeclaredFields()) {
                            if (includedFields != null && !includedFields.isEmpty()) {
                                if (includedFields.contains(field.getName())) {
                                    buffer.append(field.getName()).append(delimiter);
                                }
                            } else if (excludedFields != null && !excludedFields.isEmpty()) {
                                if (!excludedFields.contains(field.getName())) {
                                    buffer.append(field.getName()).append(delimiter);
                                }
                            } else {
                                buffer.append(field.getName()).append(delimiter);
                            }
                        }

                        currentClass = currentClass.getSuperclass();

                    }

                    buffer.append("\n");

                }

                // Write the object data row
                currentClass = entity.getClass();

                while (currentClass != null) {

                    for (Field field : currentClass.getDeclaredFields()) {
                        field.setAccessible(true);
                        if (includedFields != null && !includedFields.isEmpty()) {
                            if (includedFields.contains(field.getName())) {
                                buffer.append(field.get(entity)).append(delimiter);
                            }
                        } else if (excludedFields != null && !excludedFields.isEmpty()) {
                            if (!excludedFields.contains(field.getName())) {
                                buffer.append(field.get(entity)).append(delimiter);
                            }
                        } else {
                            buffer.append(field.get(entity)).append(delimiter);
                        }
                    }

                    currentClass = currentClass.getSuperclass();

                }

                buffer.append("\n");
                entityString = buffer.toString();

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                entityString = "# Invalid record.";
            }

            writer.write(entityString);
            showHeader = false;

        }

        writer.close();

    }

}
