package com.blueprint.centromere.tests.core.models;

import com.blueprint.centromere.core.model.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author woemler
 */
@Data
public abstract class User<ID extends Serializable> implements UserDetails, Model<ID> {

  private String username;
  private String password;
  private List<? extends GrantedAuthority> authorities = new ArrayList<>();
  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean credentialsNonExpired;
  private boolean enabled;
  
}
