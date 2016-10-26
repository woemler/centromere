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

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.RepositoryOperations;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.*;

/**
 * @author woemler
 */
public class EmptyRepository<T extends Model<ID>, ID extends Serializable> 
		implements RepositoryOperations<T, ID> {
	
	private Map<ID, T> data = new HashMap<>();
	private Class<T> model;

	public EmptyRepository(Class<T> model) {
		this.model = model;
	}

	@Override public Iterable<T> find(Iterable<QueryCriteria> queryCriterias) {
		return data.values();
	}

	@Override public Iterable<T> find(Iterable<QueryCriteria> queryCriterias, Sort sort) {
		return data.values();
	}

	@Override public Page<T> find(Iterable<QueryCriteria> queryCriterias, Pageable pageable) {
		return new PageImpl<T>(new ArrayList<>(data.values()), pageable, data.values().size());
	}

	@Override public long count(Iterable<QueryCriteria> queryCriterias) {
		return data.values().size();
	}

	@Override public Iterable<Object> distinct(String field) {
		Set<Object> values = new HashSet<>();
		for (T entity: data.values()){
			BeanWrapper wrapper = new BeanWrapperImpl(entity);
			if (wrapper.getPropertyValue(field) != null){
				values.add(wrapper.getPropertyValue(field));
			}
		}
		return values;
	}

	@Override public Iterable<Object> distinct(String field, Iterable<QueryCriteria> queryCriterias) {
		Set<Object> values = new HashSet<>();
		for (T entity: data.values()){
			BeanWrapper wrapper = new BeanWrapperImpl(entity);
			if (wrapper.getPropertyValue(field) != null){
				values.add(wrapper.getPropertyValue(field));
			}
		}
		return values;
	}

	@Override public <S extends T> S insert(S entity) {
		data.put(entity.getId(), entity);
		return entity;
	}

	@Override public <S extends T> Iterable<S> insert(Iterable<S> entities) {
		List<S> datas = new ArrayList<>();
		for (S entity: entities){
			datas.add(insert(entity));
		}
		return datas;
	}

	@Override public <S extends T> S update(S entity) {
		data.put(entity.getId(), entity);
		return entity;
	}

	@Override public <S extends T> Iterable<S> update(Iterable<S> entities) {
		List<S> datas = new ArrayList<>();
		for (S entity: entities){
			datas.add(insert(entity));
		}
		return datas;
	}

	@Override public Iterable<T> findAll(Sort sort) {
		return null;
	}

	@Override public Page<T> findAll(Pageable pageable) {
		return new PageImpl<T>(new ArrayList<>(data.values()), pageable, data.values().size());
	}

	@Override public <S extends T> S save(S s) {
		data.put(s.getId(), s);
		return s;
	}

	@Override public <S extends T> Iterable<S> save(Iterable<S> iterable) {
		List<S> datas = new ArrayList<>();
		for (S entity: iterable){
			datas.add(insert(entity));
		}
		return datas;
	}

	@Override public T findOne(ID id) {
		return data.containsKey(id) ? data.get(id) : null;
	}

	@Override public boolean exists(ID id) {
		return data.containsKey(id);
	}

	@Override public Iterable<T> findAll() {
		return data.values();
	}

	@Override public Iterable<T> findAll(Iterable<ID> iterable) {
		List<T> datas = new ArrayList<>();
		for (ID id: iterable){
			if (data.containsKey(id)){
				datas.add(data.get(id));
			}
		}
		return datas;
	}

	@Override public long count() {
		return data.size();
	}

	@Override public void delete(ID id) {
		data.remove(id);
	}

	@Override public void delete(T t) {
		data.remove(t.getId());
	}

	@Override public void delete(Iterable<? extends T> iterable) {
		for (T t: iterable){
			data.remove(t.getId());
		}
	}

	@Override public void deleteAll() {
		data = new HashMap<>();
	}

	@Override public Class<T> getModel() {
		return model;
	}

	@Override public void setModel(Class<T> model) {
		this.model = model;
	}

	public List<T> getData() {
		return new ArrayList<>(data.values());
	}

	public void setData(List<T> data) {
		for (T t: data){
			this.data.put(t.getId(), t);
		}
	}
}
