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

package com.blueprint.centromere.core.repository;

import com.google.common.collect.Iterables;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.ws.QueryParameterException;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.StringPath;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface BaseRepository<T extends Model<ID>, ID extends Serializable>
		extends PagingAndSortingRepository<T, ID>, QueryDslPredicateExecutor<T>, QuerydslBinderCustomizer {
	
	default long count(Predicate predicate){
		return Iterables.size(findAll(predicate));	
	}
	
	default Set<Object> distinct(String field){
		Sort sort = new Sort(Sort.Direction.ASC, field);
		HashSet<Object> distinct = new HashSet<>();
		for (T obj: findAll(sort)){
			BeanWrapper wrapper = new BeanWrapperImpl(obj);
			if (!wrapper.isReadableProperty(field)){
				throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
			}
			distinct.add(wrapper.getPropertyValue(field));
		}
		return distinct;
	}

	default Set<Object> distinct(String field, Predicate predicate){
		HashSet<Object> distinct = new HashSet<>();
		Sort sort = new Sort(Sort.Direction.ASC, field);
		for (T obj: findAll(predicate, sort)){
			BeanWrapper wrapper = new BeanWrapperImpl(obj);
			if (!wrapper.isReadableProperty(field)){
				throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
			}
			distinct.add(wrapper.getPropertyValue(field));
		}
		return distinct;
	}

	@Override
	default void customize(QuerydslBindings bindings, EntityPath entityPath){
		bindings.bind(String.class).all((path, value) -> {
			List<String> values = new ArrayList<>(value);
			if (path instanceof MapPath) {
				String[] bits = values.get(0).split(":"); //TODO allow for multiple values via 'in()'
				return ((MapPath) path).get(bits[0]).eq(bits[1]);
			} else if (path instanceof ListPath){
				return ((ListPath) path).any().in(values);
			} else {
				return ((StringPath) path).in(values);
			}
		});
	}
}
