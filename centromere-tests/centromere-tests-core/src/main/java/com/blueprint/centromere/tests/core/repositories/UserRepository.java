package com.blueprint.centromere.tests.core.repositories;

import com.blueprint.centromere.core.repository.ModelRepository;
import com.blueprint.centromere.tests.core.models.User;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author woemler
 */
@NoRepositoryBean
public interface UserRepository<T extends User<I>, I extends Serializable>
    extends ModelRepository<T, I>, UserDetailsService {

    Optional<User> findByUsername(String username);

    default UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return findByUsername(s).orElse(null);
    }

}
