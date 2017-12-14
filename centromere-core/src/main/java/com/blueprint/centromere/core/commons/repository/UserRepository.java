/*
 * Copyright 2017 the original author or authors
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

package com.blueprint.centromere.core.commons.repository;

import com.blueprint.centromere.core.commons.model.User;
import com.blueprint.centromere.core.repository.ModelRepository;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author woemler
 * @since 0.5.0
 */
@NoRepositoryBean
public interface UserRepository<T extends User<ID>, ID extends Serializable> 
    extends ModelRepository<T, ID>, UserDetailsService {

  Optional<T> findByUsername(String username);

	@Override 
	default T loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<T> optional = findByUsername(username);
		return optional.isPresent() ? optional.get() : null;
	}
}
