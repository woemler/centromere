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

package org.oncoblocks.centromere.web.controller;

import org.oncoblocks.centromere.core.config.ModelRegistry;
import org.oncoblocks.centromere.core.model.ForeignKey;
import org.oncoblocks.centromere.core.model.Model;
import org.oncoblocks.centromere.core.model.ModelAttributes;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Extension of Spring HATEOAS's {@link ResourceAssemblerSupport}, which automatically builds self
 *   links, based upon the {@link Model} class's `getId()` method signature, and by inferring 
 *   related models by fields annotated with {@link ForeignKey}.
 * 
 * @author woemler
 */
public class MappingModelResourceAssembler 
		extends ResourceAssemblerSupport<Model, FilterableResource> {

	private final ModelRegistry registry;
	private static final String ROOT_URL = "/api";
	
	public MappingModelResourceAssembler(ModelRegistry registry, Class<?> controllerClass){
		super(controllerClass, FilterableResource.class);
		this.registry = registry;
	}
	
	private String getModelUri(Class<? extends Model> model){
		String uri = model.getName().toLowerCase();
		if (model.isAnnotationPresent(ModelAttributes.class)){
			ModelAttributes modelAttributes = model.getAnnotation(ModelAttributes.class);
			if (!modelAttributes.uri().equals("")){
				uri = modelAttributes.uri();
			}
		}
		return ROOT_URL + "/" + uri;
	}
	
	/**
	 * Converts a {@link Model} object into a {@link FilterableResource}, adding the appropriate links.
	 * 
 	 * @param t
	 * @return
	 */
	public FilterableResource toResource(Model t) {
		FilterableResource<Model> resource = new FilterableResource<>(t);
		resource.add(new Link(getModelUri(t.getClass()), "self"));
		List<Link> links = addLinks(new ArrayList<>());
		links.addAll(this.addForeignKeyLinks(t));
		resource.add(links);
		return resource;
	}

	/**
	 * Inspects the target {@link Model} class for {@link ForeignKey} annotations, and creates links
	 *   based upon the inferred relationship and field names.
	 * 
	 * @param t
	 * @return
	 */
	private List<Link> addForeignKeyLinks(Model t){
		List<Link> links = new ArrayList<>();
		for (Field field: t.getClass().getDeclaredFields()){
			ForeignKey fk = field.getAnnotation(ForeignKey.class);
			if (fk == null) continue;
			if (fk.model() == null) throw new RuntimeException(String.format("ForeignKey annotation for " 
					+ "class %s does not contain any class reference!", t.getClass().getName()));
			Class<? extends Model> fkClass = fk.model();
			String relName = fk.rel().equals("") ? field.getName() : fk.rel();
			String fieldName = fk.field().equals("") ? field.getName() : fk.field();
			if (!Model.class.isAssignableFrom(fkClass)) continue;
			Link link = null;
			try {
				field.setAccessible(true);
				if (fk.relationship().equals(ForeignKey.Relationship.MANY_TO_ONE)
						&& (!field.getType().isArray() && !Collection.class
						.isAssignableFrom(field.getType()))) {
					link = new Link(getModelUri(fkClass) + "/" + field.get(t), relName);
				} else if (fk.relationship().equals(ForeignKey.Relationship.ONE_TO_MANY)
						&& (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType()))) {
					Map<String,Object> map = new HashMap<>();
					map.put(fieldName, field.get(t));
					link = new Link(getModelUri(fkClass) + "?" + fieldName + "=" + field.get(t).toString(), relName);
				} else if (fk.relationship().equals(ForeignKey.Relationship.MANY_TO_MANY)
						&& (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType()))){
					Map<String,Object> map = new HashMap<>();
					map.put(fieldName, field.get(t));
					link = new Link(getModelUri(fkClass) + "?" + fieldName + "=" + field.get(t).toString(), relName);
				} else {
					throw new RuntimeException(String.format("Unable to determine correct link format for " 
							+ "field %s of class %s", field.getName(), t.getClass().getName()));
				}
			} catch (IllegalAccessException e){
				e.printStackTrace();
			}
			if (link != null) links.add(link);
		}
		return links;
	}
	
	private List<Link> addLinks(List<Link> links){
		return links;
	}
}
