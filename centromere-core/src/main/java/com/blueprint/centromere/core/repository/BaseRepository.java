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

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.ws.QueryParameterException;
import com.google.common.collect.Iterables;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.SimplePath;
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
import java.util.*;

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
	@SuppressWarnings("unchecked")
	default void customize(QuerydslBindings bindings, EntityPath entityPath){
		
		bindings.bind(String.class).all((path, strings) -> {
			List<Object> value = new ArrayList<>(strings);
			List<String> values;
			Object o = value.get(0); // only the first occurrence of a query parameter is accepted
			if (path instanceof MapPath) {
				MapPath p = (MapPath) path;
				if (o instanceof String){
					String s = (String) o;
					String key = s.split(":")[0];
					values = Arrays.asList(s.split(":")[1].split(","));
					if (values.size() > 1){
						return p.get(key).in(values);
					} else {
						return p.get(key).eq(values.get(0));
					}
				} else {
					throw new IllegalArgumentException("Cannot determine map key from non-string parameter object.");
				}
			} else if (path instanceof ListPath){
				ListPath p = (ListPath) path; 
				if (o instanceof List){
					List<Object> l = (List<Object>) o;
					return p.any().in(l);
				} else {
					return p.any().in(o);
				}
			} else {
				if (o instanceof String){
					String s = (String) o; 
					StringPath p = (StringPath) path; 
					values = Arrays.asList(s.split(","));
					if (values.size() > 1){
						return p.in(values);
					} else {
						String v = values.get(0);
						if (v.startsWith("*")) {
							v = "%" + v.replaceFirst("\\*", "") + "%";
							return p.like(v);
						} else if (v.startsWith("!*")){
							v = "%" + v.replaceFirst("!\\*", "") + "%";
							return p.notLike(v);
						} else if (v.startsWith("!")){
							v = v.replaceFirst("!", "");
							return p.ne(v);
						} else {
							return p.eq(v); // defaults to equality test
						}
					}
				} else {
					return ((SimplePath) path).eq(o);
				}
			}
		});
		
	}

}
