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

package com.blueprint.centromere.core.dataimport.impl.repositories;

import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.core.model.ModelSupport;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.List;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

/**
 * @author woemler
 */
public interface AttributeOperations<T extends Model<?>> 
    extends ModelSupport<T>, QueryDslPredicateExecutor<T> {

	@SuppressWarnings("unchecked")
	default List<T> findWithAttribute(@Param("name") String name){
		PathBuilder<T> pathBuilder = new PathBuilder<>(this.getModel(),
				this.getModel().getSimpleName().toLowerCase());
		MapPath<String, String, PathBuilder<String>> mapPath
				= pathBuilder.getMap("attributes", String.class, String.class);
		Predicate predicate = Expressions.predicate(Ops.EQ_IGNORE_CASE, mapPath.containsKey(name));
		return (List<T>) ((QueryDslPredicateExecutor) this).findAll(predicate);
	}

	@SuppressWarnings("unchecked")
	default List<T> findByAttribute(@Param("name") String name, @Param("value") String value){
    PathBuilder<T> pathBuilder = new PathBuilder<>(this.getModel(),
        this.getModel().getSimpleName().toLowerCase());
    MapPath<String, String, PathBuilder<String>> mapPath
        = pathBuilder.getMap("attributes", String.class, String.class);
    Expression<String> constant = Expressions.constant(value);
    Predicate predicate = Expressions.predicate(Ops.EQ_IGNORE_CASE, mapPath.get(name), constant);
    return (List<T>) ((QueryDslPredicateExecutor) this).findAll(predicate);
	}

}
