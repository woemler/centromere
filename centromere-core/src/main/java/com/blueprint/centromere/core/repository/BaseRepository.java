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
import com.querydsl.core.types.Predicate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> 
		extends PagingAndSortingRepository<T, ID>, QueryDslPredicateExecutor<T> {
	
	default long count(Predicate predicate){
		return Iterables.size(findAll(predicate));	
	}
	
	default Set<Object> distinct(String field){
		HashSet<Object> distinct = new HashSet<>();
		for (T obj: findAll()){
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
		for (T obj: findAll(predicate)){
			BeanWrapper wrapper = new BeanWrapperImpl(obj);
			if (!wrapper.isReadableProperty(field)){
				throw new QueryParameterException(String.format("Submitted parameter is not valid entity field: %s", field));
			}
			distinct.add(wrapper.getPropertyValue(field));
		}
		return distinct;
	}
	
}
