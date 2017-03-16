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

package com.blueprint.centromere.core.commons.repositories;

import com.blueprint.centromere.core.commons.models.User;
import com.blueprint.centromere.core.repository.ModelRepository;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author woemler
 * @since 0.5.0
 */
@RepositoryRestResource(path = "users", collectionResourceRel = "users", exported = false)
public interface UserRepository extends ModelRepository<User, String>, UserDetailsService {

	@Override 
	default User loadUserByUsername(String username) throws UsernameNotFoundException {
		PathBuilder<User> pathBuilder = new PathBuilder<>(User.class, "user");
		StringPath stringPath = pathBuilder.getString("username");
		Expression<String> constant = Expressions.constant(username);
		Predicate predicate = Expressions.predicate(Ops.EQ, stringPath, constant);
		User user = this.findOne(predicate);
		if (user == null) throw new UsernameNotFoundException(String.format("Cannot find user: %s", username));
		return user;
	}
}
