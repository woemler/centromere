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

package com.blueprint.centromere.core.test;

import com.blueprint.centromere.core.commons.models.Gene;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.RepositoryOperations;
import com.blueprint.centromere.core.test.model.EntrezGeneDataGenerator;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author woemler
 */

@Component
public class TestRepository implements RepositoryOperations<Gene, UUID> {

	private Map<Object, Gene> geneMap;
	private Class<Gene> model = Gene.class;
	private EntrezGeneDataGenerator dataGenerator = new EntrezGeneDataGenerator();

	public TestRepository() {
		geneMap = new HashMap<>();
		try {
			this.setGeneMap(dataGenerator.generateData(Gene.class));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public Map<Object, Gene> getGeneMap() {
		return geneMap;
	}
	
	public void setGeneMap(List<Gene> genes){
		for (Gene gene: genes){
			geneMap.put(gene.getId(), gene);
		}
	}

	public void setGeneMap(Map<Object, Gene> geneMap) {
		this.geneMap = geneMap;
	}

	@Override public Gene findOne(UUID aLong) {
		if (geneMap.containsKey(aLong)) return geneMap.get(aLong);
		return null;
	}

	@Override public boolean exists(UUID aLong) {
		return geneMap.containsKey(aLong);
	}

	@Override public Iterable<Gene> findAll() {
		return geneMap.values();
	}

	@Override public Iterable<Gene> findAll(Sort sort) {
		return geneMap.values();
	}

	@Override public Page<Gene> findAll(Pageable pageable) {
		return new PageImpl<>(new ArrayList<>(geneMap.values()), pageable, geneMap.size());
	}

	@Override public long count() {
		return geneMap.size();
	}

	@Override public Iterable<Gene> find(Iterable<QueryCriteria> queryCriterias) {
		return geneMap.values();
	}

	@Override public Iterable<Gene> find(Iterable<QueryCriteria> queryCriterias, Sort sort) {
		return geneMap.values();
	}

	@Override
	public Page<Gene> find(Iterable<QueryCriteria> queryCriterias, Pageable pageable) {
		return new PageImpl<>(new ArrayList<>(geneMap.values()), pageable, geneMap.size());
	}

	@Override public long count(Iterable<QueryCriteria> queryCriterias) {
		return geneMap.size();
	}

	@Override public Iterable<Object> distinct(String field) {
		Set<Object> values = new HashSet<>();
		for (Gene gene: geneMap.values()){
			BeanWrapper wrapper = new BeanWrapperImpl(gene);
			if (wrapper.getPropertyValue(field) != null){
				values.add(wrapper.getPropertyValue(field));
			}
		}
		return values;
	}

	@Override public Iterable<Object> distinct(String field, Iterable<QueryCriteria> queryCriterias) {
		return this.distinct(field);
	}

	@Override public <S extends Gene> S insert(S entity) {
		geneMap.put(entity.getId(), entity);
		return entity;
	}

	@Override public <S extends Gene> Iterable<S> insert(Iterable<S> entities) {
		for (Gene gene: entities){
			this.insert(gene);
		}
		return entities;
	}

	@Override public <S extends Gene> S update(S entity) {
		geneMap.put(entity.getId(), entity);
		return entity;
	}

	@Override public <S extends Gene> Iterable<S> update(Iterable<S> entities) {
		for (Gene gene: entities){
			this.update(gene);
		}
		return entities;
	}

	@Override public void delete(UUID aLong) {
		geneMap.remove(aLong);
	}

	@Override 
	public <S extends Gene> S save(S s) {
		return null;
	}

	@Override 
	public <S extends Gene> Iterable<S> save(Iterable<S> iterable) {
		return null;
	}

	@Override 
	public Iterable<Gene> findAll(Iterable<UUID> iterable) {
		return null;
	}

	@Override 
	public void delete(Gene Gene) {

	}

	@Override 
	public void delete(Iterable<? extends Gene> iterable) {

	}

	@Override 
	public void deleteAll() {
		geneMap = new HashMap<>();
	}

	@Override public Class<Gene> getModel() {
		return model;
	}

	public void setModel(Class<Gene> model) {
		this.model = model;
	}
}
