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
import com.blueprint.centromere.core.repository.MongoOperationsAware;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author woemler
 * @since 0.5.0
 */
@RepositoryRestResource(path = "users", collectionResourceRel = "users", exported = false)
public interface UserRepository extends ModelRepository<User, String>, UserDetailsService,
    MongoOperationsAware {

	@Override 
	default User loadUserByUsername(String username) throws UsernameNotFoundException {
		Query query = new Query(Criteria.where("username").is(username));
		User user = getMongoOperations().findOne(query, getModel());
		if (user == null){
		  throw new UsernameNotFoundException("User not found: " + username);
    }
		return user;
		
	}
}
